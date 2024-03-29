package dao

import java.sql.Date

import models.Blog
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import scala.concurrent.Future

trait BlogsComponent {
  self: HasDatabaseConfig[JdbcProfile] =>

  class Blogs(tag: Tag) extends Table[Blog](tag, Some("public_bubble"), "blog") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def author = column[String]("author")
    def intro = column[String]("intro")
    def content = column[String]("content")
    def publishDate = column[Date]("publish_date")
    def image1Url = column[Option[String]]("image_1_url")

    /* It is possible to define a mapped table
    that uses a custom type for its * projection
    by adding a bi-directional mapping with the <> operator:

    The <> operator is optimized for case classes*/


    // the ? method lifts the column into an option
    def * = (id.?, title, author, intro, content, publishDate, image1Url) <> ((Blog.apply _).tupled, Blog.unapply)
    // the default projection is Blog

  }

}

class SlickBlogDao extends HasDatabaseConfig[JdbcProfile] with BlogDao with BlogsComponent  {

  private val blogs = TableQuery[Blogs]

  override protected val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  override def update(blog: Blog): Unit = {
    val query = for {b <- blogs if b.id === blog.id} yield
    (b.title, b.intro, b.author, b.content, b.publishDate)

    db.run(query.update(blog.title, blog.intro, blog.author, blog.content, blog.publishDate))
  }

  override def addImage(id: Long, url: String): Future[Boolean] = {
    val q = for { b <- blogs if b.id === id } yield b.image1Url
    val updateImage = q.update(Some(url))
    db.run(updateImage).map(_ == 1)
  }

  override def delete(id: Long):  Future[Int] = {
      val findById = blogs.filter(_.id === id)
      dbConfig.db.run(findById.delete)
  }

  override def findById(blogId: Long): Future[Option[Blog]] = db.run(blogs.filter(_.id === blogId).result.headOption)

  override def create(blog: Blog) = {

         dbConfig.db.run(blogs += blog)


  }

  override def sortedById : Future[Seq[Blog]] = db.run(blogs.result)
  override def sortedByDate : Future[Seq[Blog]] = {
    val query  =     blogs.sortBy(_.publishDate.desc);

    dbConfig.db.run(query.result)
  }

}
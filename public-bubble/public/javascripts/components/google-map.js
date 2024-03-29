var GoogleMap = React.createClass({

    getInitialState : function() {return {mapKey : null}},

    initDirections : function() {
        console.log("Reading state")
        console.log(this.state)
        var directionsDisplay = new google.maps.DirectionsRenderer;
        directionsDisplay.setMap();
    },

    addMarker : function(latLng, map) {

        var marker = new google.maps.Marker({
            position: latLng,
            map: map
        });
                
    },  

    componentDidMount :  function() {
        var markerPosition = {lat: this.props.lat, lng: this.props.lng}
        console.log("component has mounted")
        var map = new google.maps.Map(React.findDOMNode(this.refs.map_canvas), 
                            {
                             minZoom: 3,
                             zoom: 13,
                             center: markerPosition,
                             mapTypeControl : false
                            });

        this.addMarker(markerPosition, map)
        this.setState({mapKey : map}, function() {this.initDirections()});
    },

    render: function() {
    
    return <div ref="map_canvas" className="bigMap"></div>
            }
});
CREATE USER playlocal WITH PASSWORD 'password';
GRANT ALL ON ALL TABLES IN SCHEMA public TO playlocal;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO playlocal;

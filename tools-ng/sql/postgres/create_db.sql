-- Usage: psql --username=postgres --file=create_db.sql
--        createlang --username=ccm -d ccm plpgsql

CREATE USER ccm PASSWORD 'ccm';
CREATE DATABASE ccm WITH ENCODING='UTF8' OWNER=ccm;

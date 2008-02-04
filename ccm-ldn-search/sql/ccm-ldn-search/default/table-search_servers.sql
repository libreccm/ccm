create table search_servers (
  server_id integer not null
    constraint search_servers_server_id_pk primary key
    constraint search_servers_server_id_fk references
    acs_objects (object_id) on delete cascade,
  hostname varchar(250) not null
    constraint search_servers_hostname_un unique,
  title varchar(50) not null
);

create table sc_shortcuts (
       shortcut_id      integer
                        constraint sc_shortcuts_pk
                        primary key,              
       url_key          varchar(1000) not null
                        constraint sc_shortcuts_key_unique unique,
       redirect         varchar(1000) not null
);

create table nav_quick_links_bak as select * from nav_quick_links;

drop table nav_quick_links;

create table nav_quick_links (
    link_id INTEGER not null
        constraint nav_quic_links_link_id_p_zefwe
          primary key,
        -- referential constraint for link_id deferred due to circular dependencies
    title VARCHAR(300) not null,
    url VARCHAR(300) not null,
    description VARCHAR(4000),
    icon VARCHAR(300)
);
alter table nav_quick_links add 
    constraint nav_quic_links_link_id_f_svehq foreign key (link_id)
      references acs_objects(object_id);

insert into nav_quick_links (link_id, title, url) select link_id, description, url from nav_quick_links_bak;

drop table nav_quick_links_bak;

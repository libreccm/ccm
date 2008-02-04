create table search_sponsored_links (
    link_id INTEGER not null
        constraint sear_sponso_lin_lin_id_p_kftdq
          primary key,
        -- referential constraint for link_id deferred due to circular dependencies
    title VARCHAR(1000) not null,
    term VARCHAR(100) not null,
    url VARCHAR(2000) not null
);
alter table search_sponsored_links add 
    constraint sear_sponso_lin_lin_id_f_d2r7n foreign key (link_id)
      references acs_objects(object_id);

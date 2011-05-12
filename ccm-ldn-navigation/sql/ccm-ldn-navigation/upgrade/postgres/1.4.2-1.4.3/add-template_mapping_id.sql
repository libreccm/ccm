alter table nav_template_cat_map add
    map_id INTEGER;

update nav_template_cat_map set
    map_id = nextval('acs_object_id_seq');

alter table nav_template_cat_map
    drop constraint nav_tem_cat_map_cat_id_p_9byjj;

alter table nav_template_cat_map
    alter column map_id set not null;

alter table nav_template_cat_map
    add constraint nav_tem_cat_map_map_id_p_tpehj primary key ( map_id );

alter table nav_template_cat_map
    add constraint nav_tem_cat_map_cat_id_u_tgxia
        unique(category_id, use_context);

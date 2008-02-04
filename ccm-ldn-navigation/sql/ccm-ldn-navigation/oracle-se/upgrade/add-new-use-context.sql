alter table nav_template_cat_map
    add use_context varchar(100);

alter table nav_template_cat_map
    drop constraint nav_tem_cat_map_cat_id_u_6g2ku;

alter table nav_template_cat_map
    add constraint nav_tem_cat_map_cat_id_u_b_e6b
    unique(category_id, dispatcher_context, use_context);

update nav_template_cat_map
    set use_context = 'default';

alter table nav_template_cat_map
    modify use_context not null;

alter table nav_template_cat_map
    rename column use_context to dispatcher_context;

alter table nav_template_cat_map
    drop constraint nav_tem_cat_map_cat_id_u_tgxia;

alter table nav_template_cat_map
    add constraint nav_tem_cat_map_cat_id_u_6g2ku
    unique(category_id, dispatcher_context);

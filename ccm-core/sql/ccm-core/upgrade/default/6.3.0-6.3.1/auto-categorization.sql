alter table cat_object_category_map add auto_p char(1) default '0'
    constraint cat_obj_map_auto_p_ck
    check (auto_p in ('0','1')) ;

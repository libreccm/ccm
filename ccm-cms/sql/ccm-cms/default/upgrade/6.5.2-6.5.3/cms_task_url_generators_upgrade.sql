alter table cms_task_url_generators
    add content_type INTEGER;

alter table cms_task_url_generators 
    add constraint cms_tas_url_gen_con_ty_f_lz1y5 foreign key (content_type)
        references content_types(type_id);

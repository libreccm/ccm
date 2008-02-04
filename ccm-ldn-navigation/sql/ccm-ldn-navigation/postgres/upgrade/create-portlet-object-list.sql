create table portlet_object_list (
    portlet_id INTEGER not null
        constraint port_obje_lis_portl_id_p_l26mq
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    base_object_type VARCHAR(200) not null,
    specific_object_type VARCHAR(200),
    ordering VARCHAR(200) not null,
    xml_attributes VARCHAR(200),
    count INTEGER not null,
    filter_category_id INTEGER,
        -- referential constraint for filter_category_id deferred due to circular dependencies
    check_permissons BOOLEAN not null,
    descend_categories BOOLEAN not null,
    exclude_index_objects BOOLEAN not null
);
alter table portlet_object_list add 
    constraint port_obje_lis_portl_id_f_sf95h foreign key (portlet_id)
      references portlets(portlet_id);
alter table portlet_object_list add 
    constraint port_obj_lis_fil_cat_i_f_328ql foreign key (filter_category_id)
      references cat_categories(category_id);

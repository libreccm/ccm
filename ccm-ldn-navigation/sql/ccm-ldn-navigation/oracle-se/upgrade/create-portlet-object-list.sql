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
    check_permissons CHAR(1) not null
        constraint port_obj_lis_che_permi_c_blam_
          check(check_permissons in ('0', '1')),
    descend_categories CHAR(1) not null
        constraint port_obj_lis_des_categ_c_j6_us
          check(descend_categories in ('0', '1')),
    exclude_index_objects CHAR(1) not null
        constraint port_obj_lis_exc_ind_o_c_pmxhl
          check(exclude_index_objects in ('0', '1'))
);
alter table portlet_object_list add 
    constraint port_obje_lis_portl_id_f_sf95h foreign key (portlet_id)
      references portlets(portlet_id);
alter table portlet_object_list add 
    constraint port_obj_lis_fil_cat_i_f_328ql foreign key (filter_category_id)
      references cat_categories(category_id);

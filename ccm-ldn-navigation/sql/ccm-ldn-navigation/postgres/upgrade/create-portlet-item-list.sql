create table portlet_item_list (
    portlet_id INTEGER not null
        constraint portl_ite_lis_portl_id_p_5gokc
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    version VARCHAR(4000)
);
alter table portlet_item_list add 
    constraint portl_ite_lis_portl_id_f_dcwxm foreign key (portlet_id)
      references portlet_object_list(portlet_id);


create table portlet_content_directory (
    portlet_id INTEGER not null
        constraint port_con_direc_port_id_p_f5k1_
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    depth INTEGER not null,
    layout VARCHAR(10) not null,
    root_id INTEGER
        -- referential constraint for root_id deferred due to circular dependencies
);

alter table portlet_content_directory add 
    constraint port_con_direc_port_id_f_9kbkn foreign key (portlet_id)
      references portlets(portlet_id) on delete cascade;
alter table portlet_content_directory add 
    constraint port_con_direct_roo_id_f_fvrr7 foreign key (root_id)
      references cat_categories(category_id);

insert into portlet_content_directory 
  (portlet_id, root_id, layout, depth)
  select object_id, (select category_id
      from cat_categories
     where name = 'Navigation'), 
     'grid', 2
    from acs_objects 
   where object_type = 'com.arsdigita.london.portal.portlet.ContentDirectoryPortlet';


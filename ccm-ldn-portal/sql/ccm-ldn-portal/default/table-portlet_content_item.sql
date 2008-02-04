create table portlet_content_item (
    portlet_id INTEGER not null
        constraint port_cont_ite_portl_id_p_fikuf
          primary key,
    item_id INTEGER
);

alter table portlet_content_item add 
    constraint port_cont_ite_portl_id_f_n19z_ foreign key (portlet_id)
      references portlets(portlet_id) on delete cascade;
alter table portlet_content_item add 
    constraint portl_conte_ite_ite_id_f_aft9p foreign key (item_id)
      references cms_items(item_id) on delete set null;

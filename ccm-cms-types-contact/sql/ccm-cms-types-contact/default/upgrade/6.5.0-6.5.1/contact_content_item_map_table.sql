create table contact_content_item_map (
    item_id INTEGER not null
        constraint cont_con_ite_map_ite_i_p_scqe9
          primary key,
        -- referential constraint for item_id deferred due to circular dependencies
    contact_id INTEGER not null
        -- referential constraint for contact_id deferred due to circular dependencies
);


alter table contact_content_item_map add
    constraint cont_con_ite_map_con_i_f_lanid foreign key (contact_id)
      references ct_contacts(contact_id);
alter table contact_content_item_map add
    constraint cont_con_ite_map_ite_i_f_fr0po foreign key (item_id)
      references cms_items(item_id);

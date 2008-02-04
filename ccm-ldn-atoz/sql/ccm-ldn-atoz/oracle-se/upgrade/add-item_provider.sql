create table atoz_item_provider (
    provider_id INTEGER not null
        constraint atoz_ite_prov_provi_id_p_8kr86
          primary key,
        -- referential constraint for provider_id deferred due to circular dependencies
    category_id INTEGER not null,
        -- referential constraint for category_id deferred due to circular dependencies
    load_paths VARCHAR(4000)
);

alter table atoz_item_provider add 
    constraint atoz_ite_prov_categ_id_f_dfhl8 foreign key (category_id)
      references cat_categories(category_id);
alter table atoz_item_provider add 
    constraint atoz_ite_prov_provi_id_f_7b7k7 foreign key (provider_id)
      references atoz_provider(provider_id);

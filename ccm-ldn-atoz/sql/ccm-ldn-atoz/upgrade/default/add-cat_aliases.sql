create table atoz_cat_aliases (
    object_id NUMERIC not null
        constraint atoz_cat_alias_obje_id_p_5h3fv
          primary key,
    provider_id INTEGER not null,
        -- referential constraint for provider_id deferred due to circular dependencies
    category_id INTEGER not null,
        -- referential constraint for category_id deferred due to circular dependencies
    letter CHAR(1) not null,
    title VARCHAR(200) not null
);

alter table atoz_cat_aliases add 
    constraint atoz_cat_alia_categ_id_f_smlu2 foreign key (category_id)
      references cat_categories(category_id);
alter table atoz_cat_aliases add 
    constraint atoz_cat_alia_provi_id_f_c9mnf foreign key (provider_id)
      references atoz_cat_provider(provider_id);

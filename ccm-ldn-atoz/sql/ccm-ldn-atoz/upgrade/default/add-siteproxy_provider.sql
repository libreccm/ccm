create table atoz_siteproxy_provider (
    provider_id INTEGER not null
        constraint atoz_site_prov_prov_id_p_p1eu6
          primary key,
        -- referential constraint for provider_id deferred due to circular dependencies
    category_id INTEGER not null
        -- referential constraint for category_id deferred due to circular dependencies
);

alter table atoz_siteproxy_provider add
    constraint atoz_site_prov_cate_id_f_ubcwg foreign key (category_id)
      references cat_categories(category_id);
alter table atoz_siteproxy_provider add
    constraint atoz_site_prov_prov_id_f_3n5mw foreign key (provider_id)
      references atoz_provider(provider_id);


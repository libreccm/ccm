create table atoz_cat_ct_blacklist_map (
    provider_id INTEGER not null,
        -- referential constraint for provider_id deferred due to circular dependencies
    type_id INTEGER not null,
        -- referential constraint for type_id deferred due to circular dependencies
    constraint atoz_cat_ct_bla_map_pr_p_rqpg1
      primary key(type_id, provider_id)
);

alter table atoz_cat_ct_blacklist_map add
    constraint atoz_cat_ct_bla_map_pr_f_b2b9h foreign key (provider_id)
      references atoz_cat_provider(provider_id);
alter table atoz_cat_ct_blacklist_map add
    constraint atoz_cat_ct_bla_map_ty_f_lb9wc foreign key (type_id)
      references content_types(type_id);



create table atoz_item_aliases (
    alias_id INTEGER not null
        constraint atoz_ite_alias_alia_id_p_7yshg
          primary key,
        -- referential constraint for alias_id deferred due to circular dependencies
    provider_id INTEGER not null,
        -- referential constraint for provider_id deferred due to circular dependencies
    item_id INTEGER not null,
        -- referential constraint for item_id deferred due to circular dependencies
    title VARCHAR(200) not null,
    letter CHAR(1) not null
);

alter table atoz_item_aliases add
    constraint atoz_ite_alia_provi_id_f_si8tg foreign key (provider_id)
      references atoz_item_provider(provider_id);
alter table atoz_item_aliases add
    constraint atoz_ite_alias_alia_id_f_spljy foreign key (alias_id)
      references acs_objects(object_id);
alter table atoz_item_aliases add
    constraint atoz_ite_aliase_ite_id_f__bqlu foreign key (item_id)
      references cms_items(item_id);


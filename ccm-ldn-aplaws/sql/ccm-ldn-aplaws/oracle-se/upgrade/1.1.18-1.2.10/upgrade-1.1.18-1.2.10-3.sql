alter table ss_responses modify (score integer null);


insert into g11n_charsets (charset_id, charset)
  values (acs_object_id_seq.nextval, 'UTF-8');

update g11n_locales set default_charset_id = 
  (select charset_id from g11n_charsets where charset = 'UTF-8');

insert into g11n_locales (locale_id, language, country, variant, default_charset_id)
   values (acs_object_id_seq.nextval, 'en', 'GB', null, 
  (select charset_id from g11n_charsets where charset = 'UTF-8'));

insert into cms_category_index_item_map (item_id, category_id)
 select min(i.item_id), m.category_id
   from cms_items i, 
        acs_objects o,
        cat_object_category_map m
  where i.version = 'draft'
    and i.item_id = m.object_id
    and o.object_id = i.item_id
    and o.object_type = 'com.arsdigita.london.cms.dublin.types.Greeting'
    group by m.category_id;

@@ portals.sql
@@ delete-public-registered.sql
@@ deferred.sql

update cms_items
set parent_id = null
where item_id in ( select dublin_id from ldn_dublin_core_items );

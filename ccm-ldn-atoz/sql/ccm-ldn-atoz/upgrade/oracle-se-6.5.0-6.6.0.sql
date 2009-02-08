@@ ../default/upgrade/add-cat_aliases.sql

insert into atoz_cat_aliases (object_id, provider_id, category_id, letter, title)
select acs_object_id_seq.nextval, m.provider_id, m.category_id, m.letter, m.title
from atoz_cat_alias_map m;

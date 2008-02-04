alter table cat_category_category_map
  drop constraint cat_cat_map_rel_type_ck ;
alter table cat_category_category_map
  add constraint cat_cat_map_rel_type_ck check
    (relation_type in ('child','related','preferred')) ;

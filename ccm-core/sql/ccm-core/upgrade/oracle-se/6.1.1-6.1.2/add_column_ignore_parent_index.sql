alter table cat_categories add (ignore_parent_index_p char(1));
update cat_categories set ignore_parent_index_p=0;
alter table cat_categories modify (ignore_parent_index_p not null);

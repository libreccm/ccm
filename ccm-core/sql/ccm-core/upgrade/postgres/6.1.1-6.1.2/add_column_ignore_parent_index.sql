alter table cat_categories add column ignore_parent_index_p character(1);
update cat_categories set ignore_parent_index_p=0;
alter table cat_categories alter column ignore_parent_index_p set NOT NULL;

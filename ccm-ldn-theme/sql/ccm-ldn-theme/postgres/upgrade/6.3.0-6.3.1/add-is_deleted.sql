
alter table theme_files add is_deleted boolean;
update theme_files set is_deleted = 'f';
alter table theme_files alter is_deleted set not null;



alter table theme_files add is_deleted char(1);
update theme_files set is_deleted = '0';
alter table theme_files add constraint theme_files_is_deleted_c_gadn2
          check(is_deleted in ('0', '1'));
alter table theme_files modify is_deleted not null;


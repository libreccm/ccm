-- This adds a constrating to make sure that we do not get any duplicate
-- themes.  We have to do this manually because persistence
-- does not know how to deal with theme_id which is part of an association

alter table theme_files add constraint theme_files_un unique(theme_id, file_path, version);

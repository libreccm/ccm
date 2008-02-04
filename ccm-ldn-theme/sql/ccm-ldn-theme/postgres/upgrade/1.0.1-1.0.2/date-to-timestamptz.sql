begin;

    alter table theme_themes rename column last_published_date to last_published_date_save;
    alter table theme_themes add last_published_date timestamptz;
    update theme_themes set last_published_date = last_published_date_save;
    alter table theme_themes drop last_published_date_save;

    alter table theme_files rename column last_modified_date to last_modified_date_save;
    alter table theme_files add last_modified_date timestamptz;
    update theme_files set last_modified_date = last_modified_date_save;
    alter table theme_files alter last_modified_date set not null;
    alter table theme_files drop last_modified_date_save;

commit;



alter table CAT_PURPOSES add (LANGUAGE char(2 byte));
alter table CAT_PURPOSES add constraint CAT_PURPOSES_KEY_LANG_UN unique (KEY, LANGUAGE);
alter table CAT_PURPOSES drop constraint CAT_PURPOSES_KEY_UN;

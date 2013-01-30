alter table cat_purposes add column language char(2);
alter table cat_purposes add constraint cat_purposes_lang_un unique (key, language);
alter table cat_purposes drop constraint cat_purposes_key_un;

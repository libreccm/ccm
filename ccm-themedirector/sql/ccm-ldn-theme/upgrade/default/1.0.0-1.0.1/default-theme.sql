alter table theme_app add
    default_theme_id INTEGER;

alter table theme_app add
    constraint them_app_defau_them_id_f_6plv_
    foreign key (default_theme_id) references theme_themes(theme_id);

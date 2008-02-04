create table workspace_themes (
    theme_id INTEGER not null
        constraint workspac_theme_them_id_p_6_22y
          primary key,
        -- referential constraint for theme_id deferred due to circular dependencies
    theme_name VARCHAR(4000),
    theme_desc VARCHAR(4000),
    ctx_bar_color VARCHAR(100),
    ctx_bar_text_color VARCHAR(100),
    active_tab_color VARCHAR(100),
    active_tab_text_color VARCHAR(100),
    inactive_tab_color VARCHAR(100),
    inactive_tab_text_color VARCHAR(100),
    top_rule VARCHAR(100),
    bottom_rule VARCHAR(100),
    portlet_head VARCHAR(100),
    portlet_icon VARCHAR(100),
    portlet_border_color VARCHAR(100),
    portlet_border_style VARCHAR(100),
    portlet_header_text_color VARCHAR(100),
    page_bg_color VARCHAR(100),
    page_bg_image VARCHAR(100),
    narrow_bg_color VARCHAR(100),
    body_text_color VARCHAR(100)
);

create table themeapplications (
    theme_app_id INTEGER not null
        constraint themeapplic_the_app_id_p_ix9jr
          primary key
        -- referential constraint for theme_app_id deferred due to circular dependencies
);

alter table workspaces add column theme_id INTEGER;

alter table workspaces add
    constraint workspaces_theme_id_f_tpdju foreign key (theme_id)
      references workspace_themes(theme_id);

alter table workspace_themes add
    constraint workspac_theme_them_id_f_ix7ez foreign key (theme_id)
      references acs_objects(object_id);

alter table themeapplications add
    constraint themeapplic_the_app_id_f_ejek5 foreign key (theme_app_id)
      references applications(application_id);

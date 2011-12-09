create table temp as select * from subsite_site;
drop table subsite_site;

create table subsite_site (
    site_id INTEGER not null
        constraint subsite_site_site_id_p_rz022
          primary key,
        -- referential constraint for site_id deferred due to circular dependencies
    description VARCHAR(4000),
    front_page_id INTEGER not null,
        -- referential constraint for front_page_id deferred due to circular dependencies
    hostname VARCHAR(250) not null
        constraint subsite_site_hostname_u_uy5xf
          unique,
    root_category_id INTEGER not null,
        -- referential constraint for root_category_id deferred due to circular dependencies
    style_dir VARCHAR(50),
    template_context VARCHAR(200) not null,
        -- referential constraint for template_context deferred due to circular dependencies
    title VARCHAR(100) not null
);

insert into cms_template_use_contexts (use_context, label, description)
  select 'subsite-' || site_id, title, description
    from temp;

insert into subsite_site (site_id, description, front_page_id, hostname, root_category_id, style_dir, template_context, title)
  select site_id, description, front_page_id, hostname, root_category_id, style_dir, 'subsite-' || site_id, title
    from temp; 

alter table subsite_site add 
    constraint subs_sit_roo_catego_id_f_kwe6m foreign key (root_category_id)
      references cat_categories(category_id);
alter table subsite_site add 
    constraint subs_sit_templ_context_f_6wdu3 foreign key (template_context)
      references cms_template_use_contexts(use_context);
alter table subsite_site add 
    constraint subsit_sit_fron_pag_id_f_4agqx foreign key (front_page_id)
      references applications(application_id);
alter table subsite_site add 
    constraint subsite_site_site_id_f_rntkc foreign key (site_id)
      references acs_objects(object_id) on delete cascade;

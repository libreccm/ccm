
create table workspace_page_layouts (
    layout_id INTEGER not null
        constraint worksp_pag_layo_lay_id_p_paxph
          primary key,
    title VARCHAR(200) not null,
    description VARCHAR(4000),
    format VARCHAR(50) not null
        constraint worksp_pag_layo_format_u_ff8p6
	  unique
);

insert into workspace_page_layouts (layout_id, title, description, format)
  values (acs_object_id_seq.nextval, '1 Column', 'One column', '100%');

insert into workspace_page_layouts (layout_id, title, description, format)
  values (acs_object_id_seq.nextval, '2 Column', 'Two columns, equal size', '50%,50%');

insert into workspace_page_layouts (layout_id, title, description, format)
  values (acs_object_id_seq.nextval, '3 Column', 'Three columns, equal size', '30%,40%,30%');

insert into workspace_page_layouts (layout_id, title, description, format)
  values (acs_object_id_seq.nextval, '4 Column', 'Four columns, equal size', '25%,25%,25%,25%');

insert into workspace_page_layouts (layout_id, title, description, format)
  values (acs_object_id_seq.nextval, '5 Column', 'Five columns, equal size', '20%,20%,20%,20%,20%');

alter table workspaces add default_layout_id INTEGER;
alter table workspaces modify party_id not null;
-- XXX '100%' is only relevant for APLAWS...
update workspaces set default_layout_id = (select layout_id from workspace_page_layouts where format = '100%');
alter table workspaces modify default_layout_id not null;
alter table workspaces add 
    constraint workspac_defau_layo_id_f_xvb7g foreign key (default_layout_id)
      references workspace_page_layouts(layout_id);

create table workspace_pages (
    page_id INTEGER not null
        constraint workspac_pages_page_id_p_iugi0
          primary key,
    workspace_id INTEGER,
    layout_id INTEGER not null
);
alter table workspace_pages add 
    constraint workspa_pag_workspa_id_f_4xkkr foreign key (workspace_id)
      references workspaces(workspace_id);
alter table workspace_pages add 
    constraint workspac_page_layou_id_f_9uq1r foreign key (layout_id)
      references workspace_page_layouts(layout_id);
alter table workspace_pages add 
    constraint workspac_pages_page_id_f_jhka1 foreign key (page_id)
      references portals(portal_id);

insert into workspace_pages (page_id, workspace_id, layout_id)
  select portal_id, workspace_id,
    (select layout_id from workspace_page_layouts where format = '100%')
   from workspace_portal_map;

update acs_objects 
   set object_type = 'com.arsdigita.london.portal.WorkspacePage',
       default_domain_class = 'com.arsdigita.london.portal.WorkspacePage'
 where object_id in (select page_id from workspace_pages);

insert into application_types (
   application_type_id, 
   object_type, 
   title, 
   description, 
   workspace_application_p, 
   has_full_page_view_p, 
   has_embedded_view_p, 
   singleton_p) 
 values (
   acs_object_id_seq.nextval, 
   'com.arsdigita.london.portal.WorkspacePage', 
   'Portal Workspace Page', 
   'Pages for the portal workspaces',
   null, 
   null,
   null,
   null);

update applications
   set sort_key = (select tab_number from workspace_portal_map where application_id = portal_id)
  where application_id in (select page_id from workspace_pages);

update applications
   set application_type_id = (select application_type_id from application_types where object_type = 'com.arsdigita.london.portal.WorkspacePage')
  where application_id in (select page_id from workspace_pages);

drop table workspace_portal_map;

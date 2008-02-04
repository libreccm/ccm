@@ ../../../../navigation/ddl/oracle-se/table-nav_quick_links-auto.sql
@@ ../../../../navigation/ddl/oracle-se/table-nav_templates-auto.sql
@@ ../../../../navigation/ddl/oracle-se/table-nav_template_cat_map-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-cms_links-auto.sql
@@ ../../../../subsite/ddl/oracle-se/table-subsite_site-auto.sql
@@ ../../../../subsite/ddl/oracle-se/table-subsite_cc-auto.sql
@@ ../../../../portal/ddl/oracle-se/table-workspace_portal_map-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-ct_faq-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-ct_file_storage-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-ct_glossary-auto.sql
@@ ../../../../content-types/ddl/oracle-se/table-ct_organization-auto.sql
@@ ../../../../content-types/default/table-in_motd.sql
@@ ../../../../util/default/table-managed_packages.sql
@@ content-types.sql
@@ formbuilder.sql

alter table ss_survey_responses rename to ss_responses;

insert into cat_root_cat_object_map select * from site_node_category_map;
drop table site_node_category_map;

-- the default value will be overridden by the java layer so it does
-- not matter what we set it to
alter table rss_feeds add is_provider char(1) default 'f' constraint rss_feeds_is_provider_nn not null;
update rss_feeds set is_provider = acsj;
alter table rss_feeds drop column acsj;


insert into vc_transactions (
    transaction_id,
    object_id,
    timestamp
)
select
    acs_object_id_seq.nextval,
    i.item_id,
    sysdate
from
    cms_items i
where
    not exists (
        select 1
        from vc_transactions t
        where t.object_id = i.item_id
    );


-- drop some tables
drop table ldn_frm_dd_select;
drop table ldn_frm_form_item;
drop table ldn_frm_lstnr_conf_email;
drop table ldn_frm_lstnr_conf_redirect;
drop table ldn_frm_lstnr_simple_email;
drop table ldn_frm_lstnr_tmpl_email;
drop table ldn_frm_lstnr_xml_email;
drop table ldn_frm_meta_object;
drop table ldn_frm_section_item;
drop table ldn_frm_widget_label;
drop table ldn_frm_dataquery;
drop table ldn_frm_object_type;
drop table ps_public_groups;
-- Expressions service is no longer in use
drop table persistent_expressions;

-- drop some triggers
drop trigger users_public_grp_tr;

alter table ldn_dublin_core_item_map drop constraint ldn_dublin_core_imap_pk;

alter table ldn_dublin_core_item_map add constraint ldn_dln_core_imap_itm_id_pk primary key (item_id);

alter table ldn_dublin_core_item_map modify dublin_id integer null;
alter table ldn_dublin_core_item_map drop constraint ldn_dln_core_imap_itm_id_nn;

delete from core_services_items where item_id in (select dublin_id from ldn_dublin_core_items);
delete from cms_pages where item_id in (select dublin_id from ldn_dublin_core_items);

insert into cms_text_mime_types (mime_type, is_inso) select mime_type, 0 from cms_mime_types where object_type = 'com.arsdigita.cms.TextMimeType' and mime_type not in (select mime_type from cms_text_mime_types);


@@ ../../../../core/default/kernel/insert-groups.sql
insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -300, 'admin', sysdate);

insert into acs_objects (object_id, object_type, display_name) 
values (-200, 'com.arsdigita.kernel.User', 'The Public');
insert into parties (party_id, primary_email) values (-200, 'public@nullhost');
insert into person_names (name_id, given_name, family_name) values
(-201, 'Public', 'Users');
insert into users (user_id, name_id) values (-200, -201);
insert into email_addresses values ('public@nullhost', '1', '0');
insert into acs_objects (object_id, object_type, display_name) 
values (-202, 'com.arsdigita.kernel.User', 'Registered Users');
insert into parties (party_id, primary_email) values (-202, 'registered@nullhost');
insert into person_names (name_id, given_name, family_name) values
(-203, 'Registered', 'Users');
insert into users (user_id, name_id) values (-202, -203);
insert into email_addresses values ('registered@nullhost', '1', '0');

--/core/sql/default/versioning/insert-vc_actions.sql



-- Morph old file objects into new ones
update acs_objects 
   set object_type = 'com.arsdigita.cms.contenttypes.FileStorageItem',
       default_domain_class = 'com.arsdigita.cms.contenttypes.FileStorageItem'
 where object_type = 'com.arsdigita.cms.FileStorage';

insert into ct_file_storage (item_id, description, file_id)
  select filestorage_id, nvl(description, 'unknown'), filebits from cms_filestorage;

drop table cms_filestorage;

-- Rename the old file storage type

--  Change the content type.
update content_types 
   set object_type = 'com.arsdigita.cms.contenttypes.FileStorageItem',
       classname = 'com.arsdigita.cms.contenttypes.FileStorageItem'
  where object_type = 'com.arsdigita.cms.FileStorage';

-- Let the initializer change the authoring kit & steps

-- Oh, & the dynamic object tyep
delete from persistence_dynamic_ot
  where dynamic_object_type = 'com.arsdigita.cms.FileStorage';

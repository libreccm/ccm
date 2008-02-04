--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: oracle-se-6.0.1-6.1.0.sql 1169 2006-06-14 13:08:25Z fabrice $
-- $DateTime: 2004/08/16 18:10:38 $

PROMPT Red Hat WAF 6.0.1 -> 6.1.0 Upgrade Script (Oracle)

@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-admin_app-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-agentportlets-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql 
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-init_requirements-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-inits-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-keystore-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-lucene_ids-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-sitemap_app-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-webapps-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/deferred.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-host-unique-index.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-cat_root_cat_object_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-applications.sql

alter table cms_mime_extensions modify (mime_type null);
alter table cms_mime_extensions add constraint
  cms_mim_exten_mim_type_f_7pwwd foreign key(mime_type)
  references cms_mime_types(mime_type);

drop table parameterized_privileges;
update apm_package_types set package_uri = 'http://arsdigita.com/sitemap' where package_uri = 'http://arsdigita.com/admin/sitemap';

create index AGENTPORT_SUPERPORT_ID_IDX on AGENTPORTLETS(SUPERPORTLET_ID);
create index INIT_REQS_REQD_INIT_IDX on INIT_REQUIREMENTS(REQUIRED_INIT);

-- insert mime type file extensions
@@ ../default/upgrade/6.0.1-6.1.0/insert-cms_mime_extensions.sql

-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privilege_col_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/comment-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_permissions.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_privileges.sql

@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-acs_privilege_hierarchy.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql

-- Object context denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_object_1_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_object_grants.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_granted_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_context-quick.sql

-- Party denormalization
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/index-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-dnm_party_grants.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/package-dnm_parties.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-dnm_group_membership.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/upgrade-dnm_parties.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql

drop package permission_denormalization;

drop view all_context_non_leaf_map;
drop view granted_trans_context_index;
drop view granted_trans_context_map;
drop view ungranted_trans_context_index;
drop view ungranted_trans_context_map;
drop view object_context_trans_map;

drop trigger object_context_in_tr;
drop trigger object_context_up_tr;
drop trigger object_context_del_tr;
drop trigger acs_objects_context_in_tr;
drop trigger acs_permissions_in_tr;
drop trigger acs_permissions_up_tr;
drop trigger acs_permissions_del_tr;

declare
  v_exists char(1);
begin

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'granted_context_non_leaf_map';

  if (v_exists = '1') then
    execute immediate 'drop table granted_context_non_leaf_map';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'ungranted_context_non_leaf_map';

  if (v_exists = '1') then
    execute immediate 'drop table ungranted_context_non_leaf_map';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'object_grants';

  if (v_exists = '1') then
    execute immediate 'drop table object_grants';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'context_child_counts';

  if (v_exists = '1') then
    execute immediate 'drop table context_child_counts';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'object_context_map';

  if (v_exists = '1') then
    execute immediate 'drop table object_context_map';
  end if;

end;
/
show errors;

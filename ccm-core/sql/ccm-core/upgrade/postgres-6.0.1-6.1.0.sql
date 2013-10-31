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
-- $Id: postgres-6.0.1-6.1.0.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

\echo Red Hat WAF 6.0.1 -> 6.1.0 Upgrade Script (PostgreSQL)

begin;

\i postgres/upgrade/6.0.1-6.1.0/table-admin_app-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-agentportlets-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-init_requirements-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-inits-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-keystore-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-lucene_ids-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-sitemap_app-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/table-webapps-auto.sql
\i postgres/upgrade/6.0.1-6.1.0/deferred.sql
\i postgres/upgrade/6.0.1-6.1.0/update-host-unique-index.sql
\i postgres/upgrade/6.0.1-6.1.0/update-cat_root_cat_object_map.sql
\i postgres/upgrade/6.0.1-6.1.0/update-applications.sql

update apm_package_types set package_uri = 'http://arsdigita.com/sitemap' where package_uri = 'http://arsdigita.com/admin/sitemap';

alter table cms_mime_extensions alter mime_type drop not null;
alter table cms_mime_extensions add constraint cms_mim_exten_mim_type_f_7pwwd foreign key(mime_type) references cms_mime_types(mime_type);
drop table parameterized_privileges;
create index agentport_superport_id_idx on agentportlets(superportlet_id);
create index init_reqs_reqd_init_idx on init_requirements(required_init);

-- insert mime type file extensions
\i default/upgrade/6.0.1-6.1.0/insert-cms_mime_extensions.sql

-- Upgrade script for new permission denormalization
-- Privilege Hierarchy
\i postgres/upgrade/6.0.1-6.1.0/table-acs_privilege_hierarchy.sql
\i postgres/upgrade/6.0.1-6.1.0/index-acs_privilege_hierarchy.sql
\i postgres/upgrade/6.0.1-6.1.0/comment-acs_privilege_hierarchy.sql

-- Privileges/permission denormalization
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_privileges.sql
\i postgres/upgrade/6.0.1-6.1.0/comment-dnm_privileges.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_col_map.sql
\i postgres/upgrade/6.0.1-6.1.0/comment-dnm_privilege_col_map.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy_map.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_privilege_hierarchy.sql
\i postgres/upgrade/6.0.1-6.1.0/comment-dnm_privilege_hierarchy.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_permissions.sql
\i postgres/upgrade/6.0.1-6.1.0/comment-dnm_permissions.sql
\i postgres/upgrade/6.0.1-6.1.0/index-dnm_permissions.sql

\i postgres/upgrade/6.0.1-6.1.0/package-dnm_privileges.sql
\i postgres/upgrade/6.0.1-6.1.0/insert-acs_privilege_hierarchy.sql
\i postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_privileges.sql

-- Object context denormalization
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_object_1_granted_context.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_object_grants.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_granted_context.sql
\i postgres/upgrade/6.0.1-6.1.0/index-dnm_object_1_granted_context.sql
\i postgres/upgrade/6.0.1-6.1.0/index-dnm_granted_context.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_ungranted_context.sql
\i postgres/upgrade/6.0.1-6.1.0/index-dnm_ungranted_context.sql

\i postgres/upgrade/6.0.1-6.1.0/insert-dnm_context.sql
\i postgres/upgrade/6.0.1-6.1.0/package-dnm_context.sql
\i postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_context.sql

-- Party denormalization
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_group_membership.sql
\i postgres/upgrade/6.0.1-6.1.0/index-dnm_group_membership.sql
\i postgres/upgrade/6.0.1-6.1.0/table-dnm_party_grants.sql
\i postgres/upgrade/6.0.1-6.1.0/package-dnm_parties.sql
\i postgres/upgrade/6.0.1-6.1.0/insert-dnm_group_membership.sql
\i postgres/upgrade/6.0.1-6.1.0/upgrade-dnm_parties.sql

\i postgres/upgrade/6.0.1-6.1.0/triggers-dnm_privileges.sql
\i postgres/upgrade/6.0.1-6.1.0/triggers-dnm_context.sql
\i postgres/upgrade/6.0.1-6.1.0/triggers-dnm_parties.sql

create index dnm_group_membership_grp_idx on dnm_group_membership(pd_group_id);
create index dnm_ungranted_context_obj_idx on dnm_ungranted_context(object_id);

drop index dnm_gc_uk;
drop index dnm_o1gc_necid_oid;
drop index dnm_o1gc_uk1;
drop index dnm_ungranted_context_un;

drop trigger object_context_in_tr on object_context;
drop trigger object_context_up_tr on object_context;
drop trigger object_context_del_tr on object_context;
drop trigger acs_objects_context_in_tr on acs_objects;
drop trigger acs_permissions_in_tr on acs_permissions;
drop trigger acs_permissions_up_tr on acs_permissions;
drop trigger acs_permissions_del_tr on acs_permissions;

drop function object_context_in_fn();
drop function object_context_up_fn();
drop function object_context_del_fn();
drop function acs_objects_context_in_fn();
drop function acs_permissions_in_fn();
drop function acs_permissions_up_fn();
drop function acs_permissions_del_fn();

drop function permissions_add_context (integer, integer);
drop function permissions_remove_context (integer, integer);
drop function permissions_add_grant(integer);
drop function permissions_remove_grant(integer);
drop function permissions_rebuild();

create or replace function temp_drop_objects() returns boolean as '
declare
  v_exists boolean;
begin
  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''all_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop view all_context_non_leaf_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''granted_trans_context_index'';

  if (v_exists) then
    execute ''drop view granted_trans_context_index cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''granted_trans_context_map'';

  if (v_exists) then
    execute ''drop view granted_trans_context_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''ungranted_trans_context_index'';

  if (v_exists) then
    execute ''drop view ungranted_trans_context_index cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''ungranted_trans_context_map'';

  if (v_exists) then
    execute ''drop view ungranted_trans_context_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''v''
         and lower(relname) = ''object_context_trans_map'';

  if (v_exists) then
    execute ''drop view object_context_trans_map cascade'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''granted_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop table granted_context_non_leaf_map'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''ungranted_context_non_leaf_map'';

  if (v_exists) then
    execute ''drop table ungranted_context_non_leaf_map'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''object_grants'';

  if (v_exists) then
    execute ''drop table object_grants'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''context_child_counts'';

  if (v_exists) then
    execute ''drop table context_child_counts'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''object_context_map'';

  if (v_exists) then
    execute ''drop table object_context_map'';
  end if;

  return TRUE;

end;
' language 'plpgsql';

select temp_drop_objects();
drop function temp_drop_objects();

commit;

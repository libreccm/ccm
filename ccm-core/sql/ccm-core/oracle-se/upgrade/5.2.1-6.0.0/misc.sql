--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: misc.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

ALTER TABLE vc_operations DROP CONSTRAINT vc_operations_actions_fk;
ALTER TABLE vc_operations DROP CONSTRAINT vc_operations_trans_id_fk;
ALTER TABLE vc_transactions DROP CONSTRAINT vc_trans_masters_fk;
ALTER TABLE vc_transactions DROP CONSTRAINT vc_trans_objects_fk;

drop function last_attr_value;

alter table portlets modify (
    portal_id not null
);

drop table cw_process_task_map;

update cw_tasks set is_active = 0 where is_active is null;

commit;

-- cat_categories
--------------------------------------------------------------------------------
alter table cat_categories drop constraint cat_categories_abstract_p_ck;
alter table cat_categories modify (
    abstract_p not null,
    enabled_p not null
);
alter table cat_categories modify (
    default_ancestors varchar2(3209)
);

alter table cat_categories add (
    url VARCHAR(200)
);

declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_indexes uc
   where lower(index_name) = 'cat_cat_deflt_ancestors_idx';

  if (v_exists = '0') then
    execute immediate 'create index cat_cat_deflt_ancestors_idx on cat_categories(default_ancestors)';
  end if;

end;
/
show errors;

-- Fix Constraint Ordering
-- NOTE: The following ddl assumes that no tables have a referential constraint against one
--       of these tables.  This holds for core and *should* hold in general as these tables
--       are unlikely cadidates for foreign keys.
--------------------------------------------------------------------------------
alter table acs_permissions drop constraint acs_per_gra_id_obj_id__p_lrweb;
alter table acs_permissions add
    constraint acs_per_gra_id_obj_id__p_lrweb
      primary key(object_id, grantee_id, privilege);

alter table apm_package_type_listener_map drop constraint apm_pac_typ_lis_map_li_p_6_z6o;
alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_li_p_6_z6o
      primary key(package_type_id, listener_id);

alter table cw_task_group_assignees drop constraint task_group_assignees_pk;
alter table cw_task_group_assignees add
    constraint cw_tas_gro_ass_gro_id__p_0bqv_
        primary key(group_id, task_id);

alter table group_member_map drop constraint grou_mem_map_gro_id_me_p_9zo_i;
alter table group_member_map add
    constraint grou_mem_map_gro_id_me_p_9zo_i
      primary key(member_id, group_id);

alter table group_subgroup_map drop constraint grou_sub_map_gro_id_su_p_8caa0;
alter table group_subgroup_map add
    constraint grou_sub_map_gro_id_su_p_8caa0
      primary key(subgroup_id, group_id);

alter table parameterized_privileges drop constraint para_pri_bas_pri_par_k_p_a1rpb;
alter table parameterized_privileges add
    constraint para_pri_bas_pri_par_k_p_a1rpb
      primary key(param_key, base_privilege);

alter table party_email_map drop constraint part_ema_map_ema_add_p_p_px7u4;
alter table party_email_map add
    constraint part_ema_map_ema_add_p_p_px7u4
      primary key(party_id, email_address);

alter table site_nodes drop constraint site_node_nam_paren_id_u_a3b4a;
alter table site_nodes add
    constraint site_node_nam_paren_id_u_a3b4a
      unique(parent_id, name);

-- Actions that require PL/SQL
----------------------------------------------------------------------------------
declare
  version varchar2(4000);
  compatibility varchar2(4000);
  v_constraint_name varchar2(4000);
begin

  -- Find and fix cw_task_user_assignees fk constraint
  --------------------------------------------------------------------------------
  select constraint_name into v_constraint_name
    from user_constraints uc
   where lower(table_name) = 'cw_task_user_assignees'
     and constraint_type = 'R'
     and exists (select 1
                   from user_cons_columns ucc
                  where ucc.constraint_name = uc.constraint_name
                    and lower(column_name) = 'task_id'
                    and position = 1);

  if (v_constraint_name is not null) then
    execute immediate 'alter table cw_task_user_assignees drop constraint ' ||  v_constraint_name;
    execute immediate 'alter table cw_task_user_assignees add constraint cw_tas_use_assi_tas_id_f_feri7 foreign key(task_id) references cw_user_tasks(task_id)';
  end if;

  -- Rename constraints
  --------------------------------------------------------------------------------
  DBMS_UTILITY.DB_VERSION (version, compatibility);
  if (compatibility >= '9.2.0.0.0') then
    -- The following ddl will only work on Oracle 9.2 or greater
    execute immediate 'alter table cw_task_dependencies rename constraint task_dependencies_pk to cw_tas_dep_dep_tas_id__p_hdzws';
    execute immediate 'alter table cw_task_user_assignees rename constraint task_user_assignees_pk to cw_tas_use_ass_tas_id__p_vsdyq';
  end if;
end;
/
show errors;

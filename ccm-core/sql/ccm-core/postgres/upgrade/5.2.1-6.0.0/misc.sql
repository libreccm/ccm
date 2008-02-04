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

-- redefine function because calling current_timestamp() no longer works.  We
-- need to drop the parens.
drop function currentDate();
create or replace function currentDate()
  returns timestamptz as '
  declare
  begin
    return current_timestamp;
end;' language 'plpgsql';

drop function last_attr_value(varchar,integer);

drop table cw_process_task_map;

update cw_tasks set is_active = 0 where is_active is null;

alter table cat_categories add url varchar (200);

drop trigger acs_permissions_cascade_del_tr on acs_objects;
drop function acs_permissions_cascade_del_fn();

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

alter table site_nodes add
    constraint site_node_nam_paren_id_u_a3b4a
      unique(parent_id, name);

-- These constraints changed name
update pg_constraint set conname = 'cat_categori_catego_id_p_yeprq' where UPPER(conname) = UPPER('cat_categories_pk');
update pg_class set relname = 'cat_categori_catego_id_p_yeprq' where relname = 'cat_categories_pk';

update pg_constraint set conname = 'cw_tas_dep_dep_tas_id__p_hdzws' where UPPER(conname) = UPPER('task_dependencies_pk');
update pg_class set relname = 'cw_tas_dep_dep_tas_id__p_hdzws' where relname = 'task_dependencies_pk';

update pg_constraint set conname = 'cw_tas_use_ass_tas_id__p_vsdyq' where UPPER(conname) = UPPER('task_user_assignees_pk');
update pg_class set relname = 'cw_tas_use_ass_tas_id__p_vsdyq' where relname = 'task_user_assignees_pk';

-- oracle compatibility

-- Replacements for PG's special operators that cause persistence's
-- SQL parser to barf

create or replace function bitand(integer, integer) returns integer as '
begin
    return $1 & $2;
end;
' language 'plpgsql';

create or replace function bitor(integer, integer) returns integer as '
begin
    return $1 | $2;
end;
' language 'plpgsql';

create or replace function bitxor(integer, integer) returns integer as '
begin
    return $1 # $2;
end;
' language 'plpgsql';

create or replace function bitneg(integer) returns integer as '
begin
    return ~$1;
end;
' language 'plpgsql';


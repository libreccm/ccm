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
-- $Id: triggers-dnm_privileges.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace function acs_privileges_dnm_pr_fn()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' THEN 
      perform dnm_privileges_add_privilege(new.privilege);
      return new;
    elsif TG_OP = ''DELETE'' THEN 
      perform dnm_privileges_delete_privilege(old.privilege);
      return old;
    elsif TG_OP = ''UPDATE'' THEN
      perform dnm_privileges_delete_privilege(old.privilege);
      perform dnm_privileges_add_privilege(new.privilege);
      return new;
    end if; 
  end; ' language 'plpgsql'
;


create trigger acs_privileges_dnm_pr_trg
  after insert or delete or update
  on acs_privileges
  for each row
  execute procedure acs_privileges_dnm_pr_fn()
;


create or replace function acs_priv_hier_dnm_pr_fn ()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' THEN
      perform dnm_privileges_map_add_child_priv(new.privilege, new.child_privilege);
      return new;
    elsif TG_OP = ''DELETE'' THEN
      perform dnm_privileges_delete_child_privilege(old.privilege, old.child_privilege);
      return old;
    elsif TG_OP = ''UPDATE'' THEN
      perform dnm_privileges_delete_child_privilege(old.privilege, old.child_privilege);
      perform dnm_privileges_map_add_child_priv(new.privilege, new.child_privilege);
      return new;
    end if;
  end; ' language 'plpgsql'
;

create trigger acs_priv_hier_dnm_pr_trg
  after insert or delete
  on acs_privilege_hierarchy
  for each row
  execute procedure acs_priv_hier_dnm_pr_fn()
;


create or replace function acs_permission_dnm_pr_fn()
  returns trigger as '
  begin
    if TG_OP = ''INSERT'' then
      perform dnm_privileges_add_grant(new.object_id, new.grantee_id, new.privilege);  
      return new;
    elsif TG_OP = ''DELETE'' then
      perform dnm_privileges_remove_grant(old.object_id, old.grantee_id, old.privilege);
      return old;
    end if;
end; ' language 'plpgsql'
;
      

create trigger acs_permission_dnm_pr_trg
  after insert or delete 
  on acs_permissions
  for each row
  execute procedure acs_permission_dnm_pr_fn()
;


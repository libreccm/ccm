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
-- $Id: triggers-dnm_parties.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


create or replace function acs_permissions_dnm_parties_fn ()
  returns trigger as '
  declare 
  begin
    if TG_OP = ''INSERT'' then
      perform dnm_parties_add_grant(new.grantee_id);
      return new;
    elsif TG_OP = ''DELETE'' then
      perform dnm_parties_remove_grant(old.grantee_id);
      return new;
    end if;
  end; ' language 'plpgsql'
;

create trigger acs_permissions_dnm_parties_trg 
  after insert or delete 
  on acs_permissions 
  for each row
  execute procedure acs_permissions_dnm_parties_fn()
;


create or replace function group_subgroup_map_dnm_parties_fn ()
  returns trigger as '
  declare
  begin
    perform dnm_parties_add_group_subgroup_map(new.group_id, new.subgroup_id);
    return null;
  end; ' language 'plpgsql'
;

create trigger group_subgroup_map_dnm_parties_trg
  after insert
  on group_subgroup_map 
  for each row
  execute procedure group_subgroup_map_dnm_parties_fn()
;


create or replace function group_subgr_tr_idx_dnm_parties_fn ()
  returns trigger as '
  begin 
    perform  dnm_parties_delete_map(old.group_id, old.subgroup_id); 
    return null;
  end; ' language 'plpgsql'
;

create trigger group_subgr_tr_idx_dnm_parties_trg
  after delete 
  on group_subgroup_trans_index
  for each row
  execute procedure group_subgr_tr_idx_dnm_parties_fn()
;


create or replace function group_member_map_dnm_parties_fn ()
  returns trigger as '
  declare
  begin
    perform dnm_parties_add_group_user_map(new.group_id, new.member_id);
    return null;
  end; ' language 'plpgsql'
;

create trigger group_member_map_dnm_parties_trg
  after insert 
  on group_member_map
  for each row
  execute procedure group_member_map_dnm_parties_fn()
;

create or replace function group_member_trans_idx_dnm_parties_fn()
  returns trigger as '
  declare
  begin
     perform dnm_parties_delete_map(old.group_id, old.member_id);
     return null;
  end; ' language 'plpgsql'
;

create trigger group_member_trans_idx_dnm_parties_trg
  after delete 
  on group_member_trans_index 
  for each row
  execute procedure group_member_trans_idx_dnm_parties_fn ()
;

create or replace function parties_dnm_paries_fn() 
  returns trigger as '
  declare
  begin
    perform dnm_parties_delete_map(old.party_id, old.party_id);
    return null;
  end; ' language 'plpgsql'
;
  
create trigger parties_dnm_paries_trg
  after delete
   on parties
  for each row
  execute procedure parties_dnm_paries_fn()
;

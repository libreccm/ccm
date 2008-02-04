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

create or replace trigger acs_permission_dnm_pa_trg
  after insert or delete 
  on acs_permissions
  for each row
begin
  if inserting then
    dnm_parties.add_grant(:new.grantee_id);
  elsif deleting then
    dnm_parties.remove_grant(:old.grantee_id);
  end if;
end;
/
show errors

create or replace trigger group_subg_map_dnm_pa_trg
  after insert
  on group_subgroup_map
  for each row
begin
    dnm_parties.add_group_subgroup_map(:new.group_id, :new.subgroup_id);
end;
/
show errors 

  
create or replace trigger group_subg_tr_idx_dnm_pa_trg
  after delete
  on group_subgroup_trans_index
  for each row
begin
    dnm_parties.delete_group_member_map(:old.group_id, :old.subgroup_id);
end;
/
show errors

create or replace trigger group_mem_map_dnm_pa_trg
  after insert
  on group_member_map
  for each row
begin
    dnm_parties.add_group_user_map (:new.group_id, :new.member_id);
end;
/ 
show errors

create or replace trigger group_mem_tr_idx_dnm_pa_trg
  after delete
  on group_member_trans_index 
  for each row
begin
    dnm_parties.delete_group_member_map(:old.group_id, :old.member_id);
end;
/
show errors



create or replace trigger parties_dnm_pa_del_trg
  after delete
  on parties
  for each row
begin
    dnm_parties.delete_group_member_map(:old.party_id, :old.party_id);
end;
/
show errors

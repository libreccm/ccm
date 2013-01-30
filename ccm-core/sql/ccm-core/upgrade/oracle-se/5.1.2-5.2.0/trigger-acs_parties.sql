--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: trigger-acs_parties.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


create or replace trigger groups_in_tr
after insert on groups
for each row
begin
    parties_denormalization.add_group(:new.group_id);
end;
/
show errors

-- Subgroup triggers

create or replace trigger group_subgroup_in_tr
after insert on group_subgroup_map
for each row
begin
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_del_tr
after delete on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_up_tr
after update on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

-- Membership triggers

create or replace trigger group_member_in_tr
after insert on group_member_map
for each row
begin
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors

create or replace trigger group_member_del_tr
after delete on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
end;
/
show errors

create or replace trigger group_member_up_tr
after update on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors

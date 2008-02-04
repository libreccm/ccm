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


create or replace function parties_groups_in_fn () returns opaque as '
begin
  perform hierarchy_add_item(new.group_id, ''group_subgroup_trans_index'',
                             ''group_id'', ''subgroup_id'');
  return null;
end;' language 'plpgsql';

create trigger parties_groups_in_tr
after insert on groups
for each row execute procedure
parties_groups_in_fn();

-- Subgroup triggers


create or replace function parties_group_subgroup_in_fn () returns opaque as '
begin
  perform parties_add_subgroup(new.group_id, new.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_in_tr
after insert on group_subgroup_map
for each row execute procedure
parties_group_subgroup_in_fn();


create or replace function parties_group_subgroup_del_fn () returns opaque as '
begin
  perform parties_remove_subgroup(old.group_id, old.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_del_tr
after delete on group_subgroup_map
for each row execute procedure
parties_group_subgroup_del_fn();


create or replace function parties_group_subgroup_up_fn () returns opaque as '
begin
  perform parties_remove_subgroup(old.group_id, old.subgroup_id);
  perform parties_add_subgroup(new.group_id, new.subgroup_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_subgroup_up_tr
after update on group_subgroup_map
for each row execute procedure
parties_group_subgroup_up_fn();


-- Membership triggers

create or replace function parties_group_member_in_fn () returns opaque as '
begin
  perform parties_add_member(new.group_id, new.member_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_member_in_tr
after insert on group_member_map
for each row execute procedure
parties_group_member_in_fn();


create or replace function parties_group_member_del_fn () returns opaque as '
begin
  perform parties_remove_member(old.group_id, old.member_id);
  return null;
end;' language 'plpgsql';

create trigger parties_group_member_del_tr
after delete on group_member_map
for each row execute procedure
parties_group_member_del_fn();


create or replace function parties_group_member_up_fn () returns opaque as '
begin
  perform parties_remove_member(old.group_id, old.member_id);
  perform parties_add_member(new.group_id, new.member_id);
  return null;
end;' language 'plpgsql';

create trigger group_member_up_tr
after update on group_member_map
for each row execute procedure
parties_group_member_up_fn();

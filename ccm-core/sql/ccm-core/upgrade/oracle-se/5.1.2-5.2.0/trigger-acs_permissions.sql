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
-- $Id: trigger-acs_permissions.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


create or replace trigger object_context_in_tr
after insert on object_context
for each row
begin
  if :new.context_id is not null then
    permission_denormalization.add_context(:new.object_id, :new.context_id);
  end if;
end;
/
show errors

create or replace trigger object_context_up_tr
after update on object_context
for each row
begin
  if ((:old.context_id != :new.context_id) or
      (:old.context_id is null) or
      (:new.context_id is null)) then

    if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
    end if;
    if :new.context_id is not null then
      permission_denormalization.add_context(:new.object_id, :new.context_id);
    end if;

  end if;
end;
/
show errors

create or replace trigger object_context_del_tr
before delete on object_context
for each row
begin
  if :old.context_id is not null then
      permission_denormalization.remove_context(:old.object_id, 
                                                :old.context_id);
  end if;
end;
/
show errors

create or replace trigger acs_objects_context_in_tr
after insert on acs_objects
for each row
begin
   insert into object_context
   (object_id, context_id)
   values
   (:new.object_id, null);
end;
/
show errors

--
-- Triggers on acs_permissions to maintain above denormalizations
--

create or replace trigger acs_permissions_in_tr
after insert on acs_permissions
for each row
begin
    permission_denormalization.add_grant(:new.object_id);
end;
/
show errors

-- this trigger supports a fringe case where someone updates a
-- a grant (i.e. row in acs_permissions) and chagnes the object_id.
create or replace trigger acs_permissions_up_tr
after update on acs_permissions
for each row
begin
    if (:old.object_id != :new.object_id) then
        permission_denormalization.remove_grant(:old.object_id);
        permission_denormalization.add_grant(:new.object_id);
    end if;
end;
/
show errors

create or replace trigger acs_permissions_del_tr
after delete on acs_permissions
for each row
begin
    permission_denormalization.remove_grant(:old.object_id);
end;
/
show errors

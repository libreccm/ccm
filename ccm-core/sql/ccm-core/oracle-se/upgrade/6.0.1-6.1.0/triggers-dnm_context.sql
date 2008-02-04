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
-- $Id: triggers-dnm_context.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace trigger acs_permissions_dnm_ctx_trg
  after insert or update or delete
  on acs_permissions
  for each row
begin
  if inserting then
     dnm_context.add_grant(:new.object_id);
  elsif deleting then
     dnm_context.remove_grant(:old.object_id);
  elsif updating then
     dnm_context.remove_grant(:old.object_id);
     dnm_context.add_grant(:new.object_id);
  end if;
end;
/
show errors

create or replace trigger object_context_dnm_ctx_trg
  after insert or update or delete
  on object_context
  for each row
begin
  if inserting or updating then
     dnm_context.change_context(:new.object_id, :new.context_id);
   else
     dnm_context.change_context(:old.object_id, null);
   end if;
end;
/
show errors

create or replace trigger acs_objects_dnm_ctx_in_trg
  before insert
  on acs_objects
  for each row
begin
     dnm_context.add_object(:new.object_id,null);
end;
/

create or replace trigger acs_objects_dnm_ctx_aftin_trg
  after insert
  on acs_objects
  for each row
begin
      insert into object_context (object_id, context_id)
        values (:new.object_id, null);
end;
/


create or replace trigger acs_objects_dnm_ctx_del_trg
  after delete
  on acs_objects
  for each row
begin
     dnm_context.drop_object(:old.object_id);
end;
/

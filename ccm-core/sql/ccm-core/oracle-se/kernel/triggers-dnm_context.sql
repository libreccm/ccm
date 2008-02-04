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
-- $Id: triggers-dnm_context.sql 1586 2007-05-31 13:05:10Z chrisgilbert23 $
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

-- dnm entries created defensively when required rather than for every object
-- so may need to add now - cg
CREATE OR REPLACE TRIGGER acs_permission_dnm_ob_trg  
    before insert  
    on acs_permissions  
    for each row  
declare  
    dnm_count integer;  
begin  
    select count(*)  
    into dnm_count  
    from dnm_object_1_granted_context  
    where pd_object_id = :new.object_id;  
    if dnm_count = 0 then  
        dnm_context.add_object(:new.object_id,null);  
    end if;  
end;
/
show errors

-- after context is deleted, acs object will be deleted
-- and so drop_object procedure is called then. This trigger
-- ensures dnm hirarchy is tidy before the delete   
CREATE OR REPLACE TRIGGER obj_contxt_dnm_del_ctx_trg  
    after delete  
  on object_context
  for each row
declare  
    dnm_count integer;  
begin
    select count(*)  
    into dnm_count  
    from dnm_object_1_granted_context  
    where pd_object_id = :old.object_id;  
    if dnm_count <> 0 then  
     dnm_context.change_context(:old.object_id, null);
   end if;
end;
/
show errors

-- create dnm entries for object and/or context when context 
-- is updated (object context always initially created as null) 
CREATE OR REPLACE TRIGGER object_context_dnm_ctx_trg  
    after update  
    on object_context  
  for each row
declare  
    dnm_count integer;  
begin
    if nvl(:old.context_id,0) <> nvl(:new.context_id,0) THEN  
        select count(*)  
        into dnm_count  
        from dnm_object_1_granted_context  
        where pd_object_id = :new.object_id;  
        if dnm_count = 0 THEN  
     dnm_context.add_object(:new.object_id,null);
        end if;  
        if nvl(:new.context_id, 0) <> 0 THEN 
            select count(*)  
            into dnm_count  
            from dnm_object_1_granted_context  
            where pd_object_id = :new.context_id;  
            if dnm_count = 0 THEN  
                dnm_context.add_object(:new.context_id,null);  
            end if;  
        end if;  
        dnm_context.change_context(:new.object_id, :new.context_id);  
    end if;  
end;
/
show errors

create or replace trigger acs_objects_dnm_ctx_aftin_trg
  after insert
  on acs_objects
  for each row
begin
      insert into object_context (object_id, context_id)
        values (:new.object_id, null);
end;
/
show errors

-- should also drop object if context becomes null and  
-- there are no permissions for object, to keep 
-- dnm_object_1_granted_context clean
-- BUT in reality, if an access controlled object is 
-- not deleted then whatever happens it is very unlikely 
-- to become an object with no permissions or context
-- hence leave as is.
create or replace trigger acs_objects_dnm_ctx_del_trg
  after delete
  on acs_objects
  for each row
begin
     dnm_context.drop_object(:old.object_id);
end;
/
show errors
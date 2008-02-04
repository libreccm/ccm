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

create or replace function acs_object_dnm_ctx_add_fn () 
  returns trigger as '
  begin
      perform dnm_context_add_object(new.object_id,0);
      insert into object_context (object_id, context_id)
        values (new.object_id, null);
    return new;
  end; ' 
  language 'plpgsql'
;

create or replace function acs_object_dnm_ctx_del_fn () 
  returns trigger as '
  begin
      perform dnm_context_drop_object(old.object_id);
    return old;
  end; ' 
  language 'plpgsql'
;

create trigger acs_object_dnm_ctx_add_trg
  after insert  on acs_objects
  for each row 
  execute procedure acs_object_dnm_ctx_add_fn()
;

create trigger acs_object_dnm_ctx_del_trg
  before delete on acs_objects
  for each row 
  execute procedure acs_object_dnm_ctx_del_fn();
;

create or replace function object_context_dnm_fn ()
  returns trigger as '
  declare 
  begin
    if TG_OP = ''INSERT'' then
      perform dnm_context_change_context(new.object_id, new.context_id);
      return new;
    elsif  TG_OP = ''UPDATE'' then 
      if coalesce(new.context_id,0) != coalesce(old.context_id,0) then
         -- do not call change context if new/old context values are null or equal 
         perform dnm_context_change_context(new.object_id, new.context_id);
      end if;
         return new;      
    else 
      perform dnm_context_change_context(old.object_id, 0);
      return old;
    end if;    
  end; ' language 'plpgsql'
;

create trigger object_context_dnm_trg
  after insert or update or delete 
  on object_context
  for each row 
  execute procedure object_context_dnm_fn();
;

create or replace function acs_permissions_dnm_ctx_fn ()
  returns trigger as '
  declare
  begin 
    if TG_OP = ''INSERT'' then
      perform dnm_context_add_grant(new.object_id);
    elsif TG_OP = ''DELETE'' then
      perform dnm_context_drop_grant(old.object_id);
    elsif new.object_id <> old.object_id then
      perform dnm_context_drop_grant(old.object_id);
      perform dnm_context_add_grant(new.object_id);
    end if;
    return null;
  end; ' language 'plpgsql'
;

create trigger acs_permissions_dnm_ctx_trg
  after insert or delete or update 
  on acs_permissions
  for each row 
  execute procedure acs_permissions_dnm_ctx_fn();
;

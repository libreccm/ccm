--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: triggers-cms_items_ancestors.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

-- Authors: Daniel Berrange  (berrange@redhat.com)   [PL/pgSQL version]
--          Vadim Nasardinov (vadimn@redhat.com)     [PL/SQL port]
--          Aram Kananov     (akananov@redhat.com)   [suggested statement triggers]
-- Since:  2004-01-15
-- See:    https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=109718#c14
--         https://bugzilla.redhat.com/bugzilla/attachment.cgi?id=97029&action=view


-- This table is only need for Oracle.  It allows us to avoid
-- ORA-04091:
-- http://otn.oracle.com/pls/db92/db92.drilldown?word=ORA-04091
-- The basic problem is that when CMS_ITEMS.PARENT_ID in one row
-- changes, we may need to change CMS_ITEMS.ANCESTORS in a bunch of
-- other rows.  This would require reading/writing the CMS_ITEMS table
-- which is being mutated at the time our trigger is fired, leading to
-- the above-mentioned error.  To avoid this, we introduce an auxilliary
-- table CMS_ITEMS_AUX.
-- See
-- https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=109718#c22
create table cms_items_aux (
  item_id	  integer
                  constraint cms_items_aux_item_id_fk references
		  cms_items
		  constraint cms_items_aux_pk primary key,
  ancestors       varchar(3209),
  is_changed      varchar(1) default '0'
                  constraint cms_items_is_changed_nn not null
);

-- note for oracle EE: using a bitmap index here would be better
create index cms_items_aux_is_changed_idx on cms_items_aux(is_changed);

create index cms_items_aux_ancestors_idx on cms_items_aux(ancestors);

-- The global variable cms_items_denorm.is_changed is used for passing]
-- information from the before-update for-each-row trigger
-- cms_items_ancestors_up_trg into the after-update statement trigger
-- cms_items_ancestors_stmt_trg.  Without this variable, the latter
-- trigger would fire itself recursively.  This variable provides a
-- means to establish the base case for recursion.
create or replace package cms_items_denorm as
  is_changed  number;
end cms_items_denorm;
/
show errors

create or replace package body cms_items_denorm as
  is_changed  number := null;
end cms_items_denorm;
/
show errors

--
-- Triggers
--
create or replace trigger cms_items_ancestors_be_in_trg
  before insert
  on cms_items
  for each row
declare
  parents_ancestors cms_items.ancestors%TYPE;
begin
  if :new.parent_id is null then
    :new.ancestors := :new.item_id || '/';
  else
    select ancestors into parents_ancestors
    from cms_items
    where item_id = :new.parent_id;

    :new.ancestors := parents_ancestors || :new.item_id || '/';
  end if;
end cms_items_ancestors_be_in_trg;
/
show errors

create or replace trigger cms_items_ancestors_af_in_trg
  after insert
  on cms_items
  for each row
begin
  insert into cms_items_aux
    (item_id, ancestors)
  values
    (:new.item_id, :new.ancestors);
end cms_items_ancestors_af_in_trg;
/
show errors

create or replace trigger cms_items_ancestors_up_trg
  before update
  on cms_items
  for each row
declare
  parents_ancestors cms_items.ancestors%TYPE;
begin
  if :new.parent_id is null and :old.parent_id is null then
    return;
  end if;
  if :new.parent_id = :old.parent_id then
    return;
  end if;

  if :new.parent_id is null then
    :new.ancestors := :new.item_id || '/';
  else
    select ancestors into parents_ancestors
    from cms_items_aux
    where item_id = :new.parent_id;

    :new.ancestors := parents_ancestors  || :new.item_id  || '/';
  end if;

  update cms_items_aux
  set
    ancestors = :new.ancestors,
    is_changed = '1'
  where item_id = :new.item_id;

  update cms_items_aux
  set
    ancestors = :new.ancestors || substr(ancestors, length(:old.ancestors)+1),
    is_changed = '1'
  where ancestors like :old.ancestors || '%' and item_id <> :new.item_id;

  cms_items_denorm.is_changed := 1;
end cms_items_ancestors_up_trg;
/
show errors

create or replace trigger cms_items_ancestors_u_stmt_trg
  after update
  on cms_items
begin
  if cms_items_denorm.is_changed is null then
    return;
  end if;

  -- this prevents re-entrant calls
  cms_items_denorm.is_changed := null;

  for record in (
    select item_id, ancestors from cms_items_aux where is_changed = '1'
  ) loop
    -- Although this update does recursively trigger calls to both
    -- cms_items_ancestors_up_trg and cms_items_ancestors_stmt_trg,
    -- the recursion is nipped in the bud:
    -- 1. The trigger cms_items_ancestors_up_trg does not do anything,
    --    because we are not modifying parent_id.
    -- 2. The trigger cms_items_ancestors_stmt_trg does not do
    --    anything, because is_changed is null when the trigger fires
    --    recursively.
    update cms_items
    set ancestors = record.ancestors
    where item_id = record.item_id;
  end loop;

  update cms_items_aux
  set is_changed = '0'
  where is_changed = '1';
end cms_items_ancestors_u_stmt_trg;
/
show errors

create or replace trigger cms_items_ancestors_d_stmt_trg
  after delete
  on cms_items
  for each row
begin
  delete from cms_items_aux
  where item_id = :old.item_id;
end cms_items_ancestors_d_stmt_trg;
/
show errors

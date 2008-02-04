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
-- $Id: update-lifecycles.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- remove the referential integrity constraint from ACS_OBJECT_LIFECYCLE_MAP.OBJECT_ID
DECLARE
  ref_cons_name VARCHAR(30);
  drop_cmd      VARCHAR(100);
BEGIN
  select c.constraint_name into ref_cons_name
    from user_constraints c,
         user_cons_columns r1,
         user_cons_columns r2
   where c.constraint_type = 'R'
     and c.constraint_name = r1.constraint_name
     and r1.table_name = 'ACS_OBJECT_LIFECYCLE_MAP'
     and r1.column_name = 'OBJECT_ID'
     and c.r_constraint_name = r2.constraint_name
     and r2.table_name = 'ACS_OBJECTS'
     and r2.column_name = 'OBJECT_ID';
  if SQL%NOTFOUND then
    raise_application_error(-20000, 'Constraint name for ACS_OBJECT_LIFECYCLE_MAP not found.  ' ||
                                    'This script may have already been run, please verify.');
  end if;

  drop_cmd := 'alter table acs_object_lifecycle_map drop constraint ' || ref_cons_name;

  execute immediate drop_cmd;

EXCEPTION
  when NO_DATA_FOUND then
    raise_application_error(-20000, 'Constraint name for ACS_OBJECT_LIFECYCLE_MAP not found.  ' ||
                                    'This script may have already been run, please verify.');
END;
/

-- add the new ITEM_ID column
alter table acs_object_lifecycle_map add (
  item_id INTEGER
          constraint acs_obj_lif_map_ite_id_f_2cb3k
          references acs_objects(object_id) 
);

-- set the ITEM_ID column to the old OBJECT_ID
update acs_object_lifecycle_map
   set item_id = object_id;

-- make the updated ITEM_ID column not null
alter table acs_object_lifecycle_map modify (
  item_id constraint acs_obj_cycle_map_item_id_nn
          not null);

-- Update ACS_OBJECT_LIFECYCLE_MAP.OBJECT_ID with new, unused
-- ids.  This is not technically necessary, but we do it for clarity.
update acs_object_lifecycle_map
   set object_id = acs_object_id_seq.nextval;

-- remove the unused VERSION_TAG column
alter table acs_object_lifecycle_map
 drop (version_tag);

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


-- add the new ITEM_ID column
alter table acs_object_lifecycle_map
  add item_id INTEGER
      constraint acs_obj_cycle_map_item_id_fk
        references acs_objects(object_id);

-- set the ITEM_ID column to the old OBJECT_ID
update acs_object_lifecycle_map
   set item_id = object_id;

-- make the updated ITEM_ID column not null
alter table acs_object_lifecycle_map
  alter column item_id set not null;

-- Update ACS_OBJECT_LIFECYCLE_MAP.OBJECT_ID with new, unused
-- ids.  This is not technically necessary, but we do it for clarity.
update acs_object_lifecycle_map
   set object_id = nextval('acs_object_id_seq');

-- remove the unused VERSION_TAG column
alter table acs_object_lifecycle_map
  drop column version_tag;

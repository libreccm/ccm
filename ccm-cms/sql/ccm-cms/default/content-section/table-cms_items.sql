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
-- $Id: table-cms_items.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_items (
  item_id	  integer
                  constraint cms_items_item_id_fk references
		  acs_objects
		  constraint cms_items_pk primary key,
  parent_id	  integer
                  constraint cms_items_parent_id_fk references
		  acs_objects,
  name		  varchar(200)
		  constraint cms_items_name_nil
                  not null,
  type_id         integer
		  constraint cms_items_type_id_fk
                  references content_types,
  version         varchar(200) not null
                  constraint cms_items_version_ck
                  check (version in ('live', 'draft', 'pending', 'archived')),
  language        char(2),
  additional_info varchar(1024),
  section_id      integer,
                  -- Do not add fk constraints on a denormalized column.
                  --constraint cms_items_section_id_fk
                  --references content_sections on delete cascade,
  ancestors       varchar(3209),
  master_id       integer
                  constraint master_id_fk references cms_items
);

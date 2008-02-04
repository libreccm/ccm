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
-- $Id: table-cat_object_category_map.sql 1151 2006-06-07 21:57:40Z apevec $
-- $DateTime: 2004/08/16 18:10:38 $

create table cat_object_category_map (
  category_id             integer
                          constraint cat_obj_cat_map_cat_id_fk
                          references cat_categories,
  object_id               integer
                          constraint cat_obj_map_object_id_fk
                          references acs_objects,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_obj_map_default_p_ck
                          check(default_p in ('0','1')),
  index_p                 char(1)
                          constraint cat_obj_map_index_p_ck
                          check(index_p in ('0','1')),
  auto_p                  char(1) default '0'
                          constraint cat_obj_map_auto_p_ck
                          check(auto_p in ('0','1')),
  sort_key                integer,
  constraint cat_obj_cat_map_ckone
  check(not category_id = object_id),
  constraint cat_obj_cat_map_un
  unique(category_id, object_id)
);

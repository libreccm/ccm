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
-- $Id: table-cat_category_category_map.sql 1084 2006-01-17 17:09:52Z apevec $
-- $DateTime: 2004/08/16 18:10:38 $

create table cat_category_category_map (
  category_id             integer
                          constraint cat_cat_map_parent_id_fk
                          references cat_categories,
  related_category_id     integer
                          constraint cat_cat_map_category_id_fk
                          references cat_categories,
    -- this should default to the JDBC version of true
  default_p               char(1)
                          constraint cat_cat_map_default_p_ck
                          check(default_p in ('0','1')),
  sort_key                integer,
  relation_type           varchar(10) 
                          constraint cat_cat_map_rel_type_ck
                          check(relation_type in ('child','related','preferred')),
  constraint cat_cat_cat_map_ckone
  check(not category_id = related_category_id),
  constraint cat_cat_catmap_un
  unique(category_id, related_category_id)
);

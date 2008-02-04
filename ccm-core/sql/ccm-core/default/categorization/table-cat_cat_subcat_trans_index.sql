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
-- $Id: table-cat_cat_subcat_trans_index.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


-- See also: //core-platform/dev/sql/ccm-core/default/kernel/table-group_subgroup_trans_index.sql

create table cat_cat_subcat_trans_index (
      category_id   integer
                    constraint cat_cat_subcat_index_c_nn 
                    not null
                    constraint cat_cat_subcat_index_c_fk
                    references cat_categories(category_id) on delete cascade, 
      subcategory_id integer
                    constraint cat_cat_subcat_index_s_nn 
                    not null
                    constraint cat_cat_subcat_index_s_fk
                    references cat_categories(category_id) on delete cascade, 
      n_paths       integer 
                    constraint cat_cat_subcat_index_n_nn
                    not null,
      constraint cat_cat_subcat_index_pk primary key(category_id, subcategory_id),
      -- This prevents circularity in the category-subcategory graph.
      -- If category_id=subcategory_id, then n_paths=0.
	  constraint cat_subcat_circularity_ck 
                 check ( category_id != subcategory_id or n_paths=0 ),
      -- This constraint makes sure that we never forget to delete rows when
      -- we decrement n_paths.  n_paths should never reach 0 except for
      -- mappings where category_id=subcategory_id (in which case n_paths should
      -- always be 0 due to above constraint).
      constraint cat_cat_subcat_n_paths_ck
                 check (n_paths>0 or category_id=subcategory_id)
);

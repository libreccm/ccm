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
-- $Id: cms_category_index_item_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_category_index_item_map (
    item_id INTEGER not null,
        -- referential constraint for item_id deferred due to circular dependencies
    category_id INTEGER not null,
        -- referential constraint for category_id deferred due to circular dependencies
    constraint cms_cat_ind_ite_map_ca_p_klihs
      primary key(category_id, item_id)
);

alter table cms_category_index_item_map add 
    constraint cms_cat_ind_ite_map_ca_f_5b_0c foreign key (category_id)
      references cat_categories(category_id) on delete cascade;
alter table cms_category_index_item_map add 
    constraint cms_cat_ind_ite_map_it_f_gpmkj foreign key (item_id)
      references cms_items(item_id) on delete cascade;

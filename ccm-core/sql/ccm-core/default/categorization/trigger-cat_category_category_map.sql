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
-- $Id: trigger-cat_category_category_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

-- Triggers to maintain denormalizations

create or replace trigger cat_categories_in_tr
after insert on cat_categories
for each row
begin
     hierarchy_add_item(:new.category_id, 'cat_cat_subcat_trans_index',
                        'category_id', 'subcategory_id');
end;
/
show errors


-- Subgroup triggers

create or replace trigger cat_category_cat_map_in_tr
after insert on cat_category_category_map
for each row
begin
     if (:new.relation_type = 'child') then
        hierarchy_add_subitem(:new.category_id, :new.related_category_id, 
                           'cat_cat_subcat_trans_index',
                           'category_id', 'subcategory_id');
     end if;
end;
/
show errors;



create or replace trigger cat_category_cat_map_del_tr
after delete on cat_category_category_map
for each row
begin
     hierarchy_remove_subitem(:old.category_id, :old.related_category_id,
                              'cat_cat_subcat_trans_index',      
                              'category_id', 'subcategory_id');
end;
/ 
show errors;



create or replace trigger cat_category_cat_map_up_tr
after update on cat_category_category_map
for each row
begin
  if (:old.relation_type = 'child') then
     hierarchy_remove_subitem(:old.category_id, :old.related_category_id,
                              'cat_cat_subcat_trans_index',      
                              'category_id', 'subcategory_id');
  end if;
  if (:new.relation_type = 'child') then
     hierarchy_add_subitem(:new.category_id, :new.related_category_id, 
                           'cat_cat_subcat_trans_index',
                           'category_id', 'subcategory_id');
  end if;
end;
/ 
show errors;

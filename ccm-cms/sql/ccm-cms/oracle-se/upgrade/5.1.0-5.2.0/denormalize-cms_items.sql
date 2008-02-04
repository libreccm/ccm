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
-- $Id: denormalize-cms_items.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

alter table cms_items add (ancestors varchar(4000));

create or replace procedure updateCMSItemsHelpTEMP(v_item_id INTEGER)
as 
   cursor parents is 
             select item_id
               from cms_items
                    start with item_id = v_item_id
                    connect by item_id = prior parent_id;
   v_parent_id integer;
begin
   update cms_items set ancestors = item_id || '/'
          where item_id = v_item_id;
   open parents;
   loop
      FETCH parents INTO v_parent_id;
      EXIT WHEN parents%NOTFOUND;
      
      if (v_parent_id != v_item_id) then
         update cms_items set ancestors = v_parent_id || '/' || ancestors where item_id = v_item_id;
      end if;
   end loop;
end;
/
show errors

create or replace procedure updateCMSItemsTEMP
as 
   cursor items is select item_id from cms_items;
   v_item_id integer;
begin
   open items;
   loop
      FETCH items INTO v_item_id;
      EXIT WHEN items%NOTFOUND;
      updateCMSItemsHelpTEMP(v_item_id);
   end loop;
end;
/
show errors

begin
  updateCMSItemsTEMP;
end;
/

drop procedure updateCMSItemsTEMP;
drop procedure updateCMSItemsHelpTEMP;

alter table cms_items modify (ancestors not null);

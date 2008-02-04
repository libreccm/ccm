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
-- $Id: table-cms_item_template_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_item_template_map (
  mapping_id   integer 
               constraint cms_itm_mapping_id_fk references 
               acs_objects,
  item_id      integer 
               constraint cms_itm_item_id_fk references 
               cms_items,
  template_id  integer 
               constraint cms_itm_template_id_fk references 
               cms_templates,
  use_context  varchar(200) default 'public'
               constraint cms_itm_use_ctx_fx references       
               cms_template_use_contexts,
  constraint cms_item_template_map_pk primary key (mapping_id)
);

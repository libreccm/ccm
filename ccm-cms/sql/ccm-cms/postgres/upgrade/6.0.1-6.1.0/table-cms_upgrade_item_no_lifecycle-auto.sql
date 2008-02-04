--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: table-cms_upgrade_item_no_lifecycle-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create table cms_upgrade_item_no_lifecycle (
    item_id INTEGER not null
        constraint cms_upg_ite_no_lif_ite_p_v85hc
          primary key
        -- referential constraint for item_id deferred due to circular dependencies
);

alter table cms_upgrade_item_no_lifecycle add 
    constraint cms_upg_ite_no_lif_ite_f_btv6p foreign key (item_id)
      references cms_items(item_id);

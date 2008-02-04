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
-- $Id: table-cms_links-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create table cms_links (
    link_id INTEGER not null
        constraint cms_links_link_id_p_agonk
          primary key,
        -- referential constraint for link_id deferred due to circular dependencies
    title VARCHAR(200) not null,
    description VARCHAR(4000),
    type VARCHAR(20) not null,
    target_uri VARCHAR(250),
    target_item_id INTEGER,
        -- referential constraint for target_item_id deferred due to circular dependencies
    target_window VARCHAR(50),
    link_order INTEGER
);

alter table cms_links add
    constraint cms_link_targe_item_id_f_xe__d foreign key (target_item_id)
      references cms_items(item_id);
alter table cms_links add
    constraint cms_links_link_id_f_1ljfs foreign key (link_id)
      references acs_objects(object_id);

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
-- $Id: table-cms_published_links-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create table cms_published_links (
    draft_target INTEGER not null,
        -- referential constraint for draft_target deferred due to circular dependencies
    property_name VARCHAR(100) not null,
    pending_source INTEGER not null,
        -- referential constraint for pending_source deferred due to circular dependencies
    pending INTEGER not null,
        -- referential constraint for pending deferred due to circular dependencies
    constraint cms_pub_lin_dra_tar_pe_p_vujkb
      primary key(pending, pending_source, property_name, draft_target)
);

alter table cms_published_links add
    constraint cms_pub_lin_dra_target_f_eyxgj foreign key (draft_target)
      references cms_items(item_id);
alter table cms_published_links add
    constraint cms_pub_lin_pen_source_f_k9ttn foreign key (pending_source)
      references acs_objects(object_id);
alter table cms_published_links add
    constraint cms_publis_lin_pending_f_kkfem foreign key (pending)
      references cms_items(item_id);

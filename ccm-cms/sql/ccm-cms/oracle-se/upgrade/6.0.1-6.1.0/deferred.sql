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
-- $Id: deferred.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
alter table cms_links add
    constraint cms_link_targe_item_id_f_xe__d foreign key (target_item_id)
      references cms_items(item_id);
alter table cms_links add
    constraint cms_links_link_id_f_1ljfs foreign key (link_id)
      references acs_objects(object_id);
alter table cms_published_links add
    constraint cms_pub_lin_dra_target_f_eyxgj foreign key (draft_target)
      references cms_items(item_id);
alter table cms_published_links add
    constraint cms_pub_lin_pen_source_f_k9ttn foreign key (pending_source)
      references acs_objects(object_id);
alter table cms_published_links add
    constraint cms_publis_lin_pending_f_kkfem foreign key (pending)
      references cms_items(item_id);
alter table cms_upgrade_item_lifecycle_map add
    constraint cms_upg_ite_lif_map_it_f_xz63e foreign key (item_id)
      references cms_items(item_id);
alter table cms_upgrade_item_lifecycle_map add
    constraint cms_upg_ite_lif_map_li_f_in75l foreign key (lifecycle_id)
      references lifecycles(cycle_id);
alter table cms_upgrade_item_no_lifecycle add
    constraint cms_upg_ite_no_lif_ite_f_btv6p foreign key (item_id)
      references cms_items(item_id);

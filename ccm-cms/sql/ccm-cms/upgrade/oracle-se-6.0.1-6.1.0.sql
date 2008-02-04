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
-- $Id: oracle-se-6.0.1-6.1.0.sql 1164 2006-06-14 10:59:54Z fabrice $
-- $DateTime: 2004/08/17 23:15:09 $

PROMPT Red Hat Enterprise CMS 6.0.1 -> 6.1.0 Upgrade Script (Oracle)

@@ ../oracle-se/upgrade/6.0.1-6.1.0/denormalize-versioning-quick.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/add-mpa-page-break.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/alter-table-authoring_steps.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/alter-table-cms_form_item.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/insert-new-privileges.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/create-cms_user_home_folder_map.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/update-cat-authoring.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/triggers-cms_items_ancestors.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-cms_upgrade_progress-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-cms_links-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-cms_published_links-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-cms_upgrade_item_no_lifecycle-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/table-cms_upgrade_item_lifecycle_map-auto.sql
@@ ../oracle-se/upgrade/6.0.1-6.1.0/deferred.sql

alter table content_sections drop column content_expiration_digest_id;
alter table publish_to_fs_files drop constraint publ_to_fs_fil_fil_nam_u_3zkgd;

create index cms_links_target_item_id_idx on cms_links(target_item_id);
create index cms_pub_links_draft_tgt_idx on cms_published_links(draft_target);
create index cms_pub_links_pending_src_idx on cms_published_links(pending_source);
create index cms_upgrd_itm_lc_mp_lc_id_idx on cms_upgrade_item_lifecycle_map(lifecycle_id);
create index cms_usr_home_fdr_mp_fdr_id_idx on cms_user_home_folder_map(folder_id);
create index cms_usr_home_fdr_mp_sec_id_idx on cms_user_home_folder_map(section_id);
create index cms_usr_home_fdr_mp_usr_id_idx on cms_user_home_folder_map(user_id);

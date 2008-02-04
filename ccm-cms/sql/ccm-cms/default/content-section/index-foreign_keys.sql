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
-- $Id: index-foreign_keys.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create index CMS_UPGRD_ITM_LC_MP_LC_ID_idx on CMS_UPGRADE_ITEM_LIFECYCLE_MAP(LIFECYCLE_ID);
create index CMS_LINKS_TARGET_ITEM_ID_idx on CMS_LINKS(TARGET_ITEM_ID);
create index CMS_PUB_LINKS_DRAFT_TGT_idx on CMS_PUBLISHED_LINKS(DRAFT_TARGET);
create index CMS_PUB_LINKS_PENDING_SRC_idx on CMS_PUBLISHED_LINKS(PENDING_SOURCE);
create index CMS_USR_HOME_FDR_MP_FDR_ID_idx on CMS_USER_HOME_FOLDER_MAP(FOLDER_ID);
create index CMS_USR_HOME_FDR_MP_SEC_ID_idx on CMS_USER_HOME_FOLDER_MAP(SECTION_ID);
create index CMS_USR_HOME_FDR_MP_USR_ID_idx on CMS_USER_HOME_FOLDER_MAP(USER_ID);
create index INIT_REQS_REQD_INIT_idx on INIT_REQUIREMENTS(REQUIRED_INIT);

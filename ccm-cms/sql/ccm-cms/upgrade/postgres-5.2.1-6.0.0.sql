--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: postgres-5.2.1-6.0.0.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

\echo Red Hat Enterprise CMS 5.2.1 -> 6.0.0 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/5.2.1-6.0.0/drop-ri-triggers.sql
\i ../postgres/upgrade/5.2.1-6.0.0/drop-unique-indexes.sql
\i ../postgres/upgrade/5.2.1-6.0.0/convert-content-section-to-app.sql
\i ../postgres/upgrade/5.2.1-6.0.0/convert-to-multilingual.sql
\i ../postgres/upgrade/5.2.1-6.0.0/update-publish-to-fs.sql
\i ../postgres/upgrade/5.2.1-6.0.0/update-item-versions.sql
\i ../default/upgrade/5.2.1-6.0.0/create-cms_form_section_wrapper.sql
\i ../default/upgrade/5.2.1-6.0.0/insert-cms_item_admin_privileges.sql
\i ../default/upgrade/5.2.1-6.0.0/drop-category-index.sql
\i ../default/upgrade/5.2.1-6.0.0/misc.sql
\i ../postgres/upgrade/5.2.1-6.0.0/cms_category_template_map.sql
\i ../postgres/upgrade/5.2.1-6.0.0/update-lifecycles.sql
\i ../postgres/upgrade/5.2.1-6.0.0/misc.sql
\i ../default/upgrade/5.2.1-6.0.0/update-reusable-image-assets-acs_objects.sql
\i ../default/upgrade/5.2.1-6.0.0/add-ct_mp_sectio_art_sect_rank_idx.sql
\i ../default/upgrade/5.2.1-6.0.0/add-cms_items_name_id_version_parent_index.sql
\i ../postgres/upgrade/5.2.1-6.0.0/auto-upgrade.sql

commit;

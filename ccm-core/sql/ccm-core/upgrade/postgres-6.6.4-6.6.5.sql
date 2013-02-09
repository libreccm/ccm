--
-- Copyright (C) 2013 Peter Boy All Rights Reserved.
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
-- $DateTime: 2013/01/31 23:15:09 $

\echo Red Hat Enterprise CORE 6.6.4 -> 6.6.5 Upgrade Script (PostgreSQL)

-- 
-- 

begin;

-- Remove old style application tables, must be the first script executed.
\i default/6.6.4-6.6.5/remove_old_style_app_tables.sql

-- Adjust table cat_object_root_category_map 
-- (Constraint cat_obj_package_id_fk already removed in step 1)
\i default/6.6.4-6.6.5/remove_old_app_entries_catobjectroot_table.sql

-- Adjust table applications
-- (Constraint apapplication_package_id_f_cdaho already removed in step 1)
\i default/6.6.4-6.6.5/remove_old_app_entries_applications_table.sql

-- Adjust table application_types
-- (Constraint applica_typ_pac_typ_id_f_v80ma already removed in step 1)
\i default/6.6.4-6.6.5/remove_old_app_entries_applicationtypes_table.sql

-- Remove admin.sitemap application table
-- Check: Already done in 6.6.4
-- \i default/6.6.4-6.6.5/remove_sitemap_app_table.sql


commit;
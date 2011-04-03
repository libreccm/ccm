--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $DateTime: 2010/11/10 23:15:09 $

\echo Red Hat Enterprise ccm-portalworkspace 6.6.0 -> 6.6.1 Upgrade Script (PostgreSQL)

begin;

\i default/6.6.0-6.6.1/ren_table_workspaces.sql
\i default/6.6.0-6.6.1/ren_table_workspace_pages.sql
\i default/6.6.0-6.6.1/ren_table_workspace_page_layouts.sql
\i default/6.6.0-6.6.1/ren_table_workspace_workspace_map.sql
\i default/6.6.0-6.6.1/ren_table_workspace_themes.sql
\i default/6.6.0-6.6.1/ren_table_themeapplications.sql

\i default/6.6.0-6.6.1/add_constraints.sql

\i default/6.6.0-6.6.1/upd_system_tables.sql

commit;

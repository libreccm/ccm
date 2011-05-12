--
-- Copyright (C) 2010 Peter Boy. All Rights Reserved.
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
-- $Id: postgres-6.6.0-6.6.1.sql  $

-- NavigationTreePortlet has been moved from ccm-ldn-portal (where it existed as
-- NavigationDirectoryPortlet) to ccm-ldn-navigation because it depends on
-- navigation and was misplaced there (generating horizontal dependencies).


begin;

\i postgres/6.6.0-6.6.1/upd_nav_tree_portlet.sql
\i postgres/6.6.0-6.6.1/upd_acs_objects.sql
\i postgres/6.6.0-6.6.1/upd_application_tables.sql

commit;

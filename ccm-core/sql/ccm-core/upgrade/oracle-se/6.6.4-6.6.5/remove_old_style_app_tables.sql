--
-- Copyright (C) 2013 Peter Boy. All Rights Reserved.
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
-- $Id: remove_old_style_app_tables.sql  $

-- Old style application code in kernel.Packages etc and kernel.SiteNode
-- is no longer used and the corresponding tables are to be removed.
-- This update must be executed AFTER all applications are migrated to new
-- style.


-- drop tables
drop table apm_package_type_listener_map ;
drop table apm_listeners ;
drop table site_nodes;
drop table apm_packages CASCADE CONSTRAINTS;
drop table apm_package_types CASCADE CONSTRAINTS;

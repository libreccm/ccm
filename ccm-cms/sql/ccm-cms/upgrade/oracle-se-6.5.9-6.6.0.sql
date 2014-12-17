--
-- Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
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

-- This is part 1 of an alternative upgrade path for the upgrade 6.6.0-6.6.1. The upgrade
-- 6.6.0-6.6.1 requires that ccm-cms-assets-imagestep is installed. But cmc-cms-assets-imagestep
-- can't be installed before 6.6.0-6.6.1 because 6.6.0-6.6.1 alters some important tables used
-- when calling ccm load. This upgrade and the upgrade 6.5.9-6.6.1 split the 6.6.0-6.6.1 upgrade
-- into two parts. This part, 6.5.9-6.6.0 is altering the tables needed by ccm load. After this
-- upgrade you can install ccm-cms-assets-imagestep using ccm load ccm-cms-assets-imagestep. 
-- The upgrade ccm-cms-6.5.9-6.6.1 contains the remaining parts of the 6.6.0-6.6.1 upgrade 
-- especially the part which requires ccm-cms-assets-imagestep.

PROMPT Red Hat Enterprise CMS 6.5.9 -> 6.6.0 Upgrade Script (Oracle)

@@ ../default/upgrade/6.6.0-6.6.1/upd_table_cms_rel_links.sql
@@ ../oracle-se/upgrade/6.6.0-6.6.1/upd_table_content_types.sql

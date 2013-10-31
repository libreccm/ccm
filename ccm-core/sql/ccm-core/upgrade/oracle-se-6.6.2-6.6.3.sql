--
-- Copyright (C) 2008 Peter Boy All Rights Reserved.
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
-- $Id: oracle-se-6.6.2-6.6.3.sql 293 2011-01-07 15:10:39Z pboy $

PROMPT Red Hat Enterprise CORE 6.6.1 -> 6.6.2 Upgrade Script (Oracle)

-- delete core portals entry in apm_package_types
-- there es no entry in acs_objects!

@@ default/6.6.2-6.6.3/remove_legacy_portal.sql

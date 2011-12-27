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
-- $DateTime: 2011/03/27 23:15:09 $
-- $Id: oracle-se-6.6.0-6.6.1  pboy $

-- drop table sc_app - not needed anyway
@@ default/6.6.0-6.6.1/drop_app_table.sql

-- rename application from london.shortcuts to shortcuts
@@ default/6.6.0-6.6.1/upd_system_tables.sql

-- remove legacy compatible bits
@@ default/6.6.0-6.6.1/remove_legacy_entries.sql

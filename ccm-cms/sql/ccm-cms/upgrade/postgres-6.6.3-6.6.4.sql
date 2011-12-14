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
-- $DateTime: 2011/09/06 23:15:09 $

-- This update is only applicable for the internal development tree at
-- University of Bremen !  Don't use for the APLAWS main trunk on
-- fedorahosted!

\echo ScientificCMS 6.6.3 -> 6.6.4 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.6.2-6.6.3/create_orgaunit_hierarchy_table.sql
\i ../default/upgrade/6.6.2-6.6.3/create_publish_lock_table.sql

commit;
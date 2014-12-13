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
-- $DateTime: 2010/11/10 23:15:09 $

\echo Red Hat Enterprise CORE 6.6.0 -> 6.6.1 Upgrade Script (PostgreSQL)

begin;

-- This update just removes database tables to store and manage style sheets.
-- These are not used anymore but replaces by a pattern driven style sheet
-- selection
\i default/6.6.0-6.6.1/drop_tables_acs_stylesheets.sql

-- Once while updating postgres recreation was required, while with Oracle not.
-- may have been specific to that update situation and may probably be skipped
-- in other postgres updates as well.
\i postgres/6.6.0-6.6.1/recreate_users_index.sql

commit;

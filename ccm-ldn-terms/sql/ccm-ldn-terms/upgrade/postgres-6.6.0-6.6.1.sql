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

\echo Red Hat Enterprise TERMS 6.6.0 -> 6.6.1 Upgrade Script (PostgreSQL)

begin;

-- Under unknown circumstances a constraint may get lost during updating.
-- Only needed under special conditions or when a missing constraint causes
-- errors.
\echo 'This update is only needed if the constraints are missing.'
\echo 'If the update complains about an existing constraint it is safe '
\echo 'to ignore this error message.'
\i ../default/upgrade/6.6.0-6.6.1/upd_constraints.sql

commit;

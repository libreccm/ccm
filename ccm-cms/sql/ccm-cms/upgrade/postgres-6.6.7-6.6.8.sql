--
-- Copyright (C) 2013 Jens Pelzetter All Rights Reserved.
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
-- $DateTime$
-- $Id$

-- Update: Rename com.arsdigta.cms.Workspace to com.arsdigita.cms.ContentCenter
\echo Red Hat Enterprise CMS 6.6.7 -> 6.6.8 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.6.7-6.6.8/rename_workspace_to_contentcenter.sql
\i ../postgres/upgrade/6.6.7-6.6.8/add_personsstr_column.sql
\i ../postgres/upgrade/6.6.7-6.6.8/set_singleton.sql

commit;
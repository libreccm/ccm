--
-- Copyright (C) 2014 Peter Boy All Rights Reserved.
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
-- $Id: postgres-6.6.11-6.6.12.sql pboy $

-- Update: Fixes various identifiers with could be used in PostgreSQL but not
--         in Oracle.  
--         This update is only applicable for the scientificcms tree up to 2.2
--         Don't use for the APLAWS tree. APLAWS is updated from 1.0.4
--         directly to 2.3.x (6.6.12)!

\echo Red Hat Enterprise CMS 6.6.11 -> 6.6.12 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/6.6.11-6.6.12/upd_table_content_types.sql
\i ../postgres/upgrade/6.6.11-6.6.12/upd_tables_cms_organisation.sql

commit;

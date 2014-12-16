--
-- Copyright (C) 2014 Peter Boy. All Rights Reserved.
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
-- $DateTime: 2013/04/07 23:15:09 $

\echo Red Hat Enterprise types-decisiontree 6.6.0 -> 6.6.1 Upgrade Script (PostgreSQL)

-- This update fixes some naming problems with are incompatible to Oracle.
-- Specifically 
-- * ct_decisiontree_section_options.option_id 
--   --> ct_decisiontree_section_opts.option_id
-- * ct_decisiontree_section_options.option_id
--   --> ct_decisiontree_section_opts.option_id
 
begin;

\i postgres/6.6.0-6.6.1/upd_decisiontree_tables.sql

commit;

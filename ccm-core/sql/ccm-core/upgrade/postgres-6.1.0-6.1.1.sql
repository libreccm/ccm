--
-- Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
-- $Id: postgres-6.1.0-6.1.1.sql 469 2005-03-20 23:12:44Z mbooth $
-- $DateTime: 2004/08/16 18:10:38 $

\echo Red Hat WAF 6.1.0 -> 6.1.1 Upgrade Script (PostgreSQL)

\i ../../ccm-core/postgres/upgrade/6.1.0-6.1.1/upgrade-lucene_docs.sql

\i ../fixes/formbuilder/delete-bogus-form-widgets.sql
\i ../fixes/messaging/update-thread-replies.sql

--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: postgres-6.3.4-6.4.0.sql 1358 2006-11-06 14:22:28Z sskracic $
-- $DateTime: 2004/04/07 16:07:11 $

\echo Red Hat Enterprise CMS 6.3.4 -> 6.4.0 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/6.3.4-6.4.0/role_add_approve_privilege.sql

commit;

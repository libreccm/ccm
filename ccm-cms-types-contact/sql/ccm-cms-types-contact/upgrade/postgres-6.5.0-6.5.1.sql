--
-- Copyright (C) 2007 Magpie. All Rights Reserved.
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

\echo Red Hat Enterprise CMS 6.5.0 -> 6.5.1 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.5.0-6.5.1/contact_content_item_map_table.sql

commit;

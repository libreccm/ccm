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
-- $Id: oracle-se-6.0.1-6.1.0.sql,v 1.1 2004/12/15 14:28:03 awux7820 Exp $
-- $DateTime: 2004/04/07 16:07:11 $

PROMPT Red Hat Enterprise CMS 6.3.3 -> 6.3.4 Upgrade Script (Oracle)

@@ ../default/upgrade/6.3.3-6.3.4/add_approve_item_privilege.sql
@@ ../oracle-se/upgrade/6.3.3-6.3.4/add_url_generator_table.sql
@@ ../oracle-se/upgrade/6.3.3-6.3.4/add_task_type_privileges.sql

commit;
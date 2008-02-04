--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: add_approve_item_privilege.sql,v 1.1 2006/06/08 14:28:12 awux7820 Exp $
-- $DateTime: 2004/04/07 16:07:11 $

insert into acs_privileges (privilege)
values ('cms_approve_item');



insert into cms_privileges (privilege, pretty_name, sort_order, scope)
 values ('cms_approve_item', 'Approve Items', 69, 'item');
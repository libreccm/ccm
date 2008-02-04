--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: insert-privileges.sql 1290 2006-08-18 10:10:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_staff_admin', 'Administer Roles', 10, 'section'
);

insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_workflow_admin', 'Administer Workflow', 20, 'section'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_lifecycle_admin', 'Administer Lifecycles', 25, 'section'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_category_admin', 'Administer Categories', 30, 'section'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_content_type_admin', 'Administer Content Types', 40, 'section'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_categorize_items', 'Categorize Items', 45, 'section'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_new_item', 'Create New Items', 50, 'folder'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_edit_item', 'Edit Items', 60, 'item'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_item_admin', 'Item Administration', 65, 'item'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_apply_alternate_workflows', 'Apply Alternate Workflows', 68, 'item'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_publish', 'Publish Items', 70, 'item'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_delete_item', 'Delete Items', 80, 'folder'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_read_item', 'View Published Items', 90, 'folder'
);
insert into cms_privileges (
  privilege, pretty_name, sort_order, scope, viewer_appropriate
) values (
  'cms_preview_item', 'Preview Items', 100, 'folder', '1'
);

insert into acs_privileges (privilege) values ('cms_approve_item');

insert into cms_privileges (
  privilege, pretty_name, sort_order, scope
) values (
  'cms_approve_item', 'Approve Items', 69, 'item'
);



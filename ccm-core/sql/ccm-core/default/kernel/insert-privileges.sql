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
-- $Id: insert-privileges.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


insert into acs_privileges (privilege) values ('read');
insert into acs_privileges (privilege) values ('create');
insert into acs_privileges (privilege) values ('write');
insert into acs_privileges (privilege) values ('delete');
insert into acs_privileges (privilege) values ('admin');
insert into acs_privileges (privilege) values ('edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('create', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('delete', 'admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('edit', 'admin');



-- CMS Privileges 
insert into acs_privileges (privilege) values ('cms_staff_admin');
insert into acs_privileges (privilege) values ('cms_category_admin');
insert into acs_privileges (privilege) values ('cms_publish');
insert into acs_privileges (privilege) values ('cms_new_item');
insert into acs_privileges (privilege) values ('cms_edit_item');
insert into acs_privileges (privilege) values ('cms_delete_item');
insert into acs_privileges (privilege) values ('cms_read_item');
insert into acs_privileges (privilege) values ('cms_preview_item');
insert into acs_privileges (privilege) values ('cms_categorize_items');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_staff_admin','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_category_admin','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_publish','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_new_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item','admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items','admin');


-- This previously was implied by 
--    c.a.kernel.pemissions.PermissionManager.s_implications

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'edit');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_preview_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_delete_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_read_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_delete_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_preview_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_edit_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_edit_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_publish');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_new_item');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_staff_admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_categorize_items', 'cms_category_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_edit_item');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_new_item');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_delete_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_publish', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_new_item', 'cms_staff_admin');

insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('cms_category_admin', 'cms_staff_admin');

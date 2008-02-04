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
-- $Id: insert-task-types.sql 1288 2006-08-18 09:39:56Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

insert into cms_task_types (task_type_id, name, classname, privilege)
  values (1, 'Author', 'com.arsdigita.cms.workflow.AuthoringTaskURLGenerator', 'cms_edit_item');
insert into cms_task_types (task_type_id, name, classname, privilege)
  values (2, 'Edit', 'com.arsdigita.cms.workflow.EditingTaskURLGenerator', 'cms_approve_item');
insert into cms_task_types (task_type_id, name, classname, privilege)
  values (3, 'Deploy', 'com.arsdigita.cms.workflow.DeployTaskURLGenerator', 'cms_publish');

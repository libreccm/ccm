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
-- $Id: table-cw_tasks.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table cw_tasks (
  task_id       	 integer
                   	 constraint task_pk primary key,
  parent_task_id         integer
                         constraint task_parent_task_id 
			 references cw_tasks(task_id)
                         on delete cascade,
  label            	 varchar(200)
                   	 constraint task_label_nn not null,
  description      	 varchar(4000),
  is_active        	 char(1) default '0'
                   	 constraint task_is_active_ck
                   	 check (is_active in ('0', '1')),
  task_state             varchar(16)
                         constraint task_state_ck
                         check (task_state in 
                                 ('disabled', 'enabled', 'finished','deleted'))
);

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
-- $Id: table-cw_task_comments.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table cw_task_comments (
  comment_id		  integer
			  constraint task_comments_comment_id_nn not null,
  task_id                 integer
			  constraint task_comments_task_id_nn not null
			  constraint task_comments_task_id_fk 
			  references cw_tasks(task_id),
  task_comment		  varchar(4000),
  comment_date		  date default sysdate,
  party_id		  integer,     
  --
  -- allow same comment on many tasks, many comments on same task
  constraint task_comments_pk
  primary key (comment_id, task_id)
);

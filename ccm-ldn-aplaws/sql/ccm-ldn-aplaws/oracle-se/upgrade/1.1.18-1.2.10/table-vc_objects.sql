--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

create table vc_objects (
  object_id       integer
    constraint vc_objects_obj_fk references acs_objects
    on delete cascade,
  is_deleted      char(1) default '0' not null
                  check (is_deleted in ('1', '0')),
  master_id       integer
    constraint vc_objects_mst_fk references acs_objects
    on delete set null,
  constraint vc_objects_pk
    primary key(object_id)
);

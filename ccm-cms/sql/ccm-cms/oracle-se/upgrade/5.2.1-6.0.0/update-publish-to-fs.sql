--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: update-publish-to-fs.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


PROMPT Update Publish-To-Filesystem tables

-- publish_to_fs_files table
--------------------------------------------------------------------------------

-- fix constraint
alter table publish_to_fs_files drop constraint publish_to_fs_files_un;
alter table publish_to_fs_files add
    constraint publish_to_fs_files_un
      unique(file_name);

-- rename server_id to host_id
alter table publish_to_fs_files add (
    host_id INTEGER
);
update publish_to_fs_files
   set host_id = server_id;
commit;
alter table publish_to_fs_files modify (
    host_id not null
);
alter table publish_to_fs_files add
    constraint publi_to_fs_fil_hos_id_f_nwho1 foreign key (host_id)
      references web_hosts(host_id);
alter table publish_to_fs_files drop column server_id;

-- add item_type column
alter table publish_to_fs_files add (
    item_type VARCHAR(100)
);
update publish_to_fs_files
   set item_type = (select object_type
                      from acs_objects
                     where object_id = id);
commit;
alter table publish_to_fs_files modify (
    item_type not null
);

-- drop unused columns
alter table publish_to_fs_files drop column deleted;
alter table publish_to_fs_files drop column parent_id;

-- publish_to_fs_queue table
--------------------------------------------------------------------------------
-- rename host_id to server_id
alter table publish_to_fs_queue add (
    host_id INTEGER
);
update publish_to_fs_queue
   set host_id = server_id;
alter table publish_to_fs_queue modify (
    host_id not null
);
commit;
alter table publish_to_fs_queue add
    constraint publi_to_fs_que_hos_id_f_9wfcg foreign key (host_id)
      references web_hosts(host_id);

-- drop unused columns
alter table publish_to_fs_queue drop (
    base_type,
    extra_info,
    server_id,
    source,
    task_mode
);

-- publish_to_fs_servers table
--------------------------------------------------------------------------------
drop table publish_to_fs_servers;

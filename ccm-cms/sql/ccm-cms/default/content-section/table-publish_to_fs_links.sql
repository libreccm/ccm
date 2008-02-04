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
-- $Id: table-publish_to_fs_links.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- Domain object type is "PublishedLink".
create table publish_to_fs_links (
    id				integer
                                constraint publish_to_fs_links_pk primary key,
    source                      integer
				constraint publish_to_fs_links_source_fk
                                references publish_to_fs_files
				on delete cascade,
    target                      integer
				constraint publish_to_fs_links_target_fk
                                references publish_to_fs_files
				on delete cascade,
    is_child                    char(1)
				constraint publish_to_fs_files_child_ck
				check (is_child in ('0', '1')),
    constraint publish_to_fs_links_un 
               unique(source, target)
);

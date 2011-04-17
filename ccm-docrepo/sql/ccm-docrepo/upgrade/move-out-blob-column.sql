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
-- $Id: //apps/docmgr/dev/sql/upgrade/move-out-blob-column.sql#4 $
-- $DateTime: 2004/08/17 23:26:27 $


------------------------------------------
-- 1. Create new docs_blobjects table
------------------------------------------
create table docs_blobjects (
    content_id INTEGER not null
        constraint docs_blobjec_conten_id_p_7c9oS
          primary key,
        -- referential constraint for content_id deferred due to circular dependencies
    resource_id INTEGER not null,
        -- referential constraint for resource_id deferred due to circular dependencies
    content BLOB
);

------------------------------------------
-- 2.  Add constraints
-----------------------------------------
alter table docs_blobjects add
    constraint docs_blobjec_resour_id_f__xiNa foreign key (resource_id)
      references docs_resources(resource_id);

------------------------------------------
-- 3. Insert data from docs_resources into
--    docs_blobjects
------------------------------------------ 
insert into docs_blobjects (content_id, content,resource_id)
  select acs_object_id_seq.nextval, content, resource_id from docs_resources;

------------------------------------------
-- 4. Drop content column from docs_resources
------------------------------------------
alter table docs_resources
drop (content);

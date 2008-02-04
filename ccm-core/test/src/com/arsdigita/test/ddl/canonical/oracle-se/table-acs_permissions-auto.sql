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
-- $Id: table-acs_permissions-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table acs_permissions (
    privilege VARCHAR(100) not null,
        -- referential constraint for privilege deferred due to circular dependencies
    object_id INTEGER not null,
        -- referential constraint for object_id deferred due to circular dependencies
    grantee_id INTEGER not null,
        -- referential constraint for grantee_id deferred due to circular dependencies
    creation_date DATE not null,
    creation_ip VARCHAR(50),
    creation_user INTEGER,
        -- referential constraint for creation_user deferred due to circular dependencies
    constraint acs_per_gra_id_obj_id__p_lrweb
      primary key(grantee_id, object_id, privilege)
);

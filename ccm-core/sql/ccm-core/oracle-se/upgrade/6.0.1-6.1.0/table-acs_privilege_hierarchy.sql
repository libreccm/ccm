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
-- $Id: table-acs_privilege_hierarchy.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table acs_privilege_hierarchy (
    privilege          varchar(100)  not null
                       constraint acs_privilege_hier_priv_fk
                       references acs_privileges on delete cascade,
    child_privilege    varchar(100) not null
                       constraint acs_privilege_hier_chld_prv_fk
                       references acs_privileges on delete cascade,
                       constraint acs_privilege_hier_pk
                       primary key (privilege, child_privilege)
);

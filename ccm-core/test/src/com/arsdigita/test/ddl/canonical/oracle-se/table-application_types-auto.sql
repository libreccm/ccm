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
-- $Id: table-application_types-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table application_types (
    application_type_id INTEGER not null
        constraint appli_typ_appli_typ_id_p_r5e8o
          primary key,
    description VARCHAR(4000),
    has_embedded_view_p CHAR(1),
    has_full_page_view_p CHAR(1),
    object_type VARCHAR(100) not null
        constraint applicat_typ_obje_type_u_pf2uk
          unique,
    package_type_id INTEGER,
        -- referential constraint for package_type_id deferred due to circular dependencies
    profile VARCHAR(20),
    provider_id INTEGER,
        -- referential constraint for provider_id deferred due to circular dependencies
    singleton_p CHAR(1),
    title VARCHAR(200),
    workspace_application_p CHAR(1)
);

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
-- $Id: table-apm_packages-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table apm_packages (
    package_id INTEGER
        constraint apm_package_package_id_p_vrfsh
          primary key,
        -- referential constraint for package_id deferred due to circular dependencies
    locale_id INTEGER,
        -- referential constraint for locale_id deferred due to circular dependencies
    package_type_id INTEGER,
        -- referential constraint for package_type_id deferred due to circular dependencies
    pretty_name VARCHAR(300)
);

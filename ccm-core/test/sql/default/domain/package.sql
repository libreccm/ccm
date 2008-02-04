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
-- $Id: package.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file contains the data model for the package dependency
-- test cases.
--
-- @author Jon Orris
-- @version $Revision: #8 $ $Date: 2004/08/16 $
--

create table t_package (
    package_id    integer not null constraint package_pk primary key,
    name       varchar(100) not null constraint package_name_un unique
);

create table t_class (
    class_id    integer not null constraint class_pk primary key,
    package_id  integer not null constraint package_id_fk 
                                 references t_package(package_id),
    name       varchar(100) not null,
    is_abstract integer
);

-- Describes which packages a given package depends on.
-- Also known as Efferent packages.
create table t_package_depends_on (
    package_id                    integer not null  
                                  constraint t_pack_depend_pack_id_fk
                                  references t_package(package_id),
    depends_on_package_id         integer not null 
                                  constraint t_pack_depend_de_pack_id_fk
                                  references t_package(package_id)
);


-- Describes which packages use a given package.
-- Also known as Afferent packages.
create table t_package_used_by (
    package_id                 integer not null  
                               constraint t_pack_used_by_pack_id_fk
                               references t_package(package_id),
    used_by_package_id         integer not null 
                               constraint t_pack_used_by_used_pack_id_fk
                               references t_package(package_id)
);

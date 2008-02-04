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
-- $Id: table-cat_object_root_category_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table cat_object_root_category_map (
       root_category_id           integer
                                 constraint cat_obj_root_map_fk
                                 references cat_categories on delete cascade,
       package_id                integer
                                 constraint cat_obj_package_id_fk
                                 references apm_packages 
                                 on delete cascade,
       -- most of the time the object_id is actually going to
       -- be a user_id and used for personalizing categories
       -- hierarchies within a package
       object_id                 integer
                                 constraint cat_obj_object_id_fk
                                 references acs_objects on delete cascade,
       -- this is used to allow for package type mappings
       -- e.g. if every bboard wants to have the same category
       --  then leave package_id and object_id null and set
       --  object_type to 'bboard'
       object_type               varchar(100)
);

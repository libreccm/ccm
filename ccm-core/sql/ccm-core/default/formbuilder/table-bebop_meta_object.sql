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
-- $Id: table-bebop_meta_object.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table bebop_meta_object (
    object_id integer
        constraint bebop_meta_obj_object_id_fk references
        acs_objects
        constraint bebop_meta_obj_object_id_pk primary key,
    type_id integer
        constraint bebop_meta_object_type_id_nn not null
        constraint bebop_meta_object_type_id_fk references
        bebop_object_type on delete cascade,
    pretty_name varchar(50),
    pretty_plural varchar(50),
    class_name varchar(200),
    props_form varchar(200),
    constraint bebop_meta_obj_un unique (type_id, class_name)
);

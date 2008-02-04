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
-- $Id: table-persistence_dynamic_assoc.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table persistence_dynamic_assoc (
    pdl_id                 integer
                           constraint pers_dyn_assoc_pdl_id_fk
                           references acs_objects
                           constraint pers_dyn_assoc_pdl_id_pk
                           primary key,
    pdl_file               text
                           constraint pers_dyn_assoc_pdl_file_nn
                           not null,
    model_name             varchar(200)
                           constraint pers_dyn_assoc_model_nn
                           not null,
    object_type_one        varchar(500)
                           constraint pers_dyn_assoc_object1_nn
                           not null,
    property_one           varchar(100)
                           constraint pers_dyn_assoc_prop1_nn
                           not null,
    object_type_two        varchar(500)
                           constraint pers_dyn_assoc_object2_nn
                           not null,
    property_two           varchar(100)
                           constraint pers_dyn_assoc_prop2_nn
                           not null,
    constraint pers_dyn_assoc_un
    unique (model_name, object_type_one, property_one, object_type_two,
            property_two)
);

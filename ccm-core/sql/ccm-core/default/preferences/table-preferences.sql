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
-- $Id: table-preferences.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table preferences  (
    preference_id               integer
                                constraint preferences_pk primary key,
    parent_id                   integer
                                constraint preferences_parent_fk
                                references preferences (preference_id),
    name                        varchar(80)
                                constraint preferences_name_nn
                                not null,
    description                 varchar(4000),
    is_node                     integer
                                default 0
                                constraint preferences_is_node_ck
                                check (is_node in (0, 1)),
    preference_type             varchar(16)
                                constraint preferences_type_ck
                                check (preference_type in ('user', 'system')),
    value_type                  varchar(20)
                                constraint preference_values_type_ck
                                check (value_type in (
                                                      'int',
                                                      'long',
                                                      'float',
                                                      'double',
                                                      'boolean',
                                                      'string',
                                                      'bytearray'
                                                     )
                                      ),
    value_string                varchar(4000)
);

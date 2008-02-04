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
-- $Id: setup.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file contains the data model for the versioning tests
--
-- @author <a href="mailto:jbank@mit.edu">jbank@arsdigita.com</a>
-- @version $Revision: #6 $ $Date: 2004/08/16 $
--

create table t_versioned_datatypes (
    id               integer
                     constraint t_versioned_fk
                     references acs_objects
                     constraint t_versioned_row_id_pk primary key,
    j_big_integer    integer,
    j_big_decimal    decimal,
    j_boolean        char(1),
    j_byte           integer,
    j_character      char(1),
    j_date           date,
    j_double         numeric,
    j_float          float,
    j_integer        integer,
    j_long           numeric,
    j_short          integer,
    j_string         varchar(4000),
    j_blob           bytea,
    j_clob           text,
    -- forward relation
    related_id      integer references t_versioned_datatypes
                    on delete set null,
    -- backward relation
    parent_id       integer references t_versioned_datatypes
                    on delete cascade,
    -- composite child 
    child_id        integer references t_versioned_datatypes
                    on delete cascade
);

create table t_versioned_map (
    id integer references t_versioned_datatypes on delete cascade, 
    child_id integer references t_versioned_datatypes on delete cascade
);

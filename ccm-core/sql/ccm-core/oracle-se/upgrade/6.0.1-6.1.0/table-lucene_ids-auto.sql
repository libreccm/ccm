--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: table-lucene_ids-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table lucene_ids (
    id INTEGER not null
        constraint lucene_ids_id_p_amiqo
          primary key,
    host_id INTEGER not null
        constraint lucene_ids_host_id_u_q0oag
          unique,
        -- referential constraint for host_id deferred due to circular dependencies
    index_id INTEGER not null
        constraint lucene_ids_index_id_u_j8uuw
          unique
);

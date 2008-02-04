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
-- $Id: table-dnm_object_grants.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table dnm_object_grants (
       pd_object_id            integer not null
       constraint dnm_object_grants_pk primary key,
       pd_n_grants             integer not null
       constraint dnm_object_grants_positive_ck  check (pd_n_grants >= 1),
       constraint dnm_object_grants_obj_fk foreign key (pd_object_id)
         references dnm_object_1_granted_context (pd_object_id)
);

-- TODO: create separate implementation for oracle with  organization index and normal for postgres;

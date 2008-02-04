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
-- $Id: table-dnm_granted_context.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table dnm_granted_context (
   pd_object_id integer not null
   constraint dnm_gc_obj1_fk references dnm_object_grants,
   pd_context_id integer not null,
   pd_dummy_flag integer default 0 not null,
   constraint dnm_object_grants_dummy_ck
     check ( (pd_object_id = pd_context_id and pd_dummy_flag = 1)
             or (pd_object_id != pd_context_id and pd_dummy_flag = 0) ),
   constraint dnm_gc primary key (pd_context_id, pd_object_id)
) ;
-- TODO: does it make sence to create separate implementation for oracle with  
--  organization index and normal for postgres?

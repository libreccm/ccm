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
-- $Id: add-permissions-rebuild.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

  create or replace function permissions_rebuild()
  returns integer
  as
  '
   declare
    row    record;
   begin
    delete from object_context_map;
    delete from granted_context_non_leaf_map;
    delete from ungranted_context_non_leaf_map;
    delete from object_grants;

    for row in select * from object_context
                where context_id is not null 
    loop
      perform permissions_add_context(row.object_id, row.context_id);
    end loop;

    for row in select * from acs_permissions 
    loop
      perform permissions_add_grant(row.object_id);
    end loop;
    return 1;
  end;' language 'plpgsql';

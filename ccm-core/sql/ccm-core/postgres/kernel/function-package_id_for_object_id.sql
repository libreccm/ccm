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
-- $Id: function-package_id_for_object_id.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace function package_id_for_object_id (integer)
  returns integer as '
  declare
    v_object_id alias for $1;
    v_package_id integer;
    v_container_id integer;
    v_count integer;
  begin

    select package_id into v_package_id 
    from apm_packages
    where package_id = v_object_id;

    if (FOUND) then
       return v_package_id;
    end if;

    select container_id into v_container_id
    from object_container_map
    where object_id = v_object_id;

    if (NOT FOUND) then
        return null;
    end if;

    select package_id_for_object_id(v_container_id) 
    into v_container_id from dual;

    return v_container_id;
end;' language 'plpgsql';

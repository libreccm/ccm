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

create or replace function package_id_for_object_id(v_object_id INTEGER)
return INTEGER
as
    v_package_id apm_packages.package_id%TYPE;
    cursor containers is (select package_id from apm_packages
        where package_id in (select container_id 
        from object_container_map
        start with object_id = v_object_id
        connect by prior container_id = object_id 
        union select v_object_id from dual));
begin
    open containers;
    fetch containers into v_package_id;
    if (containers%NOTFOUND) then
       return null;
    else 
       return v_package_id;
    end if;
end;
/ 
show errors;

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
-- $Id: insert-acs_privilege_hierarchy.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('create', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('delete', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('edit', 'admin');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('read', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('write', 'edit');
insert into acs_privilege_hierarchy (child_privilege, privilege)
  values ('map_to_category', 'admin');

insert into acs_privilege_hierarchy (privilege, child_privilege)
  select 'admin', privilege 
    from acs_privileges 
    where privilege not in (select 'admin' from dual union all select child_privilege 
                              from acs_privilege_hierarchy 
                              where privilege = 'admin');

--
-- Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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
-- $Id: update-privilege-hierarchy.sql 1016 2005-11-30 11:10:55Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

-- set read as child of cms_read_item (so that a permission check
-- on read will return objects with cms_read_item granted.
-- all existing dnm... records are updated by trigger fired 
-- by this insert
insert into acs_privilege_hierarchy (privilege, child_privilege)
values ('cms_read_item', 'read');

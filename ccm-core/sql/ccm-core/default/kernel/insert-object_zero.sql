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
-- $Id: insert-object_zero.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


-- developers should never access this object directly.
-- The only way to access this object is by checking/granting/revoking
-- UniversalPermissonDescriptors instead of regular PermissionDescriptors.
-- In the future, it is likely that this object will go away or not be
-- an ACSObject.
insert into acs_objects 
(object_id, object_type, display_name, 
 default_domain_class)
values 
(0, 'com.arsdigita.kernel.ACSObject', 'Universal Permission Context', 
 'com.arsdigita.kernel.ACSObject');

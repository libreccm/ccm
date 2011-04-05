--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $Id: upd_system_tables.sql pboy $

-- adjust various system tables to the new name of application


update application_types
   set       title='Portal Workspace',
         package_type_id=null
 where   object_type='com.arsdigita.portalworkspace.Workspace' ;

update applications
   set   package_id=null
 where   primary_url = '/portal/' ;

-- table site_nodes
delete from site_nodes
 where name like '%portal%' ;

-- table apm_packages
delete from apm_packages
 where   pretty_name like '%Portal%' ;

-- table apm_package_types doesn't require an update
delete from apm_package_types
 where   pretty_name like '%Workspace%' ;



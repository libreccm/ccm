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
   set   title='Theme Director',
         package_type_id=null
 where   object_type='com.arsdigita.themedirector.ThemeDirector' ;

update applications
   set   package_id=null
 where   primary_url = '/admin/themes/' ;

-- table site_nodes
delete from site_nodes
 where name like '%theme%' ;

-- table apm_packages
delete from apm_packages
 where   pretty_name like '%Theme%' ;

-- table apm_package_types
delete from apm_package_types
 where   pretty_name like '%Theme%' ;

delete from object_context
    where object_id = (select acs_objects.object_id from acs_objects
                          where acs_objects.object_type
                              like '%com.arsdigita.kernel%'
                          AND acs_objects.display_name like '/admin/themes/') ;

delete from object_context
    where object_id = (select acs_objects.object_id from acs_objects
                          where acs_objects.object_type
                              like '%com.arsdigita.kernel%'
                          AND acs_objects.display_name like 'CCM Themes Admin') ;

delete  from acs_objects
    where object_type like '%com.arsdigita.kernel%'
    AND display_name like '%hemes%' ;


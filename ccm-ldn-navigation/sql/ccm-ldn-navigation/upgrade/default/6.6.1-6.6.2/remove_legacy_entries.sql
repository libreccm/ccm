--
-- Copyright (C) 2011 Peter Boy. All Rights Reserved.
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
-- $Id: remove_legacy_entries.sql  $

-- Navigation is now initialized as a legacy free type of application so
-- entries in tables apm_package_types are no longer needed.


update applications
    set package_id = null
    where title like 'Navigation Control Center' ;

delete from site_nodes
    where url like '/navigation/' ;

delete from apm_packages
    where pretty_name like 'Navigation' ;


update application_types
    set package_type_id = null
    where object_type like 'com.arsdigita.london.navigation.Navigation' ;

delete from apm_package_types
    where package_key like 'navigation' ;

delete from object_context
    where object_id = (select acs_objects.object_id from acs_objects
                          where acs_objects.object_type
                              like '%com.arsdigita.kernel%'
                          AND acs_objects.display_name like '/navigation/') ;

delete from object_context
    where object_id = (select acs_objects.object_id from acs_objects
                          where acs_objects.object_type
                              like '%com.arsdigita.kernel%'
                          AND acs_objects.display_name like 'Navigation') ;

delete  from acs_objects
    where object_type like '%com.arsdigita.kernel%'
    AND display_name like '%avigatio%' ;

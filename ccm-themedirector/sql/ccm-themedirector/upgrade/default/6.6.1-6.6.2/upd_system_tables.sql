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
         package_type_id = null
 where   object_type = 'com.arsdigita.themedirector.ThemeDirector' ;

 update applications
   set   package_id = null
 where   primary_url = '/admin/themes/' ;

-- table site_nodes
alter table site_nodes drop constraint site_nodes_parent_id_f_sacav;

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

delete from object_context 
 where object_id in (select object_id 
                       from acs_objects 
                      where object_type like '%com.arsdigita.kernel%'
                        and display_name like '%hemes%');

delete from object_context 
 where context_id in (select object_id 
                        from acs_objects 
                       where object_type like '%com.arsdigita.kernel%'
                         and display_name like '%hemes%');

alter table site_nodes drop constraint site_nodes_node_id_f_n1m2y;

alter table site_nodes drop constraint site_nodes_object_id_f_ked74;

delete from apm_packages 
      where package_id in (select object_id 
                             from acs_objects 
                            where object_type like '%com.arsdigita.kernel%'
                              and display_name like '%hemes%');

delete  from acs_objects
    where object_type like '%com.arsdigita.kernel%'
    AND display_name like '%hemes%' ;

-- Do not recreate the constraint for site_nodes. During the upgrade from 1.0.4 to 2.x.y site_nodes
-- will be removed completly, therefore we don't need the constraints. ccm-themedirector-6.6.1-6.6.2
-- is the first upgrade in the process which deals with the site_nodes, therefore it removes the 
-- constraints.
-- alter table site_nodes add 
--     constraint site_nodes_parent_id_f_sacav foreign key (parent_id)
--      references site_nodes(node_id);

-- alter table site_nodes add 
--    constraint site_nodes_node_id_f_n1m2y foreign key (node_id)
--      references acs_objects(object_id);

-- alter table site_nodes add 
--     constraint site_nodes_object_id_f_ked74 foreign key (object_id)
--      references apm_packages(package_id);
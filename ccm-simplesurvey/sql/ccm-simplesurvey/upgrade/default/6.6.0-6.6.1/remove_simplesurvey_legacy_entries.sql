--
-- Copyright (C) 2012 Peter Boy. All Rights Reserved.
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
-- $Id: remove_simplesurvey_legacy_entries.sql  $

-- ccm-simplesurvey is now initialized as a legacy free type
-- of application so entries in tables apm_package_types are no longer needed.


-- in case there may be several application instances!

-- delete from object_context all entries referring to node_id in site_nodes
delete from object_context
    where object_id in 
        (select node_id from site_nodes object_id where object_id in
            ( select package_id from applications where application_type_id =
                (select application_type_id from application_types 
                    where object_type 
                        like '%simplesurvey.SimpleSurvey%')
            )
        );   

-- delete from acs_objects all entries referring to node_id in site_nodes
alter table site_nodes drop constraint site_nodes_node_id_f_n1m2y ;
delete from acs_objects
    where object_id in 
        (select node_id from site_nodes where object_id in
            ( select package_id from applications where application_type_id =
                (select application_type_id from application_types 
                    where object_type 
                        like '%simplesurvey.SimpleSurvey%')
            )
        );   

-- delete all entries in site_nodes referring to a simplesurvey instance
delete from site_nodes
    where object_id in 
        (select package_id from applications where application_type_id =
            (select application_type_id from application_types 
                where object_type 
                    like '%simplesurvey.SimpleSurvey%') 
        );   
alter table site_nodes  add  constraint site_nodes_node_id_f_n1m2y
                             FOREIGN KEY (node_id)
                             REFERENCES acs_objects (object_id);



-- delete from object_context all entries referring to package_id in apm_packages
delete from object_context
    where object_id in 
        (select package_id from apm_packages where package_type_id =
                (select package_type_id from application_types 
                    where object_type 
                        like '%simplesurvey.SimpleSurvey%')
        );   

-- delete from acs_objects all entries referring to package_id in apm_packages
alter table apm_packages drop constraint apm_package_package_id_f_46may ;
alter table applications drop constraint application_package_id_f_cdaho ;
delete  from acs_objects
    where object_id in
       (select package_id from apm_packages where package_type_id =
                (select package_type_id from application_types 
                    where object_type 
                        like '%simplesurvey.SimpleSurvey%')
        );   

-- delete all entries for simplesurvey instances in apm_packages 
-- identified by package_type_id in application_types
delete from apm_packages
    where package_type_id =
        (select package_type_id from application_types 
            where object_type 
                like '%simplesurvey.SimpleSurvey%') ;

-- there seem to be no intries for a apm_packages_types entry (row) in 
-- acs_objects or object_context!

-- delete all entries for subsite in apm_package_types identified by 
-- package_type_id in application_types
alter table application_types drop constraint applica_typ_pac_typ_id_f_v80ma ;
delete from apm_package_types
    where package_type_id =
        (select package_type_id from application_types 
            where object_type 
                like '%simplesurvey.SimpleSurvey%') ;


-- set package_id to null for all entries referring to a simplesurvey instance
-- (indicating a new legacy free application) 
update applications
    set package_id = null
    where application_type_id = 
        (select application_type_id from application_types 
            where object_type 
                like '%simplesurvey.SimpleSurvey%') ;

-- set package_id to null for all entries referring to a simplesurvey instance
-- (indicating a new legacy free application) 
update application_types
    set package_type_id = null
    where object_type like '%simplesurvey.SimpleSurvey%' ;

alter table application_types  add  constraint applica_typ_pac_typ_id_f_v80ma
                            FOREIGN KEY (package_type_id)
                            REFERENCES apm_package_types (package_type_id);
alter table applications  add  constraint application_package_id_f_cdaho
                            FOREIGN KEY (package_id)
                            REFERENCES apm_packages (package_id);
alter table apm_packages  add  constraint apm_package_package_id_f_46may
                            FOREIGN KEY (package_id)
                            REFERENCES acs_objects (object_id);

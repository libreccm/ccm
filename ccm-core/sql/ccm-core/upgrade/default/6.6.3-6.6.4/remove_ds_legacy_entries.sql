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
-- $Id: remove_ds_legacy_entries.sql  $


-- CoreLoader used to load a webdeveloper support as old style, kernel.package
-- based application into database.
-- The old style application is completely removed (and reinstalled als new
-- style, legacy free application by Java upgrade script). 



-- delete from object_context all entries referring to DS node_id in site_nodes
delete from object_context
    where object_id in 
        (select node_id from site_nodes where object_id in
            (select package_id from apm_packages where package_type_id in
                (select package_type_id from apm_package_types 
                    where package_key like 'webdev-support')
            )
        );   

-- delete from acs_objects all entries referring to DS node_id in site_nodes
-- Not needed anymore, see ccm-themedirector-6.6.1-6.6.2
-- alter table site_nodes drop constraint site_nodes_node_id_f_n1m2y ;
delete from acs_objects
    where object_id in 
        (select node_id from site_nodes where object_id in
            (select package_id from apm_packages where package_type_id in
                (select package_type_id from apm_package_types 
                    where package_key like 'webdev-support')
            )
        );   

-- delete all entries in site_nodes referring to a DS instance
delete from site_nodes
    where object_id in 
        (select package_id from apm_packages where package_type_id in
            (select package_type_id from apm_package_types 
                where package_key like 'webdev-support')
        );   
-- Not needed anymore, see ccm-themedirector-6.6.1-6.6.2
-- alter table site_nodes  add  constraint site_nodes_node_id_f_n1m2y
                             FOREIGN KEY (node_id)
                             REFERENCES acs_objects (object_id);



-- delete from object_context all entries referring to DS in apm_packages
delete from object_context
    where object_id in 
        ( select package_id from apm_packages where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'webdev-support')
        );   

-- delete from acs_objects all entries referring to DS in apm_packages
ALTER TABLE apm_packages DROP CONSTRAINT apm_package_package_id_f_46may;
delete from acs_objects
    where object_id in 
        ( select package_id from apm_packages where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'webdev-support')
        );   

-- delete all entries for DS instances in apm_packages 
delete from apm_packages
    where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'webdev-support'); 

ALTER TABLE apm_packages
  ADD CONSTRAINT apm_package_package_id_f_46may FOREIGN KEY (package_id)
      REFERENCES acs_objects (object_id);

-- there seem to be no intries for a apm_packages_types entry (row) in 
-- acs_objects or object_context!

-- delete all entries in apm_package_types identified by key bebop 
delete from apm_package_types
    where package_key like 'webdev-support' ;



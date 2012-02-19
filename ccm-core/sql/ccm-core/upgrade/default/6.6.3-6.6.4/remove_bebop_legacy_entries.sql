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
-- $Id: remove_bebop_legacy_entries.sql  $

-- CoreLoader used to load a bebop package_type into database, but without
-- mounting any instance or to associate a dispatcher class. Si it never had
-- had a function as application.
-- entries in tables apm_package_types and apm_packages are no longer needed.


-- in case there may be several application instances!

-- delete from object_context all entries referring to bebop in apm_package_types
delete from object_context
    where object_id in 
        ( select package_id from apm_packages where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'bebop')
        );   

-- delete from acs_objects all entries referring to bebop in apm_package_types
ALTER TABLE apm_packages DROP CONSTRAINT apm_package_package_id_f_46may;
delete from acs_objects
    where object_id in 
        ( select package_id from apm_packages where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'bebop')
        );   

-- delete all entries in site_nodes referring to a subsite instance
-- NO ENTRIES in site_notes for bebop

-- delete all entries for bebop instances in apm_packages 
delete from apm_packages
    where package_type_id =
                (select package_type_id from apm_package_types 
                    where package_key like 'bebop'); 

ALTER TABLE apm_packages
  ADD CONSTRAINT apm_package_package_id_f_46may FOREIGN KEY (package_id)
      REFERENCES acs_objects (object_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- there seem to be no intries for a apm_packages_types entry (row) in 
-- acs_objects or object_context!

-- delete all entries in apm_package_types identified by key bebop 
delete from apm_package_types
    where package_key like 'bebop' ;



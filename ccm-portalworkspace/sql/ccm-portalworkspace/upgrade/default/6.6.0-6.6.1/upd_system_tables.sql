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

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;

alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name=replace(class_name,'london.portal', 'portalworkspace')
 where class_name like '%london.portal%' ;

update init_requirements
   set init=replace(init,'london.portal', 'portalworkspace')
 where init  like  '%london.portal%' ;

update init_requirements
   set required_init=replace(required_init,'london.portal', 'portalworkspace')
 where required_init  like  '%london.portal%' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);

ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);


UPDATE application_types
   SET object_type = REPLACE(object_type,'london.portal', 'portalworkspace')
 WHERE object_type LIKE '%london.portal%'  ;

-- table applications doesn't require an update

UPDATE apm_package_types
   SET package_key = REPLACE(package_key,'workspace', 'portalworkspace')
 WHERE package_key LIKE 'workspace'  ;

-- table apm_packages doesn't require an update either
-- table site_nodes doesn't require an update either


-- update application type in acs_objects
UPDATE acs_objects
   SET object_type = REPLACE(object_type,'london.portal', 'portalworkspace'),
       default_domain_class = REPLACE(default_domain_class,'london.portal', 'portalworkspace')
 WHERE object_type LIKE '%london.portal%' ;

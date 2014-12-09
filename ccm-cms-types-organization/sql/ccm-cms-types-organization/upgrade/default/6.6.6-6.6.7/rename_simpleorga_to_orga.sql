--
-- Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
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

-- Renaming ccm-cms-types-simpleorganization to ccm-cms-types-organization to keep the naming
-- schema

-- Rename tables
ALTER TABLE ct_simpleorganizations RENAME TO ct_organizations;
ALTER TABLE ct_simpleorganization_bundles RENAME TO ct_organization_bundles;

-- Update inits

-- Drop constraints for init_requirements temporaly (otherwise we can't update 
-- the tables)
ALTER TABLE init_requirements DROP CONSTRAINT init_requirements_init_f_cmmdn ;
ALTER TABLE init_requirements DROP CONSTRAINT init_require_requ_init_f_i6rgg ;

-- Adjust the class name of the Initializer
UPDATE inits
   SET class_name='com.arsdigita.cms.contenttypes.OrganizationInitializer'
 WHERE class_name='com.arsdigita.cms.contenttypes.SimpleOrganizationInitializer';

-- Adjust the class name of the Initializer in init-requirements
UPDATE init_requirements
   SET init='com.arsdigita.cms.contenttypes.OrganizationInitializer'
 WHERE init='com.arsdigita.cms.contenttypes.SimpleOrganizationInitializer';

-- Restore the constraints for init_requirements
ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);

-- Update system tables

-- Adjust the class name in acs-objects for all DecisionTree instances
UPDATE acs_objects
   SET object_type = 'com.arsdigita.cms.contenttypes.Organization',
       default_domain_class = 'com.arsdigita.cms.contenttypes.Organization'
 WHERE default_domain_class = 'com.arsdigita.cms.contenttypes.SimpleOrganization';

UPDATE acs_objects
   SET object_type = 'com.arsdigita.cms.contenttypes.OrganizationBundle',
       default_domain_class = 'com.arsdigita.cms.contenttypes.OrganizationBundle'
 WHERE default_domain_class = 'com.arsdigita.cms.contenttypes.SimpleOrganizationBundle';

--Adjust content type organization in contenttype directory table
UPDATE content_types
   SET object_type = 'com.arsdigita.cms.contenttypes.Organization', 
       classname = 'com.arsdigita.cms.contenttypes.Organization'
 WHERE object_type = 'com.arsdigita.cms.contenttypes.SimpleOrganization' ;

--Adjust content type decisiontree in authoring_steps directory table
UPDATE authoring_steps 
   SET component = 'com.arsdigita.cms.contenttypes.ui.OrganizationPropertiesStep'
 WHERE component = 'com.arsdigita.cms.contenttypes.ui.SimpleOrganizationPropertiesStep';

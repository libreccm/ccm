--
-- Copyright (C) 2012 Peter Boy All Rights Reserved.
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

-- rename ccm-cms-types-contact to ccm-ldn-types-contact
-- adjust various system tables to the new name of content type

ALTER TABLE init_requirements DROP CONSTRAINT init_requirements_init_f_cmmdn ;
ALTER TABLE init_requirements DROP CONSTRAINT init_require_requ_init_f_i6rgg ;

UPDATE inits
   SET class_name = 'com.arsdigita.cms.contenttypes.ldn.ESDServiceInitializer'
 WHERE class_name = 'com.arsdigita.cms.contenttypes.ESDServiceInitializer' ;

UPDATE init_requirements
   SET init = 'com.arsdigita.london.contenttypes.ldn.ESDServiceInitializer'
 WHERE init = 'com.arsdigita.cms.contenttypes.ESDServiceInitializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);


UPDATE content_types
   SET object_type = 'com.arsdigita.cms.contenttypes.ldn.ESDService',
             label = 'ESD Service',
       description = 'An ESDService',
         classname = 'com.arsdigita.cms.contenttypes.ldn.ESDService'
 WHERE   classname = 'com.arsdigita.cms.contenttypes.ESDService' ;

UPDATE authoring_steps
   SET label_key = 'ESD Service Properties',
       label_bundle = 'com.arsdigita.cms.contenttypes.ldn.ESDServiceResources',
       description_key = 'Edit the basic ESDService properties',
       description_bundle = 'com.arsdigita.cms.contenttypes.ldn.ESDServiceResources',
       component = 'com.arsdigita.cms.contenttypes.ldn.ui.ESDServicePropertiesStep'
 WHERE component = 'com.arsdigita.cms.contenttypes.ui.ESDServicePropertiesStep' ;

UPDATE authoring_steps
   SET label_key = 'ESD Service Contact',
       label_bundle = 'com.arsdigita.cms.contenttypes.ldn.ESDServiceResources',
       description_key = 'Edit/Choose the associated Contact object',
       description_bundle = 'com.arsdigita.cms.contenttypes.ldn.ESDServiceResources',
       component = 'com.arsdigita.cms.contenttypes.ldn.ui.ESDServiceChooseContactStep'
 WHERE component = 'com.arsdigita.cms.contenttypes.ui.ESDServiceChooseContactStep' ;



UPDATE acs_objects
   SET (object_type,default_domain_class) = ('com.arsdigita.cms.contenttypes.ldn.ESDService' ,
                                             'com.arsdigita.cms.contenttypes.ldn.ESDService' )
 WHERE default_domain_class = 'com.arsdigita.cms.contenttypes.ESDService' ;

UPDATE lucene_docs
   SET type = 'com.arsdigita.cms.contenttypes.ldn.ESDService'
 WHERE type = 'com.arsdigita.cms.contenttypes.ESDService' ;

UPDATE vcx_generic_operations
   SET value = replace(value, 'cms.contenttypes.ESDService', 'london.contenttypes.ldn.ESDService')
   WHERE value LIKE '%cms.contenttypes.ESDService%';

UPDATE vcx_obj_changes
   SET obj_id = REPLACE(obj_id,'cms.contenttypes.ESDService', 'london.contenttypes.ldn.ESDService')
   WHERE obj_id LIKE '%cms.contenttypes.ESDService%';

UPDATE vcx_tags
   SET tagged_oid = REPLACE(tagged_oid,'cms.contenttypes.ESDService', 'london.contenttypes.ldn.ESDService')
 WHERE tagged_oid LIKE '%cms.contenttypes.ESDService%';


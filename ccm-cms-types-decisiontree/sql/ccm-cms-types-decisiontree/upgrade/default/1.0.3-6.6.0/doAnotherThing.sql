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

--            --------------------------------------------
--            !!!  REPLACE BY REQUIRED SQL COMMANDS  !!!
--            --------------------------------------------

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;
alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name='com.arsdigita.london.contenttypes.ESDServiceInitializer'
 where class_name='com.arsdigita.cms.contenttypes.ESDServiceInitializer' ;

update init_requirements
   set init='com.arsdigita.london.contenttypes.ESDServiceInitializer'
 where init='com.arsdigita.cms.contenttypes.ESDServiceInitializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


update content_types
   set object_type='com.arsdigita.london.contenttypes.ESDService',
             label='ESD Service',
       description='An ESDService',
         classname='com.arsdigita.london.contenttypes.ESDService'
 where   classname='com.arsdigita.cms.contenttypes.ESDService' ;

update authoring_steps
   set           label_key='ESD Service Properties',
              label_bundle='com.arsdigita.london.contenttypes.ESDServiceResources',
           description_key='Edit the basic ESDService properties',
        description_bundle='com.arsdigita.london.contenttypes.ESDServiceResources',
                 component='com.arsdigita.london.contenttypes.ui.ESDServicePropertiesStep'
 where           component='com.arsdigita.cms.contenttypes.ui.ESDServicePropertiesStep' ;

update authoring_steps
   set           label_key='ESD Service Contact',
              label_bundle='com.arsdigita.london.contenttypes.ESDServiceResources',
           description_key='Edit/Choose the associated Contact object',
        description_bundle='com.arsdigita.london.contenttypes.ESDServiceResources',
                 component='com.arsdigita.london.contenttypes.ui.ESDServiceChooseContactStep'
 where           component='com.arsdigita.cms.contenttypes.ui.ESDServiceChooseContactStep' ;



update acs_objects
    set (object_type,default_domain_class) =
            ('com.arsdigita.london.contenttypes.ESDService' ,
             'com.arsdigita.london.contenttypes.ESDService' )
    where default_domain_class like 'com.arsdigita.cms.contenttypes.ESDService' ;

update lucene_docs
   set type='com.arsdigita.london.contenttypes.ESDService'
 where type='com.arsdigita.cms.contenttypes.ESDService' ;

update vcx_generic_operations
    set value=replace(value,'cms.contenttypes.ESDService', 'london.contenttypes.ESDService')
    where value like '%cms.contenttypes.ESDService%';

update vcx_obj_changes
    set obj_id=replace(obj_id,'cms.contenttypes.ESDService', 'london.contenttypes.ESDService')
    where obj_id like '%cms.contenttypes.ESDService%';

update vcx_tags
    set tagged_oid=replace(tagged_oid,'cms.contenttypes.ESDService', 'london.contenttypes.ESDService')
    where tagged_oid like '%cms.contenttypes.ESDService%';


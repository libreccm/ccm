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

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;
alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name='com.arsdigita.london.contenttypes.ContactInitializer'
 where class_name='com.arsdigita.cms.contenttypes.ContactInitializer' ;

update init_requirements
   set init='com.arsdigita.london.contenttypes.ContactInitializer'
 where init='com.arsdigita.cms.contenttypes.ContactInitializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


update content_types
   set object_type='com.arsdigita.london.contenttypes.Contact',
             label='LDN Contact',
       description='A London Contact person in CMS',
         classname='com.arsdigita.london.contenttypes.Contact'
 where   classname='com.arsdigita.cms.contenttypes.Contact' ;

update authoring_steps
   set           label_key='Contact Properties',
              label_bundle='com.arsdigita.london.contenttypes.ContactResources',
           description_key='Edit the basic contact properties',
        description_bundle='com.arsdigita.london.contenttypes.ContactResources',
                 component='com.arsdigita.london.contenttypes.ui.ContactPropertiesStep'
 where           component='com.arsdigita.cms.contenttypes.ui.ContactPropertiesStep' ;

update authoring_steps
   set           label_key='Contact Address Properties',
              label_bundle='com.arsdigita.london.contenttypes.ContactResources',
           description_key='Edit the Address associated with contact',
        description_bundle='com.arsdigita.london.contenttypes.ContactResources',
                 component='com.arsdigita.london.contenttypes.ui.ContactAddressProperties'
 where           component='com.arsdigita.cms.contenttypes.ui.ContactAddressProperties' ;

update authoring_steps
   set           label_key='Contact Phones Properties',
              label_bundle='com.arsdigita.london.contenttypes.ContactResources',
           description_key='Edit the Phones associated with contact',
        description_bundle='com.arsdigita.london.contenttypes.ContactResources',
                 component='com.arsdigita.london.contenttypes.ui.ContactPhonesPanel'
 where           component='com.arsdigita.cms.contenttypes.ui.ContactPhonesPanel' ;


update acs_objects
    set (object_type,default_domain_class) =
            ('com.arsdigita.london.contenttypes.Contact' ,
             'com.arsdigita.london.contenttypes.Contact' )
    where default_domain_class like 'com.arsdigita.cms.contenttypes.Contact' ;

update lucene_docs
   set type='com.arsdigita.london.contenttypes.Contact'
 where type='com.arsdigita.cms.contenttypes.Contact' ;

update vcx_generic_operations
    set value=replace(value,'cms.contenttypes.Contact', 'london.contenttypes.Contact')
    where value like '%cms.contenttypes.Contact%';

update vcx_obj_changes
    set obj_id=replace(obj_id,'cms.contenttypes.Contact', 'london.contenttypes.Contact')
    where obj_id like '%cms.contenttypes.Contact%';

update vcx_tags
    set tagged_oid=replace(tagged_oid,'cms.contenttypes.Contact', 'london.contenttypes.Contact')
    where tagged_oid like '%cms.contenttypes.Contact%';


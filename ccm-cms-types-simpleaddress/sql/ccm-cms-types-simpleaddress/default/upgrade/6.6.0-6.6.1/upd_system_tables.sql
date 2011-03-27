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

-- adjust various system tables to the new name of content type

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;

update inits
   set class_name='com.arsdigita.cms.contenttypes.SimpleAddressInitializer'
 where class_name='com.arsdigita.cms.contenttypes.AddressInitializer' ;

update init_requirements
   set init='com.arsdigita.cms.contenttypes.SimpleAddressInitializer'
 where init='com.arsdigita.cms.contenttypes.AddressInitializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


update content_types
   set object_type='com.arsdigita.cms.contenttypes.SimpleAddress',
             label='Simple Address',
         classname='com.arsdigita.cms.contenttypes.SimpleAddress'
 where   classname='com.arsdigita.cms.contenttypes.Address' ;

update authoring_steps
   set           label_key='simpleaddress.authoring.basic_properties.title',
              label_bundle='com.arsdigita.cms.contenttypes.SimpleAddressResources',
           description_key='simpleaddress.authoring.basic_properties.description',
        description_bundle='com.arsdigita.cms.contenttypes.SimpleAddressResources',
                 component='com.arsdigita.cms.contenttypes.ui.SimpleAddressPropertiesStep'
 where           component='com.arsdigita.cms.contenttypes.ui.AddressPropertiesStep' ;


update acs_objects
    set (object_type,default_domain_class) =
            ('com.arsdigita.cms.contenttypes.SimpleAddress' ,
             'com.arsdigita.cms.contenttypes.SimpleAddress' )
    where default_domain_class like 'com.arsdigita.cms.contenttypes.Address' ;

update lucene_docs
   set type='com.arsdigita.cms.contenttypes.SimpleAddress'
 where type='com.arsdigita.cms.contenttypes.Address' ;

update vcx_generic_operations
    set value=replace(value,'contenttypes.Address', 'contenttypes.SimpleAddress')
    where value like '%contenttypes.Address%';

update vcx_obj_changes
    set obj_id=replace(obj_id,'contenttypes.Address', 'contenttypes.SimpleAddress')
    where obj_id like '%contenttypes.Address%';

update vcx_tags
    set tagged_oid=replace(tagged_oid,'contenttypes.Address', 'contenttypes.SimpleAddress')
    where tagged_oid like '%contenttypes.Address%';


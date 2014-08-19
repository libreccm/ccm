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

-- Rename table to ct_ldn_organization to avoid name collision

ALTER TABLE init_requirements DROP CONSTRAINT init_requirements_init_f_cmmdn ;

UPDATE inits
   SET class_name='com.arsdigita.cms.contenttypes.ldn.OrganizationInitializer'
 WHERE class_name='com.arsdigita.cms.contenttypes.OrganizationInitializer';

UPDATE init_requirements
   SET init='com.arsdigita.cms.contenttypes.ldn.OrganizationInitializer'
 WHERE init='com.arsdigita.cms.contenttypes.OrganizationInitializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

UPDATE content_types 
   SET object_type='com.arsdigita.cms.contenttypes.ldn.Organization',
         classname='com.arsdigita.cms.contenttypes.ldn.Organization'
 WHERE   classname='com.arsdigita.cms.contenttypes.Organization';

UPDATE authoring_steps
   SET    label_bundle='com.arsdigita.cms.contenttypes.ldn.OrganizationResources',
    description_bundle='com.arsdigita.cms.contenttypes.ldn.OrganizationResources',
             component='com.arsdigita.cms.contenttypes.ldn.ui.OrganizationPropertiesStep'
 WHERE component='com.arsdigita.cms.contenttypes.ui.OrganizationPropertiesStep';

UPDATE acs_objects 
   SET (object_type,default_domain_class) =
            ('com.arsdigita.cms.contenttypes.ldn.Organization',
             'com.arsdigita.cms.contenttypes.ldn.Organization')
 WHERE default_domain_class = 'com.arsdigita.cms.contenttypes.Organization';

UPDATE lucene_docs
   SET type='com.arsdigita.cms.contenttypes.ldn.Organization'
 WHERE type='com.arsdigita.cms.contenttypes.Organization';

UPDATE vcx_generic_operations
SET value=REPLACE(value,'contenttypes.Organization', 'contenttypes.ldn.Organization')
WHERE value LIKE '%contenttypes.Organization%';

UPDATE vcx_obj_changes
SET obj_id=REPLACE(obj_id,'contenttypes.Organization', 'contenttypes.ldn.Organization')
    WHERE obj_id LIKE '%contenttypes.Organization%';

UPDATE vcx_tags
    SET tagged_oid=REPLACE(tagged_oid,'contenttypes.Organization', 'contenttypes.ldn.Organization')
    WHERE tagged_oid LIKE '%contenttypes.Organization%';


ALTER TABLE ct_organization RENAME TO ct_ldn_organization;
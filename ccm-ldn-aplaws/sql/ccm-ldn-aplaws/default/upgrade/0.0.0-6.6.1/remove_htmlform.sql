--
-- Copyright (C) 2013 Jens Pelzetter All Rights Reserved.
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
-- $Id$

-- Converts all HTMLForm content items to Article content items.

-- Remove the inizalizer for HTMLForm for inits and inits_requirements
DELETE FROM init_requirements WHERE required_init = 'com.arsdigita.cms.contenttypes.HTMLFormInitializer'
                                 OR init = 'com.arsdigita.cms.contenttypes.HTMLFormInitializer';
DELETE FROM inits WHERE class_name = 'com.arsdigita.cms.contenttypes.HTMLFormInitializer';

-- HTMLForm did not have its own table in APLAWS 1.0.4
-- INSERT INTO ct_articles (item_id, lead) SELECT item_id, lead FROM ct_htmlform;
DELETE FROM authoring_kit_step_map 
      WHERE kit_id = (SELECT kit_id 
                        FROM authoring_kits 
                       WHERE type_id = (SELECT type_id 
                                          FROM content_types 
                                         WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm'));

DELETE FROM authoring_steps WHERE component = 'com.arsdigita.cms.contenttypes.ui.HTMLFormPropertiesStep';

DELETE FROM authoring_kits 
      WHERE type_id = (SELECT type_id 
                         FROM content_types 
                        WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm');

UPDATE cms_items 
   SET type_id = (SELECT type_id FROM content_types WHERE object_type = 'com.arsdigita.cms.contenttypes.Article')
 WHERE type_id = (SELECT type_id FROM content_types WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm');

UPDATE acs_objects 
   SET object_type = 'com.arsdigita.cms.contenttypes.Article', 
       default_domain_class = 'com.arsdigita.cms.contenttypes.Article'
 WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm';

UPDATE lucene_docs 
   SET type = 'com.arsdigita.cms.contenttypes.Article'
 WHERE type = 'com.arsdigita.cms.contenttypes.HTMLForm';

UPDATE vcx_generic_operations
   SET value = replace(value, 'com.arsdigita.cms.contenttypes.HTMLForm', 'com.arsdigita.cms.contenttypes.Article')
   WHERE value LIKE '%com.arsdigita.cms.contenttypes.HTMLForm%';

UPDATE vcx_obj_changes
   SET obj_id = REPLACE(obj_id, 'com.arsdigita.cms.contenttypes.HTMLForm', 'com.arsdigita.cms.contenttypes.Article')
   WHERE obj_id LIKE '%com.arsdigita.cms.contenttypes.HTMLForm%';

UPDATE vcx_tags
   SET tagged_oid = REPLACE(tagged_oid,'com.arsdigita.cms.contenttypes.HTMLForm', 'com.arsdigita.cms.contenttypes.Article')
 WHERE tagged_oid LIKE '%com.arsdigita.cms.contenttypes.HTMLForm%';

DELETE FROM content_section_type_map 
      WHERE type_id = (SELECT type_id 
                         FROM content_types 
                        WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm');
DELETE FROM content_types WHERE object_type = 'com.arsdigita.cms.contenttypes.HTMLForm';

DROP TABLE IF EXISTS ct_htmlform;
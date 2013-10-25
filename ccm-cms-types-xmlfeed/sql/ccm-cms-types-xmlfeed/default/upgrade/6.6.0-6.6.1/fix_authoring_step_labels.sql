--
-- Copyright (C) 2013 Jens Pelzetter. All Rights Reserved.
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
-- Fixes the labels for authoring steps (and the moment the key in the 
-- database is the english text, this upgrade replaces this with the
-- key and sets the bundle for the authoring steps.

UPDATE authoring_steps 
   SET label_key = 'cms.contenttypes.shared.basic_properties.title',
       label_bundle = 'com.arsdigita.cms.CMSResources',
       description_key = 'cms.contenttypes.shared.basic_properties.description',
       description_bundle = 'com.arsdigita.cms.CMSResources'
 WHERE step_id = (SELECT authoring_steps.step_id 
                    FROM authoring_steps 
                    JOIN authoring_kit_step_map ON authoring_steps.step_id = authoring_kit_step_map.step_id 
                    JOIN authoring_kits ON authoring_kit_step_map.kit_id = authoring_kits.kit_id 
                    JOIN content_types ON authoring_kits.type_id = content_types.type_id 
                   WHERE authoring_steps.component = 'com.arsdigita.cms.contenttypes.xmlfeed.ui.XMLFeedProperties' 
                     AND content_types.object_type = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed');

UPDATE authoring_steps 
   SET label_key = 'xmlfeed.authoring.styling_step.title',
       label_bundle = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeedResources',
       description_key = 'xmlfeed.authoring.styling_step.description',
       description_bundle = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeedResources'
 WHERE step_id = (SELECT authoring_steps.step_id 
                    FROM authoring_steps 
                    JOIN authoring_kit_step_map ON authoring_steps.step_id = authoring_kit_step_map.step_id 
                    JOIN authoring_kits ON authoring_kit_step_map.kit_id = authoring_kits.kit_id 
                    JOIN content_types ON authoring_kits.type_id = content_types.type_id 
                   WHERE authoring_steps.component = 'com.arsdigita.cms.contenttypes.xmlfeed.ui.XSLFileProperties' 
                     AND content_types.object_type = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed');

UPDATE authoring_steps 
   SET label_key = 'xmlfeed.authoring.query_form_step.title',
       label_bundle = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeedResources',
       description_key = 'xmlfeed.authoring.query_form.description',
       description_bundle = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeedResources'
 WHERE step_id = (SELECT authoring_steps.step_id 
                   FROM authoring_steps 
                   JOIN authoring_kit_step_map ON authoring_steps.step_id = authoring_kit_step_map.step_id 
                   JOIN authoring_kits ON authoring_kit_step_map.kit_id = authoring_kits.kit_id 
                   JOIN content_types ON authoring_kits.type_id = content_types.type_id 
                  WHERE authoring_steps.component = 'com.arsdigita.cms.ui.formbuilder.FormControls' 
                    AND content_types.object_type = 'com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed');

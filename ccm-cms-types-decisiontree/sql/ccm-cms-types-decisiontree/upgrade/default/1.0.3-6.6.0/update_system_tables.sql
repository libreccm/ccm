--
-- Copyright (C) 2013 Peter Boy. All Rights Reserved.
-- Copyright (C) 2013 Jens . All Rights Reserved.
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
-- $Id: upd_acs_objects.sql  $

-- Adjust the class name in acs-objects for all DecisionTree instances
UPDATE acs_objects
   SET object_type = 'com.arsdigita.cms.contenttypes.DecisionTree',
       default_domain_class = 'com.arsdigita.cms.contenttypes.DecisionTree'
 WHERE default_domain_class = 'com.arsdigita.camden.cms.contenttypes.DecisionTree' ;

--Adjust content type decisiontree in contenttype directory table
UPDATE content_types
   SET object_type = 'com.arsdigita.cms.contenttypes.DecisionTree', 
       classname = 'com.arsdigita.cms.contenttypes.DecisionTree'
 WHERE object_type = 'com.arsdigita.camden.cms.contenttypes.DecisionTree' ;

--Adjust content type decisiontree in authoring_steps directory table
UPDATE authoring_steps 
   SET component = 'com.arsdigita.cms.contenttypes.ui.DecisionTreePropertiesStep'
 WHERE component = 'com.arsdigita.camden.cms.contenttypes.ui.DecisionTreePropertiesStep';

UPDATE authoring_steps 
   SET component = 'com.arsdigita.cms.contenttypes.ui.DecisionTreeSectionStep'
 WHERE component = 'com.arsdigita.camden.cms.contenttypes.ui.DecisionTreeViewSections';

UPDATE authoring_steps 
   SET component = 'com.arsdigita.cms.contenttypes.ui.DecisionTreeOptionStep'
 WHERE component = 'com.arsdigita.camden.cms.contenttypes.ui.DecisionTreeViewOptions';

UPDATE authoring_steps 
   SET component = 'com.arsdigita.cms.contenttypes.ui.DecisionTreeTargetStep'
 WHERE component = 'com.arsdigita.camden.cms.contenttypes.ui.DecisionTreeViewTargets';

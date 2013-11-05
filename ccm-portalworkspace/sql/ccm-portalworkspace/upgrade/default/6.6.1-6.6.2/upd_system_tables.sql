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


UPDATE application_types
   SET title = 'Portal Workspace',
       package_type_id = null
 WHERE object_type = 'com.arsdigita.portalworkspace.Workspace';

UPDATE applications
   SET package_id=null
 WHERE application_type_id = (SELECT application_type_id 
                                FROM application_types 
                               WHERE object_type = 'com.arsdigita.portalworkspace.Workspace');
 --WHERE primary_url = '/portal/';

-- table site_nodes
DELETE FROM site_nodes
 WHERE name LIKE '%portal%';

-- table apm_packages
DELETE FROM apm_packages
      WHERE pretty_name LIKE '%Portal%';

-- table apm_package_types 
DELETE FROM apm_package_types
      where pretty_name LIKE '%Workspace%';

DELETE FROM object_context
      WHERE object_id = (SELECT acs_objects.object_id FROM acs_objects
                          WHERE acs_objects.object_type LIKE '%com.arsdigita.kernel%'
                            AND acs_objects.display_name LIKE '/portal/');

DELETE FROM object_context
      WHERE object_id IN (SELECT acs_objects.object_id FROM acs_objects
                           WHERE acs_objects.object_type LIKE '%com.arsdigita.kernel%'
                             AND acs_objects.display_name LIKE 'Portal Workspace');

DELETE FROM acs_objects
      WHERE object_type LIKE '%com.arsdigita.kernel%'
        AND display_name LIKE '/portal/';

DELETE FROM acs_objects
      WHERE object_type LIKE '%com.arsdigita.kernel.PackageInstance'
        AND display_name LIKE 'Portal Workspace';


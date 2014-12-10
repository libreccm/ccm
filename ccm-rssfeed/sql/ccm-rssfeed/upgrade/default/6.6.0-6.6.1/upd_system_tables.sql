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

-- adjust various system tables to the new name of application subsite

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;

alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name=replace(class_name,'london.rss', 'rssfeed')
 where class_name like '%london.rss%' ;

update init_requirements
   set init=replace(init,'london.rss', 'rssfeed')
 where init  like  '%london.rss%' ;

update init_requirements
   set required_init=replace(required_init,'london.rss', 'rssfeed')
 where required_init  like  '%london.rss%' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);

ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);


UPDATE application_types
   SET object_type = REPLACE(object_type,'london.rss.RSS', 'rssfeed.RSSFeed'),
       title = 'RSS Feed',
       description = 'Provides RSS feed service'
 WHERE object_type  LIKE '%london.rss.RSS%';

-- table applications requires an update
UPDATE applications
   SET title = 'RSS Feeds',
       description = 'RSS Feed channels'
 WHERE primary_url LIKE  '%channels%';

-- update acs_objects
-- (a) update application type
UPDATE acs_objects
   SET object_type = REPLACE(object_type,'london.rss.RSS', 'rssfeed.RSSFeed'),
       display_name = 'RSS Service',
       default_domain_class = REPLACE(default_domain_class,'london.rss.RSS', 'rssfeed.RSSFeed')
 WHERE object_type LIKE '%london.rss.RSS%' ;

-- (b) update feeds
UPDATE acs_objects
   SET object_type = REPLACE(object_type,'london.rss', 'rssfeed'),
       display_name = REPLACE(display_name,'london.rss','rssfeed'),
       default_domain_class = REPLACE(default_domain_class,'london.rss', 'rssfeed')
 WHERE object_type LIKE '%london.rss.Feed%' ;

-- (c) remove unused RSS cat purpose
UPDATE acs_objects
   SET display_name = 'RSS cat purpose to delete'
 WHERE object_id = (SELECT purpose_id FROM cat_purposes 
                       WHERE key LIKE '%RSS%'); 

DELETE FROM cat_purposes WHERE key LIKE '%RSS%'; 

DELETE FROM object_context WHERE object_id = (SELECT object_id 
                                              FROM acs_objects
                                              WHERE display_name LIKE 'RSS cat purpose to delete');

DELETE FROM acs_objects WHERE display_name LIKE 'RSS cat purpose to delete' ;

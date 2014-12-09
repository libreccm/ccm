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


update application_types
   set (object_type,title,description)=
           (replace(object_type,'london.rss.RSS', 'rssfeed.RSSFeed'),
            'RSS Feed',
            'Provides RSS feed service') 
   where   object_type  like  '%london.rss.RSS%'  ;

-- table applications requires an update
update applications
   set (title,description)=('RSS Feeds','RSS feed channels')
   where   primary_url  like  '%channels%'  ;

-- update acs_objects
-- (a) update application type
update acs_objects
    set (object_type,display_name,default_domain_class) =
            (replace(object_type,'london.rss.RSS', 'rssfeed.RSSFeed') ,
             'RSS Service',
             replace(default_domain_class,'london.rss.RSS', 'rssfeed.RSSFeed') )
    where object_type like '%london.rss.RSS%' ;
-- (b) update feeds
update acs_objects
    set (object_type,display_name,default_domain_class) =
            (replace(object_type,'london.rss', 'rssfeed') ,
             replace(display_name,'london.rss','rssfeed'),
             replace(default_domain_class,'london.rss', 'rssfeed') )
    where object_type like '%london.rss.Feed%' ;
-- (c) remove unused RSS cat purpose
update acs_objects
    set display_name = 'RSS cat purpose to delete'
    where object_id = (select purpose_id from cat_purposes 
                       where key like '%RSS%'); 
delete from cat_purposes where key like '%RSS%' ; 
delete from object_context where object_id = (select object_id from acs_objects
                                              where display_name like
                                              'RSS cat purpose to delete') ;
delete from acs_objects where display_name like 'RSS cat purpose to delete' ;

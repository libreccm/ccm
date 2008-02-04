--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: misc.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- Promote all plain articles to article + lead
update acs_objects 
   set default_domain_class = 'com.arsdigita.cms.contenttypes.Article',
       object_type          = 'com.arsdigita.cms.contenttypes.Article'
 where default_domain_class = 'com.arsdigita.cms.Article';

insert into ct_articles (item_id) 
     select article_id 
       from cms_articles 
      where article_id not in (select article_id from ct_articles);

update cms_items 
   set type_id = (select type_id 
                    from content_types 
                   where object_type = 'com.arsdigita.cms.contenttypes.Article')
 where type_id = (select type_id 
                    from content_types 
                   where object_type = 'com.arsdigita.cms.Article');

-- Now remove the content types
delete from content_types where object_type in (
  'com.arsdigita.cms.Article',
  'com.arsdigita.cms.contenttypes.ArticleSection'
);

alter table cms_pages add ( launch_date DATE);

insert into cat_root_cat_object_map (object_id, category_id)
  (select section_id, root_category_id from content_sections);

alter table content_sections drop column root_category_id;

alter table cat_category_purpose_map modify (category_id not null);
alter table cat_category_purpose_map modify (purpose_id not null);
alter table cms_mime_status modify (hash_code not null);
alter table cms_mime_status modify (inso_filter_works not null);
alter table phases modify (cycle_id null);

alter table cms_version_map modify (timestamp default null);

drop table ct_mp_articles_map;

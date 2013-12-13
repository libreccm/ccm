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
-- $Id: drop_table_cms_article_image_map.sql pboy $

-- move data from cms_article_image_map to cms_item_image_attachments

ALTER TABLE cms_article_image_map DROP CONSTRAINT cms_article_image_map_id_fk;

DELETE FROM cms_items
      WHERE item_id IN ( SELECT object_id FROM acs_objects WHERE object_type = 'com.arsdigita.cms.ArticleImageAssociation');

UPDATE acs_objects
   SET          object_type = 'com.arsdigita.cms.contentassets.ItemImageAttachment',
               display_name = 'com.arsdigita.cms.contentassets.ItemImageAttachment ' || object_id,
       default_domain_class = 'com.arsdigita.cms.contentassets.ItemImageAttachment'
 WHERE object_type = 'com.arsdigita.cms.ArticleImageAssociation';
 
INSERT INTO cms_item_image_attachment ( attachment_id, item_id, image_id, caption, title, description )
     SELECT map_id as attachment_id,
            article_id as item_id,
            image_id,
            substring(caption, 0, 100) as caption,
            substring(caption, 0, 200) as title,
            caption as description
       FROM cms_article_image_map;

DROP TABLE cms_article_image_map;

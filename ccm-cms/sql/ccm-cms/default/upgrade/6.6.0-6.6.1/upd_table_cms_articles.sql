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
-- $Id: upd_table_cms_articles .sql pboy $



alter table cms_text_pages drop constraint cms_text_pages_item_id_f_kfox7 ;
alter table cms_text_pages drop constraint cms_text_pages_text_id_f_uri55 ;
alter table cms_text_pages drop constraint cms_text_pages_item_id_p_7tnky CASCADE ;
drop index cms_text_pages_text_id_idx ;

alter table cms_text_pages rename to cms_articles ;
alter table cms_articles   rename column item_id to article_id ;

ALTER TABLE cms_articles  ADD CONSTRAINT cms_article_article_id_p_s67nq
                          PRIMARY KEY (article_id);
alter table cms_articles  add  constraint cms_article_article_id_f_ekqk1
                               FOREIGN KEY (article_id)
                               REFERENCES cms_pages (item_id);
alter table cms_articles  add  constraint cms_articles_text_id_f_8ah18
                               FOREIGN KEY (text_id)
                               REFERENCES cms_text (text_id);



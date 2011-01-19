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

-- alter table cms_articles add column text_id integer ;
-- update cms_articles set text_id = (select text_id from cms_text_pages
--                     where cms_text_pages.item_id = cms_articles.article_id);

-- alter table cms_articles  drop constraint cms_article_article_id_f_ekqk1 ;

-- alter table cms_articles  add  constraint cms_article_article_id_f_ekqk1
--                                FOREIGN KEY (article_id)
--                                REFERENCES cms_pages (item_id);
-- alter table cms_articles  add  constraint cms_articles_text_id_f_8ah18
--                                FOREIGN KEY (text_id)
--                                REFERENCES cms_text (text_id);

-- alter table ct_events drop constraint ct_events_item_id_f_v7kjv ;

-- drop table  cms_text_pages;

-- ALTER TABLE ct_events
--     ADD CONSTRAINT ct_events_item_id_f_v7kjv FOREIGN KEY (item_id)
--     REFERENCES cms_articles(article_id);
-- Error msg bei zes-testupd:
-- FEHLER:  Einfügen oder Aktualisieren in Tabelle »ct_events« verletzt Fremdschlüssel-Constraint »ct_events_item_id_f_v7kjv«
-- DETAIL:  Schlüssel (item_id)=(520355) ist nicht in Tabelle »cms_articles« vorhanden.

-- deletes constraints in all tables with references on cms_articles!
drop table cms_articles  CASCADE ;

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



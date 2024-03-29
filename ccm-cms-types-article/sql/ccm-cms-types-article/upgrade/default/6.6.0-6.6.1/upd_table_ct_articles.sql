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
-- $Id: upd_table_ct_articles.sql pboy $

-- This is NOT a regular update script. It re-creates a constraints
-- which once got lost during one update, but never again until now.
-- Use it only as needed
--
-- recreate constraint  on cms_articles which had to be deleted during
-- update of cms_articles
ALTER TABLE ct_articles
      ADD CONSTRAINT ct_articles_item_id_f_6ofn1 FOREIGN KEY (item_id)
      REFERENCES cms_articles(article_id);
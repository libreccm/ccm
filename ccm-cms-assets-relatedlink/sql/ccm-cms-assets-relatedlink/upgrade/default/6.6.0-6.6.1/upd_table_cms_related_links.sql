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
-- $Id: upd_table_cms_related_links.sql  $

-- Column is created by ccm-cms-6.6.0-6.6.1
-- ALTER TABLE cms_related_link ADD link_list_name VARCHAR(100)

UPDATE cms_related_links
      SET link_list_name = 'NONE'
      WHERE link_list_name IS NULL;

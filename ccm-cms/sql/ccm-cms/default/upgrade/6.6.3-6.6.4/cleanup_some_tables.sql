--
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
-- $Id: cleanup_tables.sql pboy $



-- db staatsschiff-114, wsf-114
-- ALTER TABLE ONLY cms_persons
--     DROP CONSTRAINT cms_persons_alias_id_fkey;
-- db iaw-114
ALTER TABLE ONLY cms_persons
    DROP CONSTRAINT cms_persons_aliasid_fkey;
ALTER TABLE cms_persons
  ADD CONSTRAINT cms_persons_alias_id_f_uaoxu FOREIGN KEY (alias_id)
      REFERENCES cms_persons (person_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


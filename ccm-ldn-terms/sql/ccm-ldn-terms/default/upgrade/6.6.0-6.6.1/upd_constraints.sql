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
-- $Id: upd_constraints.sql pboy $

-- recreate constraint on trm_domains_indexer which got lost anywhere during
-- the update processes
ALTER TABLE trm_domains_indexer
      ADD CONSTRAINT trm_doma_inde_index_id_f_ggaqm FOREIGN KEY (indexer_id)
      REFERENCES acs_objects(object_id);
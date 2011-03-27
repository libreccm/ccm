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
-- $Id: upd_table_ct_addresses.sql pboy $

alter table ct_addresses rename to ct_simpleaddr_addresses ;

ALTER TABLE ct_simpleaddr_addresses
      ADD CONSTRAINT ct_simple_addre_add_id_p_gf2ww PRIMARY KEY(address_id);

ALTER TABLE ct_simpleaddr_addresses
      ADD CONSTRAINT ct_sim_add_iso_cou_cod_f_7mojx FOREIGN KEY (iso_country_code)
          REFERENCES ct_simpleaddr_iso_countries (iso_code) MATCH SIMPLE
          ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ct_simpleaddr_addresses
      ADD CONSTRAINT ct_simple_addre_add_id_f_nrh2p FOREIGN KEY (address_id)
          REFERENCES cms_pages (item_id) MATCH SIMPLE
          ON UPDATE NO ACTION ON DELETE NO ACTION;
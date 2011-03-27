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
-- $Id: upd_table_iso_countries.sql pboy $

alter table iso_countries drop constraint iso_countries_iso_code_p_zzr8y ;

alter table iso_countries rename to ct_simpleaddr_iso_countries ;

ALTER TABLE ct_simpleaddr_iso_countries
  ADD CONSTRAINT ct_sim_iso_cou_iso_cod_p_kvyqe PRIMARY KEY(iso_code);

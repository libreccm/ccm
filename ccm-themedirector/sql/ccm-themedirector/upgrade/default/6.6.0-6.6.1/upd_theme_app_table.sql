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
-- $Id: upd_theme_app_table.sql pboy $

ALTER TABLE theme_app  drop constraint  them_app_applicatio_id_p_bqozk;
ALTER TABLE theme_app  drop constraint  them_app_applicatio_id_f_34ffo;
ALTER TABLE theme_app  drop constraint  them_app_defau_them_id_f_6plv_;


ALTER TABLE theme_app RENAME TO theme_director ;

ALTER TABLE theme_director
  ADD CONSTRAINT them_direc_applicat_id_p_tnz9g PRIMARY KEY(application_id);

ALTER TABLE theme_director
  ADD CONSTRAINT them_direc_applicat_id_f_rbcnx FOREIGN KEY (application_id)
      REFERENCES applications (application_id);

ALTER TABLE theme_director
  ADD CONSTRAINT them_direc_defa_the_id_f_9ph37 FOREIGN KEY (default_theme_id)
      REFERENCES theme_themes (theme_id);

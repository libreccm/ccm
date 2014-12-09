--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $Id: ren_sites_table.sql pboy $

-- rename table subsite_site to subsite_sites following ccm naming conventions
-- to make maintenance tasks easier


alter table subsite_site drop constraint subsite_site_site_id_p_rz022 ; 
alter table subsite_site drop constraint subs_sit_templ_context_f_6wdu3 ;
alter table subsite_site drop constraint subsit_sit_fron_pag_id_f_4agqx ;
alter table subsite_site drop constraint subsite_site_site_id_f_rntkc ;
alter table subsite_site drop constraint subsite_site_hostname_u_uy5xf ;

alter table subsite_site  RENAME TO  subsite_sites ;

alter table subsite_sites 
      add constraint subsite_sites_site_id_p_wl5ul PRIMARY KEY (site_id) ;
alter table subsite_sites 
      add constraint subs_sit_templ_context_f_mpg0d FOREIGN KEY (template_context)
                     REFERENCES cms_template_use_contexts (use_context);
alter table subsite_sites 
      add constraint subsi_site_fron_pag_id_f_p5cc6 FOREIGN KEY (front_page_id)
                     REFERENCES applications (application_id);
alter table subsite_sites 
      add constraint subsite_sites_site_id_f_nrcet FOREIGN KEY (site_id)
                     REFERENCES acs_objects (object_id);
alter table subsite_sites 
      add constraint subsite_sites_hostname_u_hrgra UNIQUE (hostname) ; 


--
-- Copyright (C) 2012 Peter Boy All Rights Reserved.
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
-- $Id: ren_domainprovider_table.sql pboy $

-- rename ct_contact_* tables to ct_ldn_contact_* tables following
-- ccm naming conventions to make maintenance tasks easier


-- if we could figure out the old names we could rename constraints too
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 

alter table ct_contacts  RENAME TO ct_ldn_contacts ;

-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 


-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 

alter table ct_contact_address  RENAME TO ct_ldn_contact_address ;

-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_p_y5yhy PRIMARY KEY (address_id) ;
-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_f_bfcho FOREIGN KEY (address_id) 
--       REFERENCES cms_items (item_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION;


-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 

alter table ct_contact_phones  RENAME TO ct_ldn_contact_phones ;

-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_p_y5yhy PRIMARY KEY (address_id) ;
-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_f_bfcho FOREIGN KEY (address_id) 
--       REFERENCES cms_items (item_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION;


-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 
-- alter table ct_contacts drop constraint ... ; 

alter table ct_contact_types  RENAME TO ct_ldn_contact_types ;

-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_p_y5yhy PRIMARY KEY (address_id) ;
-- alter table ct_ldn_contact_address 
--       add constraint ct_ldn_con_add_addr_id_f_bfcho FOREIGN KEY (address_id) 
--       REFERENCES cms_items (item_id) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION;


alter table contact_content_item_map drop constraint cont_con_ite_map_ite_i_p_scqe9 ; 
alter table contact_content_item_map drop constraint cont_con_ite_map_con_i_f_lanid ; 
alter table contact_content_item_map drop constraint cont_con_ite_map_ite_i_f_fr0po ; 

alter table contact_content_item_map  RENAME TO ct_ldn_contact_content_item_map ;

alter table ct_ldn_contact_content_item_map 
      add constraint ct_ldn_con_con_ite_map_p_nannu PRIMARY KEY(item_id) ;
alter table ct_ldn_contact_content_item_map 
      add constraint ct_ldn_con_con_ite_map_f_g9mgi foreign key (contact_id)
      references ct_ldn_contacts (contact_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
alter table ct_ldn_contact_content_item_map 
      add constraint ct_ldn_con_con_ite_map_f_a0qiy foreign key (item_id)
      references cms_items(item_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


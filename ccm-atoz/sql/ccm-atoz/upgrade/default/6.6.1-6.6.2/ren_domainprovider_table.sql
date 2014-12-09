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
-- $Id: ren_domainprovider_table.sql pboy $

-- rename table trm_atoz_providers to atoz_trm_providers following
-- ccm naming conventions to make maintenance tasks easier


alter table trm_atoz_providers drop constraint trm_ato_provi_provi_id_p_3qjph ; 
alter table trm_atoz_providers drop constraint trm_ato_provi_provi_id_f_mibvl ; 
alter table trm_atoz_providers drop constraint trm_ato_provide_domain_f_ee4ts ; 


alter table trm_atoz_providers  RENAME TO atoz_trm_providers ;


alter table atoz_trm_providers 
      add constraint atoz_trm_provi_prov_id_p_ifmav PRIMARY KEY (provider_id) ;
alter table atoz_trm_providers 
      add constraint atoz_trm_provi_prov_id_f_yjmjc FOREIGN KEY (provider_id) 
      REFERENCES atoz_provider (provider_id);
alter table atoz_trm_providers 
      add constraint atoz_trm_provid_domain_f_9drhn FOREIGN KEY ("domain") 
      REFERENCES trm_domains ("key");


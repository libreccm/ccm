--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: dmi-create.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


-- data model for DMI: Data Model Initializer 
-- by Bryan Che (bryanche@arsdigita.com)

-- what products are installed: ACS, ECM...
create table dmi_products (
   product_id                      integer
                                   constraint dmi_products_product_id_pk primary key,
   product_name			   varchar(200)
                                   constraint dmi_products_product_name_un unique
                                   constraint dmi_products_product_name_nn not null,
   creation_date		   date
                                   constraint dmi_products_creation_date_nn not null,
   description			   varchar(4000)
);

-- what versions have been installed
create table dmi_product_versions (
   version_id                      integer
                                   constraint dmi_prod_vers_vers_id_pk primary key,
   product_id			   integer
                                   constraint dmi_prod_vers_prd_id_nn not null
                                   constraint dmi_prod_vers_prd_id_fk references dmi_products,
   version_name			   varchar(50)
                                   constraint dmi_product_versions_name_nn not null,
   creation_date		   date
                                   constraint
				   dmi_product_versions_date_nn not null,
   previous_version_id		   integer
                                   constraint dmi_product_versions_prev_v_fk 
				   references dmi_product_versions,
   -- file used to load the data model for this version				   
   install_file			   varchar(300)
                                   constraint dmi_product_versions_file_nn not null, 
   install_errors		   varchar(4000),
   description			   varchar(4000)
);

-- use the same sequence for both tables   		       
create sequence dmi_products_seq;

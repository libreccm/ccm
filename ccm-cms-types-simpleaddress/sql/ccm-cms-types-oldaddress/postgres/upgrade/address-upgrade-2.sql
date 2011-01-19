--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: address-upgrade-2.sql 1597 2007-07-10 16:27:26Z p_boy $
-- $DateTime: 2004/08/17 23:15:09 $

create table temp as select * from ct_addresses;
drop table ct_addresses;

create table ct_addresses (
    address_id INTEGER not null
        constraint ct_addresse_address_id_p_y1u3b
          primary key,
        -- referential constraint for address_id deferred due to circular dependencies
    address VARCHAR(1000),
    email VARCHAR(75),
    fax VARCHAR(20),
    iso_country_code CHAR(2),
        -- referential constraint for iso_country_code deferred due to circular dependencies
    mobile VARCHAR(20),
    notes TEXT,
    phone VARCHAR(20),
    postal_code VARCHAR(20),
    uri VARCHAR(250)
);
alter table ct_addresses add 
    constraint ct_addre_iso_coun_code_f_o8h8a foreign key (iso_country_code)
      references iso_countries(iso_code);
alter table ct_addresses add 
    constraint ct_addresse_address_id_f__qv8u foreign key (address_id)
      references cms_pages(item_id) on delete cascade;

insert into ct_addresses (address_id, address, email, fax, iso_country_code, mobile, notes, phone, postal_code, uri)
 select address_id, address, email, fax, iso_country_code, mobile, encode(notes, 'escape'), phone, postal_code, uri
  from temp;
drop table temp;

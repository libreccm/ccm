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
-- $Id: add_tables_cms_contacts.sql pboy $

-- File has to be processed AFTER add_table_addresses!

create table cms_contacts (
    contact_id integer NOT NULL,
    address_id integer
);

create table cms_contactentries (
    contactentry_id integer NOT NULL,
    contact_id integer,
    "key" character varying(100) NOT NULL,
    description character varying(100),
    value character varying(100) NOT NULL
);

ALTER TABLE ONLY cms_contacts
    ADD CONSTRAINT cms_contact_contact_id_p_kusfp PRIMARY KEY (contact_id);

ALTER TABLE ONLY cms_contactentries
    ADD CONSTRAINT cms_contacte_contac_id_p_wo_wi PRIMARY KEY (contactentry_id);

ALTER TABLE ONLY cms_contacts
    ADD CONSTRAINT cms_contact_address_id_f_wyexp
        FOREIGN KEY (address_id) REFERENCES cms_addresses(address_id);

ALTER TABLE ONLY cms_contacts
    ADD CONSTRAINT cms_contact_contact_id_f_30c_4
        FOREIGN KEY (contact_id) REFERENCES cms_pages(item_id);

ALTER TABLE ONLY cms_contactentries
    ADD CONSTRAINT cms_contacte_contac_id_f_7eg_y
        FOREIGN KEY (contactentry_id) REFERENCES cms_items(item_id);

ALTER TABLE ONLY cms_contactentries
    ADD CONSTRAINT cms_contactent_cont_id_f_2_5m8
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(contact_id);


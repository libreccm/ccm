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
-- $Id: add_tables_cms_person.sql pboy $

-- File has to be processed AFTER add_table_cms_contacts!



create table cms_persons (
    person_id integer NOT NULL,
    surname character varying(512),
    givenname character varying(512),
    titlepre character varying(256),
    titlepost character varying(256),
    birthdate date,
    gender character(1)
);

create table cms_person_contact_map (
    person_id integer NOT NULL,
    contact_id integer NOT NULL,
    link_order integer,
    link_key character varying(100)
);

ALTER TABLE ONLY cms_person_contact_map 
    ADD CONSTRAINT cms_per_con_map_con_id_p_g1cii PRIMARY KEY (contact_id, person_id);

ALTER TABLE ONLY cms_persons 
    ADD CONSTRAINT cms_persons_person_id_p_8z087 PRIMARY KEY (person_id);

ALTER TABLE ONLY cms_person_contact_map
    ADD CONSTRAINT cms_per_con_map_con_id_f_peoc2
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(contact_id);

ALTER TABLE ONLY cms_person_contact_map
    ADD CONSTRAINT cms_per_con_map_per_id_f_g82jn
    FOREIGN KEY (person_id) REFERENCES cms_persons(person_id);

ALTER TABLE ONLY cms_persons
    ADD CONSTRAINT cms_persons_person_id_f_r24km
        FOREIGN KEY (person_id) REFERENCES cms_pages(item_id);
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
-- $Id: add_tables_cms_organisation.sql pboy $

-- File has to be processed AFTER add_table_cms_contacts!
-- File has to be processed AFTER add_table_cms_persons!

create table cms_organizationalunits (
    organizationalunit_id integer NOT NULL,
    addendum character varying(512)
);

create table cms_organizationalunits_contact_map (
    organizationalunit_id integer NOT NULL,
    contact_id integer NOT NULL,
    contact_type character varying(100),
    map_order integer
);

create table cms_organizationalunits_person_map (
    organizationalunit_id integer NOT NULL,
    person_id integer NOT NULL,
    role_name character varying(100)
);

ALTER TABLE ONLY cms_organizationalunits_contact_map
    ADD CONSTRAINT cms_org_con_map_con_id_p_1rc4y
    PRIMARY KEY (contact_id, organizationalunit_id);

ALTER TABLE ONLY cms_organizationalunits_person_map
    ADD CONSTRAINT cms_org_per_map_org_id_p_km6_m
    PRIMARY KEY (person_id, organizationalunit_id);

ALTER TABLE ONLY cms_organizationalunits
    ADD CONSTRAINT cms_organiz_organiz_id_p_kk8qt
    PRIMARY KEY (organizationalunit_id);

ALTER TABLE ONLY cms_organizationalunits_contact_map
    ADD CONSTRAINT cms_org_con_map_con_id_f_9tm3c
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(contact_id);

ALTER TABLE ONLY cms_organizationalunits_contact_map
    ADD CONSTRAINT cms_org_con_map_org_id_f_vdrnx
        FOREIGN KEY (organizationalunit_id)
        REFERENCES cms_organizationalunits(organizationalunit_id);

ALTER TABLE ONLY cms_organizationalunits_person_map
    ADD CONSTRAINT cms_org_per_map_org_id_f_ducb2
        FOREIGN KEY (organizationalunit_id)
        REFERENCES cms_organizationalunits(organizationalunit_id);

ALTER TABLE ONLY cms_organizationalunits_person_map
    ADD CONSTRAINT cms_org_per_map_per_id_f_hrpzh
        FOREIGN KEY (person_id) REFERENCES cms_persons(person_id);

ALTER TABLE ONLY cms_organizationalunits
    ADD CONSTRAINT cms_organiz_organiz_id_f_ubliq
        FOREIGN KEY (organizationalunit_id) REFERENCES cms_pages(item_id);
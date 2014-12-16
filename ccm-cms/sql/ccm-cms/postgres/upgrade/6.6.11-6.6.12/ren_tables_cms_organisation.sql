--
-- Copyright (C) 2014 Peter Boy All Rights Reserved.
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
-- $Id: ren_tables_cms_organisation.sql pboy $

-- Description:
-- Rename tables with names longer than 30 characters to retain Oracle compatibility
-- cms_organizationunits_* to cms_orgaunits_* 
-- This update is only applicable for the scientificcms tree up to version 2.2 
-- Don't use for the APLAWS tree! APLAWS is update just from 1.0.4 to 2.3.x 
-- It corrects various updates
-- * 6.6.0-6.6.1/add_tables_cms_organisation.sql
-- * 6.6.3-6.6.4/create_orgaunit_hierarchy_table.sql
-- * 6.6.4-6.6.5/create_orgaunit_bundle.sql
-- * (6.6.7-6.6.8/add_personsstr_column.sql)
-- 
-- We rename also the constraint where possible. We can't rename the primary key constraint of
-- cms_organizationalunits/cms_orgaunits table
-- (old name: cms_organiz_organiz_id_p_kk8qt, new name: cms_orgauni_orgauni_id_p_ycjef) because
-- some other modules which provide content types which extending GenericOrganizatialUnit are 
-- depending on this constraint. 



-- ALTER TABLE cms_organizationalunits 
--    DROP CONSTRAINT cms_organiz_organiz_id_p_kk8qt;

ALTER TABLE cms_organizationalunits
    DROP CONSTRAINT cms_organiz_organiz_id_f_ubliq;

ALTER TABLE cms_organizationalunits_contact_map 
    DROP CONSTRAINT cms_org_con_map_con_id_p_1rc4y;

ALTER TABLE cms_organizationalunits_person_map 
    DROP CONSTRAINT cms_org_per_map_org_id_p_km6_m;

ALTER TABLE cms_organizationalunits_hierarchy_map 
    DROP CONSTRAINT cms_org_hie_map_sub_or_p_nykpq;

ALTER TABLE cms_organizationalunits 
    RENAME TO cms_orgaunits ;

ALTER TABLE cms_orgaunits 
    RENAME column organizationalunit_id TO orgaunit_id ;

ALTER TABLE cms_organizationalunits_contact_map 
    RENAME TO cms_orgaunits_contact_map ;

ALTER TABLE cms_orgaunits_contact_map
    RENAME column organizationalunit_id TO orgaunit_id ;

ALTER TABLE cms_organizationalunits_person_map 
    RENAME TO cms_orgaunits_person_map ;

ALTER TABLE cms_orgaunits_person_map
    RENAME column organizationalunit_id TO orgaunit_id ;

ALTER TABLE cms_organizationalunits_hierarchy_map 
    RENAME TO cms_orgaunits_hierarchy_map ;

ALTER TABLE ONLY cms_orgaunits_contact_map
    ADD CONSTRAINT cms_org_con_map_con_id_p_pazb1
    PRIMARY KEY (contact_id, orgaunit_id);

-- ALTER TABLE ONLY cms_orgaunits
--    ADD CONSTRAINT cms_orgauni_orgauni_id_p_ycjef
--    PRIMARY KEY (orgaunit_id);

ALTER TABLE cms_orgaunits 
    ADD CONSTRAINT cms_orgauni_orgauni_id_f_qxnsg 
    FOREIGN KEY (orgaunit_id) REFERENCES cms_pages(item_id);

ALTER TABLE ONLY cms_orgaunits_contact_map
    ADD CONSTRAINT cms_org_con_map_con_id_f_i7nai 
    FOREIGN KEY (contact_id) REFERENCES cms_contact_bundles(bundle_id);

ALTER TABLE ONLY cms_orgaunits_contact_map
    ADD CONSTRAINT cms_org_con_map_org_id_f_li14a 
    FOREIGN KEY (orgaunit_id) REFERENCES cms_orgaunit_bundles(bundle_id);

ALTER TABLE ONLY cms_orgaunits_person_map
    ADD CONSTRAINT cms_org_per_map_org_id_p_7t2yy 
    PRIMARY KEY (person_id, orgaunit_id);

ALTER TABLE cms_orgaunits_person_map 
    ADD CONSTRAINT cms_org_per_map_org_id_f_5gx18 
    FOREIGN KEY (orgaunit_id) REFERENCES cms_orgaunit_bundles(bundle_id);

ALTER TABLE cms_orgaunits_person_map 
    ADD CONSTRAINT cms_org_per_map_per_id_f_q2eog 
    FOREIGN KEY (person_id) REFERENCES cms_person_bundles(bundle_id);

ALTER TABLE ONLY cms_orgaunits_hierarchy_map
    ADD CONSTRAINT cms_org_hie_map_sub_or_p_62tce
    PRIMARY KEY (subordinate_orgaunit_id, superior_orgaunit_id);

ALTER TABLE cms_orgaunits_hierarchy_map 
    ADD CONSTRAINT cms_org_hie_map_sub_or_f_r7s1i 
    FOREIGN KEY (subordinate_orgaunit_id) REFERENCES cms_orgaunit_bundles(bundle_id);

ALTER TABLE cms_orgaunits_hierarchy_map 
    ADD CONSTRAINT cms_org_hie_map_sup_or_f_8klhh 
    FOREIGN KEY (superior_orgaunit_id) REFERENCES cms_orgaunit_bundles(bundle_id);



--
-- Copyright (C) 2012 Jens Pelzetter. All Rights Reserved.
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
-- $Id$

CREATE TABLE cms_orgaunit_bundles (bundle_id integer NOT NULL);

ALTER TABLE ONLY cms_orgaunit_bundles 
                 ADD CONSTRAINT cms_orgau_bund_bund_id_p_cfjhf 
                 PRIMARY KEY (bundle_id);

ALTER TABLE cms_orgaunits_contact_map 
            DROP CONSTRAINT cms_org_con_map_org_id_f_vdrnx;

ALTER TABLE ONLY cms_orgaunits_contact_map 
                 ADD CONSTRAINT cms_org_con_map_org_id_f_vdrnx 
                 FOREIGN KEY (orgaunit_id) 
                 REFERENCES cms_orgaunit_bundles(bundle_id);

ALTER TABLE ONLY cms_orgaunit_bundles 
                 ADD CONSTRAINT cms_orgau_bund_bund_id_f_b64mp 
                 FOREIGN KEY (bundle_id) REFERENCES cms_bundles(bundle_id);

ALTER TABLE cms_orgaunits_person_map 
            DROP CONSTRAINT cms_org_per_map_org_id_f_ducb2;
ALTER TABLE cms_orgaunits_person_map 
            DROP CONSTRAINT cms_org_per_map_per_id_f_hrpzh;

ALTER TABLE ONLY cms_orgaunits_person_map 
                 ADD CONSTRAINT cms_org_per_map_org_id_f_ducb2 
                 FOREIGN KEY (orgaunit_id) REFERENCES cms_bundles(bundle_id);
ALTER TABLE ONLY cms_orgaunits_person_map 
                 ADD CONSTRAINT cms_org_per_map_per_id_f_hrpzh 
                 FOREIGN KEY (person_id) REFERENCES cms_bundles(bundle_id);

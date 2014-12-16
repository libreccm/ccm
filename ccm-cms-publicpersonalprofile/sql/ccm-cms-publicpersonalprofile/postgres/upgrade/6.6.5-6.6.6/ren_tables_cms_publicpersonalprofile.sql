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
-- $Id: ren_tables_cms_publicpersonalprofile.sql pboy $

-- Description:
-- Rename tables with names longer than 30 characters to retain Oracle compatibility
-- ct_publicpersonalprofiles_* to ct_ppp_* 
-- This update is only applicable for the scientificcms tree up to version 2.2 
-- Don't use for the APLAWS tree! APLAWS is update just from 1.0.4 to 2.3.x 
-- It corrects various updates
-- * 6.6.0-6.6.1/add_tables_cms_organisation.sql
-- * 6.6.3-6.6.4/create_orgaunit_hierarchy_table.sql
-- * 6.6.4-6.6.5/create_orgaunit_bundle.sql
-- * (6.6.7-6.6.8/add_personsstr_column.sql)


-- Drop contraints with old names 

-- Can't delete primary key constraint because of depencies. 
-- ALTER TABLE ct_public_personal_profiles 
--    DROP CONSTRAINT ct_pub_per_prof_pro_id_p__8_1d;

ALTER TABLE ct_public_personal_profiles
    DROP CONSTRAINT ct_pub_per_prof_pro_id_f_4akoj;

--ALTER TABLE ct_public_personal_profile_bundles 
--    DROP CONSTRAINT ct_pub_per_pro_bun_bun_p_zhc9i;

ALTER TABLE ct_public_personal_profile_bundles 
    DROP CONSTRAINT ct_pub_per_pro_bun_bun_f__jr2_;

ALTER TABLE ct_public_personal_profile_owner_map 
    DROP CONSTRAINT ct_pub_per_pro_own_map_p_rr7ie;

ALTER TABLE ct_public_personal_profile_owner_map 
    DROP CONSTRAINT ct_pub_per_pro_own_map_f_cd7_1;

ALTER TABLE ct_public_personal_profile_owner_map 
    DROP CONSTRAINT ct_pub_per_pro_own_map_f_ugs15;

ALTER TABLE ct_public_personal_profile_nav_items 
    DROP CONSTRAINT ct_pub_per_pro_nav_ite_p_ijb6c;

ALTER TABLE ct_public_personal_profile_nav_items 
    DROP CONSTRAINT ct_pub_per_pro_nav_ite_u_cqkdo;

-- Rename tables
ALTER TABLE ct_public_personal_profiles 
    RENAME TO ct_ppp ;

ALTER TABLE ct_public_personal_profile_bundles 
    RENAME TO ct_ppp_bundles ;

ALTER TABLE ct_public_personal_profile_owner_map 
    RENAME TO ct_ppp_owner_map ;

ALTER TABLE ct_public_personal_profile_nav_items 
    RENAME TO ct_ppp_nav_items ;

-- Recreate contstraints with new names (copied from generated DDL files)
-- ALTER TABLE ct_ppp
--    ADD CONSTRAINT ct_ppp_profile_id_p_ejt_j PRIMARY KEY (profile_id);

ALTER TABLE ct_ppp 
    ADD CONSTRAINT ct_ppp_profile_id_f_7znuj 
    FOREIGN KEY (profile_id)REFERENCES cms_pages(item_id);

-- ALTER TABLE ct_ppp_bundles 
--    ADD CONSTRAINT ct_ppp_bundle_bundl_id_p_eeszn PRIMARY KEY(bundle_id);

ALTER TABLE ct_ppp_bundles 
    ADD CONSTRAINT ct_ppp_bundle_bundl_id_f_1u4im 
    FOREIGN KEY (bundle_id) REFERENCES cms_bundles(bundle_id);

ALTER TABLE ct_ppp_owner_map 
    ADD CONSTRAINT ct_ppp_own_map_own_id__p_rqs7q 
    PRIMARY KEY(owner_id, profile_id);

ALTER TABLE ct_ppp_owner_map 
    ADD CONSTRAINT ct_ppp_own_map_owne_id_f_ouqqr 
    FOREIGN KEY (owner_id) REFERENCES cms_person_bundles(bundle_id);

ALTER TABLE ct_ppp_owner_map 
    ADD CONSTRAINT ct_ppp_own_map_prof_id_f_tnfpj 
    FOREIGN KEY (profile_id) REFERENCES ct_ppp_bundles(bundle_id);

ALTER TABLE ct_ppp_nav_items
    ADD CONSTRAINT ct_ppp_nav_ite_obje_id_p_r7ipd PRIMARY KEY(object_id);

ALTER TABLE ct_ppp_nav_items
    ADD CONSTRAINT ct_ppp_nav_ite_key_lab_u_ecekv UNIQUE("key", lang, label);

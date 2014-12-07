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


-- Difficult to recreate constraints, leave it as is
-- ALTER TABLE ct_public_personal_profiles 
--            DROP CONSTRAINT cms_organiz_organiz_id_p_kk8qt;

ALTER TABLE ct_public_personal_profiles 
            RENAME TO ct_ppp ;

ALTER TABLE ct_public_personal_profiles_bundles 
            RENAME TO ct_ppp_bundles ;

ALTER TABLE ct_public_personal_profiles_owner_map 
            RENAME TO ct_ppp_owner_map ;

ALTER TABLE ct_public_personal_profiles_nav_items 
            RENAME TO ct_ppp_nav_items ;



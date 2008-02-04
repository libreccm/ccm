--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: create-cms_user_home_folder_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create table cms_user_home_folder_map (
    map_id INTEGER not null
        constraint cms_use_hom_fol_map_ma_p_h1be4
          primary key,
        -- referential constraint for map_id deferred due to circular dependencies
    user_id NUMERIC,
        -- referential constraint for user_id deferred due to circular dependencies
    section_id NUMERIC,
        -- referential constraint for section_id deferred due to circular dependencies
    folder_id INTEGER not null
        -- referential constraint for folder_id deferred due to circular dependencies
);
alter table cms_user_home_folder_map add
    constraint cms_use_hom_fol_map_fo_f_etja4 foreign key (folder_id)
      references cms_folders(folder_id);
alter table cms_user_home_folder_map add
    constraint cms_use_hom_fol_map_ma_f_ip3qg foreign key (map_id)
      references acs_objects(object_id);
alter table cms_user_home_folder_map add
    constraint cms_use_hom_fol_map_se_f_7arnr foreign key (section_id)
      references content_sections(section_id);
alter table cms_user_home_folder_map add
    constraint cms_use_hom_fol_map_us_f_g0a5f foreign key (user_id)
      references users(user_id);

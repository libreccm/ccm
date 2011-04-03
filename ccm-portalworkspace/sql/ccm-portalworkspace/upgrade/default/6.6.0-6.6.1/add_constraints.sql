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
-- $Id: add_constraints.sql pboy $


ALTER TABLE ONLY pw_workspace_themeapplications
    ADD CONSTRAINT pw_wor_them_the_app_id_p_2ddxf PRIMARY KEY (theme_app_id);

ALTER TABLE ONLY pw_workspace_workspace_map
    ADD CONSTRAINT pw_wor_wor_map_ref_wor_p_cy2d5
    PRIMARY KEY (referencing_workspace_id, referenced_workspace_id);

ALTER TABLE ONLY pw_workspace_page_layouts
    ADD CONSTRAINT pw_work_pag_lay_format_u_bdjb3 UNIQUE (format);

ALTER TABLE ONLY pw_workspace_page_layouts
    ADD CONSTRAINT pw_work_pag_lay_lay_id_p_1vwf9 PRIMARY KEY (layout_id);

ALTER TABLE ONLY pw_workspace_pages
    ADD CONSTRAINT pw_workspa_page_pag_id_p_gfdzk PRIMARY KEY (page_id);

ALTER TABLE ONLY pw_workspace_themes
    ADD CONSTRAINT pw_workspa_them_the_id_p_62w6p PRIMARY KEY (theme_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspac_workspa_id_p_knd54 PRIMARY KEY (workspace_id);

CREATE INDEX pw_workspaces_party_id_idx ON pw_workspaces USING btree (party_id);

ALTER TABLE ONLY pw_workspace_themeapplications
    ADD CONSTRAINT pw_wor_them_the_app_id_f_p_hb1 FOREIGN KEY (theme_app_id)
    REFERENCES applications(application_id);

ALTER TABLE ONLY pw_workspace_workspace_map
    ADD CONSTRAINT pw_wor_wor_map_ref_wor_f_7a6d2
    FOREIGN KEY (referenced_workspace_id) REFERENCES pw_workspaces(workspace_id);

ALTER TABLE ONLY pw_workspace_workspace_map
    ADD CONSTRAINT pw_wor_wor_map_ref_wor_f_lro20
    FOREIGN KEY (referencing_workspace_id) REFERENCES pw_workspaces(workspace_id);

ALTER TABLE ONLY pw_workspace_pages
    ADD CONSTRAINT pw_works_pag_worksp_id_f_t2tmm FOREIGN KEY (workspace_id)
    REFERENCES pw_workspaces(workspace_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspa_defa_lay_id_f_20goi FOREIGN KEY (default_layout_id)
    REFERENCES pw_workspace_page_layouts(layout_id);

ALTER TABLE ONLY pw_workspace_pages
    ADD CONSTRAINT pw_workspa_pag_layo_id_f_fo5yz FOREIGN KEY (layout_id)
    REFERENCES pw_workspace_page_layouts(layout_id);

ALTER TABLE ONLY pw_workspace_pages
    ADD CONSTRAINT pw_workspa_page_pag_id_f_fz2ep FOREIGN KEY (page_id)
    REFERENCES portals(portal_id);

ALTER TABLE ONLY pw_workspace_themes
    ADD CONSTRAINT pw_workspa_them_the_id_f_my7fo FOREIGN KEY (theme_id)
    REFERENCES acs_objects(object_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspac_workspa_id_f_dpweg FOREIGN KEY (workspace_id)
    REFERENCES applications(application_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspaces_owner_id_f_o_elg FOREIGN KEY (owner_id)
    REFERENCES users(user_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspaces_party_id_f_7tkia FOREIGN KEY (party_id)
    REFERENCES parties(party_id);

ALTER TABLE ONLY pw_workspaces
    ADD CONSTRAINT pw_workspaces_theme_id_f_vsmgl FOREIGN KEY (theme_id)
    REFERENCES pw_workspace_themes(theme_id);


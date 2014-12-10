--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $Id: ren_table_workspaces.sql pboy $

ALTER TABLE workspaces  drop constraint  workspace_workspace_id_p_vm9z2 CASCADE;
ALTER TABLE workspaces  drop constraint  workspac_defau_layo_id_f_xvb7g;
ALTER TABLE workspaces  drop constraint  workspace_workspace_id_f_dted3;
ALTER TABLE workspaces  drop constraint  workspaces_owner_id_f_tpdju;
ALTER TABLE workspaces  drop constraint  workspaces_party_id_f_jotdd;
ALTER TABLE workspaces  drop constraint  workspaces_theme_id_f_tpdju;

DROP INDEX workspaces_party_id_idx ;


ALTER TABLE workspaces RENAME TO pw_workspaces ;


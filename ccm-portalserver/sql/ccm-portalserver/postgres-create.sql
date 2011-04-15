--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //portalserver/dev/sql/ccm-portalserver/postgres-create.sql#2 $
-- $DateTime: 2004/08/17 23:19:25 $

begin;

\i ddl/postgres/create.sql

\i default/index-personal_workspaces_usr_id_idx.sql
\i default/index-workspace_roles_workspc_id_idx.sql
\i default/index-workspaces_theme_id_idx.sql
\i default/index-workspace_tabs_workspac_id_idx.sql
\i default/index-wrkspc_prtcpnt_mp_wrksp_id_idx.sql
\i default/index-wrkspc_wrkspc_mp_rfrn_wrks_idx.sql

\i ddl/postgres/deferred.sql

commit;

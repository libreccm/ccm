begin;
\i ../postgres/upgrade/add-workspace-themes.sql
\i ../postgres/upgrade/add-workspace-owner.sql
\i ../postgres/upgrade/add-workspace_workspace_map.sql
commit;

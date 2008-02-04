begin;
\i ../postgres/upgrade/add-template_mapping_id.sql
\i ../postgres/upgrade/rename-use-context.sql
\i ../postgres/upgrade/add-new-use-context.sql
commit;

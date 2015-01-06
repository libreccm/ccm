\echo 'ScientificCMS Publications module 6.6.6 -> 6.6.7 Upgrade script (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.7-6.6.8/rename_orga_units.sql

end;


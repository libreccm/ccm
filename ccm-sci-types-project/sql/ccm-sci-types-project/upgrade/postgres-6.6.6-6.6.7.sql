\echo 'ScientificCMS SciProject module upgrade 6.6.6 -> 6.6.7 (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.6-6.6.7/create_sponsor_map.sql

end;
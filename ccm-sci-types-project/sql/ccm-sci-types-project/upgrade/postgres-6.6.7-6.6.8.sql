\echo 'ScientificCMS SciProject module upgrade 6.6.7 -> 6.6.8 (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.7-6.6.8/add_sponsor_fundingcode.sql

end;
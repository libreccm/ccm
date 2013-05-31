\echo 'ScientificCMS Publications module 6.6.4 -> 6.6.5 Upgrade script (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.4-6.6.5/vol_of_series_alphanum.sql
\i ../default/upgrade/6.6.4-6.6.5/set_singleton.sql

end;


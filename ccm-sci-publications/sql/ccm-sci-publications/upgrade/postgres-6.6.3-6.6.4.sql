\echo 'ScientificCMS Publications module 6.6.3 -> 6.6.4 Upgrade script (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.3-6.6.4/add-publication-fields.sql
\i ../default/upgrade/6.6.3-6.6.4/add-journal-symbol.sql

end;


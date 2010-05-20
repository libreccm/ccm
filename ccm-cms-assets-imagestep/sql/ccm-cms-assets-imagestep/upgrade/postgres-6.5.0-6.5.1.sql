\echo ImageStep 6.5.0 -> 6.5.1 Upgrade Script (PostgreSQL)

begin;

\i ../postgres/upgrade/6.5.0-6.5.1/add_link_column.sql

commit;

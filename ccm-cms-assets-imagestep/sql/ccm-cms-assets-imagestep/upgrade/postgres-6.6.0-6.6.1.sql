\echo ImageStep 6.5.0 -> 6.5.1 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.6.0-6.6.1/upd_inits.sql
\i ../default/upgrade/6.6.0-6.6.1/upd_acs_objects.sql

commit;

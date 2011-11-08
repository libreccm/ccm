\echo 'Scientific CMS Publications module 6.6.0 -> 6.6.1 Upgrade Script (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.0-6.6.1/update-genericorgaunit-publication-assoc.sql
\i ../default/upgrade/6.6.0-6.6.1/move-reviewed.sql

commit;
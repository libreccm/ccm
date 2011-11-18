\echo 'Scientific CMS Publications module 6.6.1 -> 6.6.2 Upgrade Script (PostgreSQL)'

begin;

\i ../default/upgrade/6.6.1-6.6.2/add-authors-field.sql
\i ../default/upgrade/6.6.1-6.6.2/add-internet-articles-properties.sql

commit;
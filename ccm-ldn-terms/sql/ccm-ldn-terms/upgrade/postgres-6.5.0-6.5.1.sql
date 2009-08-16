begin;
\i ../ddl/postgres/table-trm_domains_indexer-auto.sql
\i ../postgres/upgrade/6.5.0-6.5.1/table-trm_domains_indexer-deferred.sql
commit;

\echo ImageStep 6.6.1 -> 6.6.2 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.6.1-6.6.2/cms_item_image_attachment.sql

commit;

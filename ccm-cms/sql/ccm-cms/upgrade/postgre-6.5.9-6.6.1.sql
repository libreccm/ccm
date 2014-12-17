--
-- Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--

-- This is part 2 of the split upgrade 6.6.0-6.6.1. See the explanation in 6.5.9-6.6.0

\echo Red Hat Enterprise CMS 6.5.9 -> 6.6.1 Upgrade Script (PostgreSQL)

begin;

\i ../default/upgrade/6.6.0-6.6.1/add_table_cms_addresses.sql
\i ../default/upgrade/6.6.0-6.6.1/add_tables_cms_contacts.sql
\i ../default/upgrade/6.6.0-6.6.1/add_tables_cms_persons.sql
\i ../default/upgrade/6.6.0-6.6.1/add_tables_cms_organisation.sql
\i ../default/upgrade/6.6.0-6.6.1/add_table_cms_rel_attr.sql
\i ../postgres/upgrade/6.6.0-6.6.1/upd_table_cms_publ_links.sql
\i ../default/upgrade/6.6.0-6.6.1/drop_table_cms_article_image_map.sql
\i ../postgres/upgrade/6.6.0-6.6.1/drop_old_cms_articles.sql
\i ../default/upgrade/6.6.0-6.6.1/upd_table_cms_articles.sql
\i ../default/upgrade/6.6.0-6.6.1/upd_table_authoring_steps.sql


commit;
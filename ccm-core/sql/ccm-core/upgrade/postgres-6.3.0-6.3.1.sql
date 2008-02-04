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
-- $Id: postgres-6.1.1-6.1.2.sql 723 2005-08-23 16:56:33Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

begin;
\i ../postgres/upgrade/6.3.0-6.3.1/add_column_catpurpose_lang.sql
\i ../postgres/upgrade/6.3.0-6.3.1/add_column_users_banned.sql
\i ../default/upgrade/6.3.0-6.3.1/preferred-categories.sql
\i ../default/upgrade/6.3.0-6.3.1/auto-categorization.sql
create or replace function last_day(date) returns date as 'select
cast(date_trunc(''month'', $1) + ''1 month''::interval as date) - 1'
language sql;
commit;

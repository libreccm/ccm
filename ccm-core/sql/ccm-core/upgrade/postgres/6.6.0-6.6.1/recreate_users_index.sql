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
-- $Id: recreateusers_index.sql pboy $

-- for some unkown reason for some ccm installations an index for
-- users tables has been lost. Just in case it is recreated here.

-- First: Drop index to avoid an error if it already exists
drop index if exists users_lower_screen_name_idx ;

create unique index users_lower_screen_name_idx on users
       USING btree (lower((screen_name)::text));
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
-- NOTE - easier version:  alter table messages alter column body type text;
-- only works for Postgres 8.x




alter table messages add large_body text NOT NULL;

update messages set large_body = body;

alter table messages drop column body;

alter table messages rename column large_body to body;


COMMENT ON COLUMN messages.body IS '
    Body of the message.
'; 

commit;


-- reclaim disk space from old column

UPDATE messages SET body = body;

-- VACUUM can not be executed inside a transaction block. (Doesn't matter 
-- whether before or after the commit; is a transaction block anyway!)
-- Should be performed after update has finished.
-- VACUUM FULL messages;

commit;


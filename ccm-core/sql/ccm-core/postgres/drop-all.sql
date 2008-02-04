--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: drop-all.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace function drop_all(varchar) returns boolean as '
declare
    username alias for $1;
    drop_type varchar;
    row record;
begin
    for row in select *
               from pg_class, pg_user
               where pg_class.relowner = pg_user.usesysid
               and cast(usename as varchar) = username
               and relkind in (''r'', ''v'', ''S'')
               and relname not like ''pg_%'' loop
        if row.relkind = ''r'' then
          drop_type := ''table'';
        elsif row.relkind = ''v'' then
          drop_type := ''view'';
        elsif row.relkind = ''S'' then
          drop_type := ''sequence'';
        else
          drop_type := ''none'';
        end if;

        execute ''drop '' || drop_type || '' '' || row.relname;
    end loop;

    return true;
end;
' language 'plpgsql';

select drop_all(user());

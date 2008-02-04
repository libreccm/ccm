--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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



--
-- c:/tinman/enterprise/dev/kernel/sql/oracle-se/drop-all.sql
-- 
-- Utility function dropping all objects in a user's schema.
--
-- @author Bryan Quinn (bquinn@arsdigita.com) 
-- @creation-date July 22, 2001 17:29:09
-- @cvs-id $Id: drop-all.sql 287 2005-02-22 00:29:02Z sskracic $

begin
   ctx_ddl.drop_section_group('autogroup');
END;
/

declare
    cursor objects is
        select object_name, object_type
        from user_objects;
begin
    for object in objects loop
        begin
            if object.object_type = 'TABLE' then
                execute immediate 'drop table ' || object.object_name || ' cascade constraints';
            elsif object.object_type = 'SEQUENCE' or object.object_type = 'INDEX' or object.object_type = 'FUNCTION' or
                object.object_type = 'PROCEDURE' or object.object_type = 'VIEW' or object.object_type = 'PACKAGE' then
                execute immediate 'drop ' || object.object_type || ' ' || object.object_name;
            elsif object.object_type = 'UNDEFINED' and object.object_name like '%_MV' then
                execute immediate 'drop materialized view ' || object.object_name;
            end if;
        exception when others then
            null;
        end;
    end loop;
end;
/
show errors

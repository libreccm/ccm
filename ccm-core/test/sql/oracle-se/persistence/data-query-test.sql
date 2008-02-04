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
-- $Id: data-query-test.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #8 $ $Date: 2004/08/16 $
--

create table t_data_query (
    entry_id    integer constraint t_data_query_entry_id_pk primary key,
    action      varchar(100) not null,
    description varchar(4000),
    priority    integer not null,
    action_time date not null
);

begin
    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (0, 'read', 'Read item 0', 1, to_date('1976-12-24', 'YYYY-MM-DD') + 0);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (1, 'read', 'Read item 1', 2, to_date('1976-12-24', 'YYYY-MM-DD') + 4);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (2, 'read', 'Read item 2', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 8);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (3, 'read', 'Read item 3', 7, to_date('1976-12-24', 'YYYY-MM-DD') + 10);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (4, 'read', 'Read item 4', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 11);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (5, 'write', 'Wrote item 5', 9, to_date('1976-12-24', 'YYYY-MM-DD') + 11.3);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (6, 'write', 'Wrote item 6', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 11.79);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (7, 'write', 'Wrote item 7', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 24.3);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (8, 'write', 'Wrote item 8', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 28.119);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (9, 'write', 'Wrote item 9', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 31);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (10, 'delete', 'Deleted item 10', 7, to_date('1976-12-24', 'YYYY-MM-DD') + 47);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (11, 'delete', 'Deleted item 11', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 92);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (12, 'delete', 'Deleted item 12', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 102);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (13, 'delete', 'Deleted item 13', 9, to_date('1976-12-24', 'YYYY-MM-DD') + 228);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (14, 'delete', 'Deleted item 14', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 228);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (15, 'create', 'Created item 15', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 230);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (16, 'create', 'Created item 16', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 241);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (17, 'create', 'Created item 17', 4, to_date('1976-12-24', 'YYYY-MM-DD') + 281);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (18, 'create', 'Created item 18', 3, to_date('1976-12-24', 'YYYY-MM-DD') + 384);

    insert into t_data_query
    (entry_id, action, description, priority, action_time)
    values
    (19, 'create', 'Created item 19', 8, to_date('1976-12-24', 'YYYY-MM-DD') + 12203);

    commit;
end;
/
show errors


create or replace function DataOperationProcWithReturn(v_entry_id in integer) return number
is
   v_priority number;
begin
   select priority into v_priority from t_data_query 
    where entry_id = v_entry_id;
   return v_priority;
end;
/
show errors

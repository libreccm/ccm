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
-- $Id: data-operation-test.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #10 $ $Date: 2004/08/16 $
--

create or replace procedure DataOperationProcedure as
    my_variable	integer;
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors


create or replace function DataOperationFunction 
return varchar
is 
  toReturn varchar(300);
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into toReturn from t_data_query;   
   return toReturn;
end;
/
show errors

create or replace procedure DataOperationProcWithOut(v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithOut(v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithInOut(
       v_old_id IN varchar,
       v_new_id OUT varchar) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select v_old_id, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithInOutInt(
       v_old_id IN Integer,
       v_new_id OUT Integer) 
as 
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select v_old_id, action, priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
   select max(entry_id) into v_new_id from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcWithDates(
       v_id_to_update IN Integer,
       v_old_date IN Date,
       v_new_date OUT Date) 
as 
begin
   update t_data_query set action_time = v_old_date
   where entry_id = v_id_to_update;
   select max(action_time) into v_new_date from t_data_query;   
end;
/
show errors

create or replace procedure DataOperationProcedureWithArgs(v_priority in integer)
as
begin
   insert into t_data_query (entry_id, action, priority, action_time)
   select entry_id + 1, action, v_priority, action_time from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors

create or replace procedure DataOperationProcedureOneArg(v_description in integer)
as
begin
   insert into t_data_query (entry_id, action, priority, action_time, description)
   select entry_id + 1, action, priority, action_time, v_description 
   from t_data_query 
   where entry_id = (select max(entry_id) from t_data_query);
end;
/
show errors

create or replace procedure PLSQLWithArbitraryArgs(v_arg1 in integer, 
       v_arg2 in integer, 
       v_arg3 in integer default null, 
       v_arg4 in integer default null, 
       v_arg5 in integer default null)
as
begin
        insert into PLSQLTestTable 
        values 
        (v_arg1, v_arg2, v_arg3, v_arg4, v_arg5);
end;
/
show errors

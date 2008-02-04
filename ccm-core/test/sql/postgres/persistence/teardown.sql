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
-- $Id: teardown.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file cleans up all the test data models necessary for the persistence
-- tests to run.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #18 $ $Date: 2004/08/16 $
--

-- Right now this is duplicated from the individual files. This should
-- be fixed at some point.



@@ ../../default/persistence/teardown.sql

drop table t_data_query;
drop table t_sql_operation;

drop function DataOperationProcedure();
drop function DataOperationFunction();
drop function DataOperationProcWithReturn(integer);
drop function DataOperationProcWithOut(varchar);
drop function DataOperationProcWithInOut(integer);
drop function DataOperationProcWithInOutInt(integer);
drop function DataOperationProcWithDates(integer, timestamp);
drop function DataOperationProcedureOneArg(varchar);
drop function DataOperationProcedureWithArgs(integer);
drop function PLSQLWithArbitraryArgs(integer, integer, integer, integer, integer);

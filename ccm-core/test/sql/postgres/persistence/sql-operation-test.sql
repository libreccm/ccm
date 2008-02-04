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
-- $Id: sql-operation-test.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--
-- This file contains the data model for the data query test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #9 $ $Date: 2004/08/16 $
--

create table t_sql_operation (
	big_integer numeric(38),
	big_decimal float,
	boolean numeric(1),
	byte char,
	single_char char,
	multi_char char(3),
	date_col date,
	double_col double precision,
	float_col float,
	int_col int,
	long_col int,
	short_col smallint,
	string_clob text,
	string_normal varchar(10),
	byte_array_blob bytea,
	byte_array char(20));

insert into t_sql_operation values(
42, 
139.93213, 
1, 
7,
'a', 
'bcd', 
TO_DATE('January 15, 1989, 11:00 A.M.','Month dd, YYYY, HH:MI A.M.'),
8883951.12341, 
739.31431, 
5026,
99999999, 
100, 
'clob column!', 
'normal', 
'12345', 
'normal array');


insert into t_sql_operation values(
null, 
null, 
0, 
null,
null, 
null, 
null,
null, 
null, 
null,
null, 
null, 
null, 
null, 
null, 
null);

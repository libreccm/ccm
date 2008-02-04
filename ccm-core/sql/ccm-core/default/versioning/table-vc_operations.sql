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
-- $Id: table-vc_operations.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $



create table vc_operations (
  operation_id      integer 
    constraint vc_operations_pk primary key,
  transaction_id    integer
    constraint vc_operations_trans_id_fk references vc_transactions
    on delete cascade,
  action            varchar(200)
    constraint vc_operations_actions_fk references vc_actions,
  attribute         varchar(200),
  classname         varchar(4000) 
    constraint vc_operations_classname_nn not null
);

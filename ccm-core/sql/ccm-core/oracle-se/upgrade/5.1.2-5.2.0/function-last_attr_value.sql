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
-- $Id: function-last_attr_value.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

-- The function that retrieves the last known value of an attribute.
-- It starts with start_transaction_id and backtracks through history,
-- finding the most recent record of changing the attribute and
-- returning it.

create or replace
function last_attr_value(attr varchar, start_transaction_id in integer)
return varchar
is
  v_master_id integer;
  start_time date;
  end_time date;
begin
  -- The caller of this function already knows the master_id.  We
  -- could add an optional parameter that, when provided enables us to
  -- avoid this query.

  select master_id, timestamp into v_master_id, start_time
    from vc_transactions
    where transaction_id = start_transaction_id;

  declare
    cursor c is
      select new_value
        from vc_transactions t, vc_operations o, vc_generic_operations go
        where t.master_id = v_master_id
              and t.timestamp <= start_time
              and t.transaction_id = o.transaction_id
              and o.operation_id = go.operation_id
              and o.attribute = attr
              and go.new_value is not null
        order by t.timestamp desc;
  begin
    for row in c loop
      return row.new_value;
    end loop;
  end;

  return null;
end last_attr_value;
/
show errors

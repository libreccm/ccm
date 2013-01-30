--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: alter-lucene.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

alter table lucene_docs drop column is_dirty;
alter table lucene_docs add dirty integer;
update lucene_docs set dirty = 2147483647;
alter table lucene_docs alter dirty set not null;

create or replace function lucene_update_dirty(timestamptz, timestamptz, integer, integer)
returns integer
as '
declare
  oldTimestamp alias for $1;
  newTimestamp alias for $2;
  oldDirty alias for $3;
  newDirty alias for $4;
begin
  if date_trunc(''minute'', newTimestamp) < date_trunc(''minute'', oldTimestamp) then
    return oldDirty;
  else
    if newDirty <> 2147483647 then
        return bitand(newDirty, oldDirty);
    else
        return newDirty;
    end if;
  end if;
end;' language 'plpgsql';

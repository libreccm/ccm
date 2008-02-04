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
-- $Id: change-lifecycles-phases-column-type.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


alter table lifecycles add (startDateTemp date);
alter table lifecycles add (endDateTemp date);
alter table phases add (startDateTemp date);
alter table phases add (endDateTemp date);

create global temporary table updateTimeZonesTable (
currentTime timestamp with time zone);

-- we create the table and do the insert to get the time stamp.  There
-- may be a much better way to do this but I am not sure what it is
-- we have to adjust for the time zone because when the java created the
-- number it took the time zone in to account but standard multiplication
-- does not.  This would then leave us several hours off the mark.
insert into updateTimeZonesTable values (localtimestamp);

declare
  offsetSign integer;
  hourOffset integer;
  minuteOffset integer;
begin
  select TO_CHAR(currentTime, 'TZH') into hourOffset from updateTimeZonesTable;
  select TO_CHAR(currentTime, 'TZM') into minuteOffset from updateTimeZonesTable;
  if (hourOffset < 0) then
     offsetSign := -1;
  else 
     offsetSign := 1;
  end if;

  update phases set startDateTemp = to_date('01-01-1970', 'MM-DD-YYYY') + (start_date_time/3600000)/24 + hourOffset/24 + minuteOffset/24/60*offsetSign, endDateTemp = to_date('01-01-1970', 'MM-DD-YYYY') + (end_date_time/3600000)/24 + hourOffset/24 + (minuteOffset/24/60)*offsetSign where start_date_time is not null;

  update lifecycles set startDateTemp = to_date('01-01-1970', 'MM-DD-YYYY') + (start_date_time/3600000)/24 + hourOffset/24 + minuteOffset/24/60*offsetSign, endDateTemp = to_date('01-01-1970', 'MM-DD-YYYY') + (end_date_time/3600000)/24 + hourOffset/24 + (minuteOffset/24/60)*offsetSign where start_date_time is not null;

end;
/
show errors;

drop table updateTimeZonesTable;


alter table lifecycles drop column end_date_time;
alter table lifecycles drop column start_date_time;
alter table phases drop column end_date_time;
alter table phases drop column start_date_time;

alter table lifecycles add (start_date_time date);
alter table lifecycles add (end_date_time date);
alter table phases add (start_date_time date);
alter table phases add (end_date_time date);

update phases set end_date_time = endDateTemp, start_date_time = startDateTemp;
update lifecycles set end_date_time = endDateTemp, start_date_time = startDateTemp;

alter table phases modify (start_date_time date not null);

alter table lifecycles drop column endDateTemp;
alter table lifecycles drop column startDateTemp;
alter table phases drop column endDateTemp;
alter table phases drop column startDateTemp;

-- These columns changed from varchar2(1) to char(1)
alter table lifecycles modify (has_begun char(1));
alter table lifecycles modify (has_ended char(1));
alter table phases modify (has_begun char(1));
alter table phases modify (has_ended char(1));

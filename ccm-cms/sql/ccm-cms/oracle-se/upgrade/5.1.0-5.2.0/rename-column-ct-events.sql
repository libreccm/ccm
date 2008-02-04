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
-- $Id: rename-column-ct-events.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table ct_events_temp as select * from ct_events;

drop table ct_events;
create table ct_events (
    item_id INTEGER not null
        constraint ct_events_item_id_p_56u9u
          primary key,
        -- referential constraint for item_id deferred due to circular dependencies
    cost VARCHAR(1000),
    end_date DATE,
    end_time DATE,
    event_date VARCHAR(1000),
    event_type VARCHAR(1000),
    location VARCHAR(1000),
    main_contributor VARCHAR(1000),
    map_link VARCHAR(1000),
    start_date DATE,
    start_time DATE,
    tease_lead VARCHAR(4000)
);

alter table ct_events add 
    constraint ct_events_item_id_f_v7kjv foreign key (item_id)
      references cms_text_pages(item_id) on delete cascade;

insert into ct_events
   (item_id,
    cost,
    end_date,
    end_time,
    event_date,
    event_type,
    location,
    main_contributor,
    map_link,
    start_date,
    start_time,
    tease_lead)
 select 
    item_id,
    cost,
    end_date,
    end_time,
    event_date,
    event_type,
    location,
    mact_contributor,
    map_link,
    start_date,
    start_time,
    tease_lead
    from ct_events_temp;

drop table ct_events_temp;

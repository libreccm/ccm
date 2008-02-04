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
-- $Id: table-nt_queue.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

-- Explanation of request status:
-- pending             request is only in the request table, not queued
-- queued              request is in the queue, failed 0 or more times
-- sent                request has been processed successfully
-- failed              request has failed max_retries times without sucess
-- failed_partial      some components of the request of have failed,
--                     others have succeeded (only applies when
--                     recipient is a group and expand_p = 1) 
-- cancelled           request was cancelled

-- Outbound message queue

create table nt_queue (
    request_id        integer
                      constraint nt_queue_request_fk 
                          references nt_requests(request_id)
                      on delete cascade,
    party_to          integer
                      constraint nt_queue_party_to_fk
                          references parties(party_id)
                      on delete cascade,
    retry_count       integer
                      default 0,
    success_p         char(1)
                      default '1'
                      constraint nt_queue_success_ck
                          check (success_p in ('0','1')),
    constraint nt_queue_composite_pk primary key (request_id, party_to)
);

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
-- $Id: table-nt_requests.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


create table nt_requests (
    request_id        integer
                      constraint nt_requests_pk
                          primary key
                      constraint nt_requests_fk
                          references acs_objects(object_id),
    digest_id         integer
                      constraint nt_requests_digest_fk
                          references nt_digests(digest_id),
    party_to          integer
                      constraint nt_requests_party_to_fk
                          references parties(party_id),
    message_id        integer                         
                      constraint nt_requests_message_fk
                          references messages(message_id),
    header            varchar(4000),
    signature         varchar(4000),
    expand_group      char(1)
                      default '1'
                      constraint nt_requests_expand_ck
                          check (expand_group in ('0','1')),
    request_date      date
                      default sysdate,
    fulfill_date      date,
    status            varchar(20)
                      default 'pending'
                      constraint nt_requests_status_ck
                          check (status in 
                              ('pending',
                               'queued',
                               'sent',
                               'failed_partial',
                               'failed',
                               'cancelled')),
    max_retries       integer
                      default 3
                      constraint nt_requests_retries_nn
                          not null,
    expunge_p         char(1)
                      default '1'
                      constraint nt_requests_expunge_ck
                          check (expunge_p in ('0','1')),
    expunge_msg_p     char(1)
                      default '1'
                      constraint nt_requests_expunge_msg_ck
                         check (expunge_msg_p in ('0','1'))
);

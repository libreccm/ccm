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
-- $Id: table-messages.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table messages ( 
    message_id     integer
                   constraint messages_message_id_fk
                       references acs_objects(object_id)
                   constraint messages_message_id_pk
                       primary key,
    object_id      integer
                   constraint messages_object_id_fk
                       references acs_objects(object_id) on delete cascade,
    reply_to       varchar(250),
    sender         integer
                   constraint messages_sender_fk  
                       references parties (party_id),
    subject        varchar(250)
                   constraint messages_subject_nn not null,
    body           clob
                   constraint messages_body_nn not null,
    type           varchar(50)
                   constraint messages_type_nn not null,
    sent_date      date 
                   default sysdate
                   constraint messages_sent_date_nn not null,
    in_reply_to    integer
                   constraint messages_reply_to_fk
                       references messages(message_id) on delete set null,
    rfc_message_id varchar(250),
    root_id        integer
                   constraint messages_root_id_fk
                       references messages(message_id) on delete cascade,
    sort_key       varchar(300)
);

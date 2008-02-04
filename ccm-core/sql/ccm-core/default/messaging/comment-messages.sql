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
-- $Id: comment-messages.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table messages is '
    A generic message which may be attached to any object in the system.
';
comment on column messages.message_id is '
    Primary key for messages.
';
comment on column messages.object_id is '
    An optional ACSObject that this message is attached to.  For example, 
    comments might be attached to a ContentItem, or bboard posts might
    be attached to a forum.
';
comment on column messages.rfc_message_id is '
    The RFC 822 Message-ID when a
    message is transported out of the system via email.
';
comment on column messages.in_reply_to is '
    Pointer to a message this message contains a reply to, for threading.
';
comment on column messages.sent_date is '
    The date the message was sent (may be distinct from when it was created
    or published in the system.)
';
comment on column messages.reply_to is '
    Returned e-mail address. This may be different than sender.
';
comment on column messages.sender is '
    The party who sent the message (may be distinct from the person who
    entered the message in the system.)
';
comment on column messages.subject is '
    The subject of the message.
';
comment on column messages.body is '
    Body of the message.
';
comment on column messages.type is '
    MIME type of the body, should be text/plain or text/html.
';
comment on column messages.root_id is '
    Root message for all elements of a thread.  Combined with the sort
    key, this uniquely determines the location of a threaded message.
';
comment on column messages.sort_key is '
    Sort key for generating threaded messages.  Large enough to store
    100 levels of messages with the 3 characters per level.
';

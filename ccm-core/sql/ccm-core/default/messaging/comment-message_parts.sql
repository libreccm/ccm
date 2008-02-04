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
-- $Id: comment-message_parts.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table message_parts is '
    A table to store the content parts of a message.  A message is
    determined to be "multipart/mixed" by virtue of having more than
    one part.
';
comment on column message_parts.part_id is '
    Primary key for the message_parts table, doubles as the Content-ID
    when a full MIME Part is created from a row of this table.
';
comment on column message_parts.message_id is '
    Pointer to the message that contains this part.
';
comment on column message_parts.type is '
    MIME type of this part.
';
comment on column message_parts.name is '
    Name of the part.
';
comment on column message_parts.description is '
    Description of the part.
';
comment on column message_parts.disposition is '
    Disposition of the part.  The disposition describes how the part
    should be presented to the user (see RFC 2183).
';
comment on column message_parts.headers is '
    Other MIME headers, stored as multiple lines in a single text
    block.  They are all optional.
';
comment on column message_parts.content is '
    Content of the part.  Proper handling of the content is determined
    by its MIME type.
';

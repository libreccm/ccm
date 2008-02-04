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
-- $Id: comment-preferences.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table preferences is '
    The table stores the preference information. An entry can either be a
    preference node or a key-value pair parameter.
';

comment on column preferences.parent_id is '
    The parent preference id.
';

comment on column preferences.name is '
    Must be unique within the parent node directory.
';

comment on column preferences.is_node is '
    Whether the preference is a node or key-value pair. Note that in case of a
    node, we have no entries in the value_* fields.
';

comment on column preferences.preference_type is '
    Currently we see ''user'' and ''system'' preference types; this might be
    extended in the future.
';

comment on column preferences.value_type is '
    Here go the parameter values in case this is a parameter. null is a
    legitimate value, eg., in case someone specifies an empty string.
';

comment on column preferences.value_string is '
    The value string can hold int, long, float, double, boolean, string and
    byte[].
';

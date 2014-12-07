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
-- $Id: comment-content_types.sql 2155 2011-01-21 01:17:02Z pboy $
-- $DateTime: 2004/08/17 23:15:09 $

comment on table content_types is '
  The type of content
';
comment on column content_types.object_type is '
  The object type that is associated with this content type
';
comment on column content_types.classname is '
  The java class the implements this content type
';
comment on column content_types.label is '
  The pretty name for this content type
';
 comment on column content_types.type_mode is '
  Saves the mode of the content type: I = internal, H = hidden, D = Default (a
  content type used in its normal way)

  An internal content type is one that is not user-defined and maintained
  internally. A content type should be made internal under the following
  two conditions:
  1) The object type needs to take advantage of content type services
  (i.e., versioning, categorization, lifecycle, workflow) that are already
  implemented in CMS.
  2) The content type cannot be explicitly registered to a content section.
  The Template content type is one such internal content type.

  A hidden content type is one that cannot be used directly but other content
  types can extend from it. Also, it is a legit parent for UDCTs.
';

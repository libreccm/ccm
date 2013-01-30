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
-- $Id: comment-acs_privilege_hierarchy.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on TABLE ACS_PRIVILEGE_HIERARCHY is '
  To reduce number of permission records, and to simplify permission
queries and associated business logic in Java layer, this table stores
privilege hierarchy.  This hierarchy allows child nodes to have multiple
parents, and parents to have multiple children.
  This table stores only direct (parent,child) mappings, which is often
referred as adjacency model, resulting in small table.  
';


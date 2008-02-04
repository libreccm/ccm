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
-- $Id: comment-bebop_components.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table bebop_components is '
 Stores data for the Component Data Object (used by
 persistent Bebop Components).
';
comment on column bebop_components.admin_name is '
 A name that helps administrators identify the Component.
';
comment on column bebop_components.description is '
 As description that helps users use the Component.
';
comment on column bebop_components.attribute_string is '
 This is the attribute string of the Component on the XML attribute
 format 
        key1="value1" key2="value2" ... keyN="valueN"
';
comment on column bebop_components.active_p is '
 If this is true the component is active and will be displayed. By
 setting this column to false an admin has disabled a component without
 having to delete it and with the option of activating it later.
';

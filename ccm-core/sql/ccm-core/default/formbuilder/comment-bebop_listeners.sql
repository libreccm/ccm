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
-- $Id: comment-bebop_listeners.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table bebop_listeners is '
 For storing listener classes that are added to form sections or widgets.
 The table is used for listener types that can have more than one instenance
 mapped to a component. An exception is the PrintListener since a Widget
 can have only one Printlistener.
';
comment on column bebop_listeners.class_name is '
 The class name of the listener. Lets you persist any listener. Precondition is
 that the listener has a default constructor. No attributes will be set.
';
comment on column bebop_listeners.attribute_string is '
 For persistent listeners that need store attributes. Is on XML attribute format
 just like the column in bebop_components.
';

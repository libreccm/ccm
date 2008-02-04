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
-- $Id: comment-bebop_widgets.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table bebop_widgets is '
 Stores data needed specificly to persisting objects of class
 Widget.
';
comment on column bebop_widgets.parameter_name is '
 We currently only support the StringParameter class for the
 parameter model of the widget. This is the name that this
 class takes in its constructor.
';
comment on column bebop_widgets.parameter_name is '
 If a process listener does not dictate a certain parameter model
 it might be desirable for an admin to be able to set one.
';
comment on column bebop_widgets.default_value is '
 This is the default value of the Component. This corresponds to
 the text between the tags or the value attribute in the XHTML representation.
';

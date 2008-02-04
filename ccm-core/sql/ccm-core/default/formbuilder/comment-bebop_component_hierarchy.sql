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
-- $Id: comment-bebop_component_hierarchy.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

comment on table bebop_component_hierarchy is '
 This table contains information about the Component hierarchy 
 contained in a Bebop form. Examples of relationships stored in this table
 are that between a FormSection and its Widgets and that between
 an OptionGroup and its Options.
';
comment on column bebop_component_hierarchy.container_id is '
 This is the component id of the containing component. Examples include FormSection
 and OptionGroup.
';
comment on column bebop_component_hierarchy.component_id is '
 This will typically be a Bebop Widget or another type of Component 
 used in Forms, for example a Label.
';
comment on column bebop_component_hierarchy.order_number is '
 This is the order in which the components were added to their container.
';
comment on column bebop_component_hierarchy.selected_p is '
 OptionGroups need to store information about which Options are selected.
';

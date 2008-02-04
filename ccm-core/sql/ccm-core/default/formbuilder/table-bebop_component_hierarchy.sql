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
-- $Id: table-bebop_component_hierarchy.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table bebop_component_hierarchy (
       container_id              integer
                                 constraint bebop_component_hierarchyci_fk
                                 references bebop_components (component_id),
       component_id              integer
                                 constraint bebop_component_hierarchyco_fk
                                 references bebop_components(component_id),
       order_number              integer,
       selected_p                char(1),
       constraint bebop_component_hierarchy_un
       unique(container_id, component_id)
);

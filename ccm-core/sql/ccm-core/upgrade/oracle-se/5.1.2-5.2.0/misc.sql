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
-- $Id: misc.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

alter table acs_permissions modify ( 
    creation_date default null
);

alter table acs_stylesheet_node_map modify (
    stylesheet_id not null,
    node_id not null
);

alter table acs_stylesheets modify (
    output_type default null
);

alter table acs_stylesheet_type_map modify (
    stylesheet_id not null,
    package_type_id not null
);

alter table apm_package_types modify (
    dispatcher_class default null
);

alter table email_addresses modify (
    bouncing_p not null,
    verified_p not null
);

alter table party_email_map modify (
    email_address not null
);

alter table preferences modify (
    is_node number
);

alter table roles modify (
    group_id null
);

alter table site_nodes modify (
    directory_p default null,
    pattern_p default null
);

alter table vc_transactions modify (
    timestamp date default sysdate
);

alter table group_member_map drop column id;
alter table group_subgroup_map drop column id;

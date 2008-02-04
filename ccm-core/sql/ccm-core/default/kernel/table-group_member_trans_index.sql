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
-- $Id: table-group_member_trans_index.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table group_member_trans_index (
	group_id	integer 
                constraint gmti_group_id_nn
                not null
			    constraint gmti_group_id_fk
			    references groups(group_id) on delete cascade,
	member_id	integer
                constraint gmti_subgroup_id_nn
                not null
			    constraint gmti_subgroup_id_fk
			    references users(user_id) on delete cascade,
    n_paths     integer not null,
	constraint gmti_group_party_pk primary key(group_id, member_id),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gmti_n_paths_ck
                check (n_paths>0)
);

-- XXX organization index;

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
-- $Id: tests.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table tests (
    test_id INTEGER not null
        constraint tests_test_id_p_cq728
          primary key,
    name VARCHAR(200),
    optional_self_id INTEGER
        constraint tests_optional_self_id_f_5060l
          references tests(test_id),
    optional_id INTEGER
        constraint tests_optional_id_f_n9xio
          references icles(icle_id),
    required_id INTEGER not null
        constraint tests_required_id_f_swp2a
          references icles(icle_id),
    parent_id INTEGER
        constraint tests_parent_id_f_hlfvv
          references tests(test_id)
)

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
-- $Id: table-ss_answers.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:26:27 $
create table ss_answers (
       answer_id        integer
                        constraint ss_answers_pk
                        primary key,                        
       response_id      integer
                        constraint ss_answers_rid_fk
                        references ss_responses (response_id)
			on delete cascade,
--                        constraint ss_answers_rid_nn
--                        not null,
       label_id         integer
                        constraint ss_answers_lid_fk
                        references bebop_components (component_id),
--                        constraint ss_answers_lid_nn
--                        not null,
       widget_id        integer
                        constraint ss_answers_wid_fk
                        references bebop_widgets (widget_id),
--                        constraint ss_answers_wid_nn
--                        not null,
       value            varchar(4000)
);

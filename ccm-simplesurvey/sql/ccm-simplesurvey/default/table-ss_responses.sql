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
-- $Id: table-ss_responses.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:26:27 $
create table ss_responses (
       response_id      integer
                        constraint ss_responses_pk
                        primary key,
       survey_id        integer
                        constraint ss_responses_sid_fk
                        references ss_surveys (survey_id)
			on delete cascade
                        constraint ss_responses_sid_nn
                        not null,                       
       user_id          integer
                        constraint ss_responses_uid_fk
                        references users (user_id)
                        constraint ss_responses_uid_nn
                        not null,
       entry_date       date
                        constraint ss_responses_ed_nn
                        not null,
       score            integer
);

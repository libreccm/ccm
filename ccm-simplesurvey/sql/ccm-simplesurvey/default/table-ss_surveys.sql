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
-- $Id: table-ss_surveys.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:26:27 $
create table ss_surveys (
       survey_id        integer
                        constraint ss_surveys_pk
                        primary key
                        constraint ss_surveys_id_fk
                        references acs_objects (object_id),
       package_id integer
			constraint ss_surveys_pid_fk
			references apm_packages (package_id)
			constraint ss_surveys_pid_nn
			not null,
       form_id          integer
                        constraint ss_surveys_fid_fk
                        references bebop_form_sections (form_section_id)
                        constraint ss_surveys_fid_nn
                        not null,
       start_date       date,
       end_date         date,
       responses_public_p integer default (1) not null,
       quiz_type varchar(50) default 'knowledge_test' not null check (quiz_type in ('knowledge_test','personal_assessment'))
);

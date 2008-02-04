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
-- $Id: table-bebop_form_process_listeners.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table bebop_form_process_listeners (
        form_section_id         integer
                                constraint bebop_form_process_lstnr_fs_fk
                                references bebop_form_sections,
        listener_id             integer
                                constraint bebop_form_process_lstnr_li_fk
                                references bebop_process_listeners,
        position                integer,
        constraint bebop_form_process_lstnr_pk
        primary key (form_section_id, listener_id),
        constraint bebop_form_process_lstnr_un
	unique (form_section_id, position)
);

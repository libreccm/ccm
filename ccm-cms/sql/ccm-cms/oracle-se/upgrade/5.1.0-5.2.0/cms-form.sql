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
-- $Id: cms-form.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_form_item (
    item_id integer
        constraint cms_form_item_fk references
        cms_pages (item_id) on delete cascade
        constraint cms_form_item_pk primary key,
    form_id integer
        constraint cms_form_item_frm_fk references
        bebop_form_sections (form_section_id) on delete cascade
);

create table cms_form_section_item (
    item_id integer 
        constraint cms_form_section_item_id_fk references
        cms_pages (item_id) on delete cascade
        constraint cms_form_section_item_id_pk primary key,
    form_section_id integer
        constraint cms_form_section_item_frm_fk references
        bebop_form_sections (form_section_id) on delete cascade
);

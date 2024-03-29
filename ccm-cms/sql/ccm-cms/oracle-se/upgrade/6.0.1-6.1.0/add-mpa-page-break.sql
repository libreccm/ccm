--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: add-mpa-page-break.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
alter table ct_mp_sections add ( page_break char(1) );
update ct_mp_sections set page_break = '1';
alter table ct_mp_sections add constraint
  ct_mp_sectio_pag_break_c_660vp
  check (page_break in ('0', '1'));
alter table ct_mp_sections modify ( page_break not null );

alter table ct_mp_sections modify ( rank not null );

create index ct_mp_sections_article_id_idx on ct_mp_sections(article_id);

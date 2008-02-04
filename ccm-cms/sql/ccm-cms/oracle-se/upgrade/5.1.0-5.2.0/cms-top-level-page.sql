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
-- $Id: cms-top-level-page.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_top_level_pages (
    page_id INTEGER not null
        constraint cms_top_lev_pag_pag_id_p_gi1ry
          primary key,
        -- referential constraint for page_id deferred due to circular dependencies
    template_id INTEGER not null
        -- referential constraint for template_id deferred due to circular dependencies
);

alter table cms_top_level_pages add 
    constraint cms_top_lev_pag_pag_id_f_a6bhw foreign key (page_id)
      references cms_pages(item_id) on delete cascade;
alter table cms_top_level_pages add 
    constraint cms_top_lev_pag_tem_id_f_d26nu foreign key (template_id)
      references cms_templates(template_id);

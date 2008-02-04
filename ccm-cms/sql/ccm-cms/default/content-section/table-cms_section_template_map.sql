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
-- $Id: table-cms_section_template_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_section_template_map (
  mapping_id    integer 
                constraint cms_stm_mapping_id_fk references 
                acs_objects,
  section_id    integer 
                constraint cms_stm_section_id_fk references 
                content_sections,
  type_id       integer 
                constraint cms_stm_type_id_fk references 
                content_types,
  template_id   integer 
                constraint cms_stm_template_id_fk references 
                cms_templates,
  use_context   varchar(200) default 'public'
                constraint cms_stm_use_ctx_fk references
                cms_template_use_contexts,
  is_default    varchar(1) default '0' not null,
                constraint cms_stm_is_def_bool check
		(is_default in ('1', '0')),
  constraint cms_section_template_map_pk primary key(mapping_id)
);

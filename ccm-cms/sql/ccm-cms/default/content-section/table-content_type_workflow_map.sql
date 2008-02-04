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
-- $Id: table-content_type_workflow_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table content_type_workflow_map (
  section_id           integer 
                       constraint ctwf_map_section_id_fk 
                       references content_sections on delete cascade,
  content_type_id      integer 
                       constraint ctwf_map_content_type_id_fk 
                       references content_types on delete cascade,
  wf_template_id       integer 
                       constraint ctwf_map_wf_template_id_fk 
                       references cw_process_definitions on delete cascade
                       constraint ctwf_map_wf_template_id_nil not null,
  constraint content_type_workflow_map_pk 
  primary key (section_id, content_type_id)
);
-- organization index;

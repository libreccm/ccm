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
-- $Id: table-content_sections.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

--bryanche todo: changed section_id fk from acs_objects to sections,
--dropped column package_id

create table content_sections (
  section_id           integer
                       constraint content_sections_pk
                       primary key
                       constraint csections_section_id_fk 
                       references applications(application_id),
  pretty_name          varchar(300) not null
                       constraint csections_name_un
                       unique,
  root_folder_id       integer 
                       constraint csections_root_folder_id_nil
                       not null
                       constraint csections_root_folder_id_fk
                       references cms_folders,
  templates_folder_id  integer 
                       constraint csections_temps_folder_id_fk
                       references cms_folders,
  staff_group_id       integer not null
                       constraint csections_staff_group_id_fk
                       references groups,
  viewers_group_id     integer not null
                       constraint csections_viewers_group_id_fk
                       references groups,
  page_resolver_class  varchar(1000) not null,
  item_resolver_class  varchar(1000) not null,
  template_resolver_class  varchar(1000) not null,
  xml_generator_class  varchar(1000) not null
);

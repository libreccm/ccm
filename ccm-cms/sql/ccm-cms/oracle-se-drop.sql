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
-- $Id: oracle-se-drop.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

@@ ddl/oracle-se/drop-constraints.sql

drop sequence convert_format_seq;
drop sequence publish_to_file_system_seq;

drop table cms_form_section_wrapper;
--drop table publish_to_fs_notify_broken;
drop table publish_to_fs_links;
drop table publish_to_fs_files;
drop table cms_standalone_pages;
drop table cms_item_template_map;
drop table cms_section_template_map;
drop table cms_template_use_contexts;
drop table cms_templates;
drop table cms_wf_notifications;
drop table cms_tasks;
drop table cms_task_types;
drop table cms_resource_map;
drop table cms_resources;
drop table cms_resource_types;
drop table cms_form_section_item;
drop table cms_form_item;
drop table cms_article_image_map;
drop table cms_articles;
drop table cms_text_pages;
drop table authoring_kits;
drop table cms_section_locales_map;
drop table content_type_workflow_map;
drop table content_type_lifecycle_map;
drop table content_section_type_map;
drop table content_sections;
drop table cms_variants;
drop table cms_folders;
drop table cms_items;
drop table content_types;
--drop table publication_status;
drop table cms_privileges;

@@ ddl/oracle-se/drop-tables.sql

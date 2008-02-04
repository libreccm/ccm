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
-- $Id: comment-content_sections.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

comment on table content_sections is '
  A content section represents a CMS application instance. Each section 
  has a root folder, root category, staff group, and viewers group.
';
comment on column content_sections.root_folder_id is '
  The topmost (root) folder for draft items in this content section. All
  items in this folder will have their version equal to ''draft''. Live
  items are in the live version of this folder.
';
comment on column content_sections.templates_folder_id is '
  The topmost (root) folder for all templates in the content section
';
comment on column content_sections.staff_group_id is '
  Each content section will have a staff group, which will contain
  all privileged groups under this section.
';
comment on column content_sections.viewers_group_id is '
  Each content section will have a viewers group, which will contain
  all non-privileged groups under this section.
';
comment on column content_sections.page_resolver_class is '
  The page resolver class is the name Java class that is used to map
  URL stubs to CMS resources within a specific section.
';
comment on column content_sections.item_resolver_class is '
  The item resolver class is the name Java class that is used to map
  URL stubs to content items within a specific section. The item
  resolver will also fetch the template associated with a content item.
';
comment on column content_sections.template_resolver_class is '
  The template resolver class is the name Java class that is used to 
  obtain the template context associated with a request.
';
comment on column content_sections.xml_generator_class is '
  The XML generator class is the name Java class that is used to output
  content items in XML.
';

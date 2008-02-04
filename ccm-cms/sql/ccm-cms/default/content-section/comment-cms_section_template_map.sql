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
-- $Id: comment-cms_section_template_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

comment on table cms_section_template_map is '
  Maps (content_section + type) to templates. Each content type
  within each section can have a different subset of templates.
  This table defines a set of templates which could possibly
  be assigned to content items.
';

comment on column cms_section_template_map.use_context is '
  A string that describes the context in which the template is
  to be used. There can be multiple templates registered to
  a content type in a certain context; for example, a 
  public template may be used to display items on the public
  pages, and a summary template may be used to display items
  as search results.
';

comment on column cms_section_template_map.is_default is '
  A boolean value that determines whether the given template
  is the default template for its use context.
';

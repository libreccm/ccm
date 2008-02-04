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
-- $Id: comment-cms_items.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

comment on table cms_items is '
  Basic metadata required of every content item.
  Items come in two versions: ''live'' and ''draft'', which are stored 
  in separate folder hierarchies, live items in the hierarchy starting with
  a content section''s live_folder_id, draft items in the one with id
  draft_folder_id.
';
comment on column cms_items.parent_id is '
  The primary folder for this item.  Used to build a virtual item
  hierarchy for the purpose of assigning unique URLs to all items.
  Root level folders have a parent_id of null. There should not be any
  non-folders with null parent_id''s.
';
comment on column cms_items.version is '
  The version tag of the item can be set to ''live'' for live version and
  ''draft'' for the draft version.
';
comment on column cms_items.section_id is '
  This is a denormalization of the content section for items that exist
  within the folder hierarchy (site map) of a content section.
';

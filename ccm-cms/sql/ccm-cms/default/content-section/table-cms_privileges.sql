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
-- $Id: table-cms_privileges.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_privileges (
  privilege            varchar(100) not null
                       constraint cms_privileges_pk 
                       primary key,
  pretty_name          varchar(200) not null,
  scope                varchar(20) not null
                       constraint cms_privileges_scope_ck
                       check ( scope in ('section', 'folder', 'item') ),
  viewer_appropriate   char(1) default '0' not null
                       constraint cms_privileges_viewer_ck
                       check ( viewer_appropriate in ('0','1') ),
  sort_order           integer
);

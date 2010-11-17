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
-- $Id: table-content_types.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table content_types (
  type_id	  integer 
                  constraint content_types_type_id_fk references
		  acs_objects
		  constraint content_types_pk primary key,
  object_type     varchar(100) 
		  constraint content_types_object_type_un unique
		  constraint content_types_object_type_nil not null,
  label		  varchar(1000) not null,
  description	  varchar(4000),
  classname       varchar(200),
  ancestors       varchar(2000),
  siblings        varchar(2000),
--  is_internal     char(1) default '0' not null
--                  constraint content_types_is_internal_ck
--                  check ( is_internal in ('0', '1') ),
  mode            char(1) default '' not null
                  constraint content_types_mode_ck
                  check ( mode in ('D', 'H', 'I') ),
  item_form_id    integer
                  constraint content_types_form_id_fk references
	          bebop_components (component_id)
);

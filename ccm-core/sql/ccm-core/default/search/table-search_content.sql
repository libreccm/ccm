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
-- $Id: table-search_content.sql 469 2005-03-20 23:12:44Z mbooth $
-- $DateTime: 2004/08/16 18:10:38 $

create table search_content (
    object_id         	integer
			constraint search_content_id_fk references
			acs_objects (object_id) on delete cascade
	          	constraint search_content_pk primary key,
    object_type         varchar(100), -- Same as acs_object(object_type)
                        -- denormalized to reduce joins
    link_text           varchar(1000),
    url_stub            varchar(1000),
    summary             varchar(4000),
    xml_content         clob,  -- xml content to be indexed
    raw_content         blob,  -- non-xml content to be indexed
    language            varchar(3),
    content_section     varchar(300)
);

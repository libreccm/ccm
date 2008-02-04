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
-- $Id: table-cat_purposes.sql 1267 2006-07-18 08:30:06Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table cat_purposes (
    purpose_id         integer
                       constraint cat_purposes_purpose_id_fk
                       references acs_objects
                       constraint cat_purposes_pk
                       primary key,
    key                varchar(40) not null,
    name               varchar(200) not null,
    description        varchar(4000),
    language           char(2) default 'en' not null,
                       constraint cat_purposes_lang_un unique (key, language)
);


--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: alter-lucene.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


-- XXX PG 7.2 doesn't let us drop columns easily

create table temp as select * from lucene_docs;
drop table lucene_docs;

create table lucene_docs (
    document_id INTEGER not null
        constraint lucen_docs_document_id_p_2riv8
          primary key,
    content TEXT,
    country CHAR(2),
    creation_date TIMESTAMP,
    creation_party INTEGER,
    dirty INTEGER not null,
    is_deleted BOOLEAN not null,
    language CHAR(2),
    last_modified_date TIMESTAMP,
    last_modified_party INTEGER,
    summary VARCHAR(4000),
    timestamp TIMESTAMP not null,
    title VARCHAR(4000) not null,
    type VARCHAR(200) not null,
    type_info VARCHAR(4000)
);

insert into lucene_docs select document_id, content, country,
  creation_date, creation_party, 2147483647, is_deleted,
  language, last_modified_date, last_modified_party,
  summary, timestamp, title, type, type_info from temp;

drop table temp;

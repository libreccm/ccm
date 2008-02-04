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
-- $Id: add-search-test.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table search_test_author (
    author_id INTEGER not null
        constraint sear_tes_auth_autho_id_p_i_7fz
          primary key,
        -- referential constraint for author_id deferred due to circular dependencies
    name VARCHAR(100)
);

create table search_test_book (
    book_id INTEGER not null
        constraint searc_tes_book_book_id_p_vylnb
          primary key,
        -- referential constraint for book_id deferred due to circular dependencies
    title VARCHAR(100)
);

create table search_test_book_chapter (
    chapter_id INTEGER not null
        constraint sear_tes_boo_cha_cha_i_p_qchkk
          primary key,
        -- referential constraint for chapter_id deferred due to circular dependencies
    chapter_num INTEGER,
    content CLOB
);

alter table search_test_author add 
    constraint sear_tes_auth_autho_id_f_klil2 foreign key (author_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book add 
    constraint searc_tes_book_book_id_f_eqgc0 foreign key (book_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book_chapter add 
    constraint sear_tes_boo_cha_cha_i_f_fonpi foreign key (chapter_id)
      references acs_objects(object_id) on delete cascade;

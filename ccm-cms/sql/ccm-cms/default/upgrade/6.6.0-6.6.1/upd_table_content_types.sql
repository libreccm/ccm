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
-- $Id: upd_table_content_types.sql pboy $

alter table content_types
   drop constraint content_types_is_internal_ck ;

alter table content_types
    add column ancestors character varying(2000),
    add column descendants character varying(2000) ;


update content_types
    set is_internal = 'D' where is_internal like '0' ;

update content_types
    set is_internal = 'I' where is_internal like '1' ;

alter table content_types
    alter is_internal drop default ;

alter table content_types
    rename column is_internal TO type_mode;

alter table content_types
    add constraint content_types_mode_ck CHECK
        (("type_mode" = ANY (ARRAY['D'::bpchar, 'H'::bpchar, 'I'::bpchar]))) ;

alter table content_types
    alter type_mode set default '0'::bpchar ;

alter table content_types
    alter type_mode set NOT NULL ;

COMMENT ON
  COLUMN content_types.type_mode  IS '
  Saves the mode of the content type: I = internal, H = hidden, D = default (a
  content type used in its normal way)

  An internal content type is one that is not user-defined and maintained
  internally. A content type should be made internal under the following
  two conditions:
  1) The object type needs to take advantage of content type services
  (i.e., versioning, categorization, lifecycle, workflow) that are already
  implemented in CMS.
  2) The content type cannot be explicitly registered to a content section.
  The Template content type is one such internal content type.

  A hidden content type is one that cannot be used directly but other content
  types can extend from it. Also, it is a legit parent for UDCTs.
';

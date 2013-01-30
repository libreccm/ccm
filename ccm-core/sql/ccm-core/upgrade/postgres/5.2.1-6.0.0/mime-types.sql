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
-- $Id: mime-types.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


create or replace function temp_mime_types() returns boolean as '
declare
  v_exists boolean;
begin
  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''cms_image_mime_types'';

  if (not v_exists) then
    execute ''
    create table cms_image_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_ima_mim_typ_mim_ty_p_9jrgn
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        sizer_class VARCHAR(4000)
    )'';

    execute ''
    create table cms_mime_extensions (
        file_extension VARCHAR(200) not null
            constraint cms_mim_ext_fil_extens_p_pnyhk
              primary key,
        mime_type VARCHAR(200) not null
    )'';

    execute ''
    create table cms_mime_status (
        mime_status_id INTEGER not null
            constraint cms_mim_sta_mim_sta_id_p_m5ygm
              primary key,
        hash_code INTEGER not null,
        inso_filter_works INTEGER not null
    )'';

    execute ''
    create table cms_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_mim_type_mime_type_p_kl0ds
              primary key,
        label VARCHAR(200) not null,
        file_extension VARCHAR(200) not null,
        java_class VARCHAR(4000) not null,
        object_type VARCHAR(4000) not null
    )'';

    execute ''
    create table cms_text_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_tex_mim_typ_mim_ty_p_3qbec
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        is_inso CHAR(1) not null
    )'';

    execute ''
    alter table cms_image_mime_types add
        constraint cms_ima_mim_typ_mim_ty_f_s0zsx foreign key (mime_type)
          references cms_mime_types(mime_type)'';

    execute ''
    alter table cms_text_mime_types add
        constraint cms_tex_mim_typ_mim_ty_f__tubf foreign key (mime_type)
          references cms_mime_types(mime_type)'';
  else
    execute ''comment on table cms_image_mime_types is NULL'';
    execute ''comment on column cms_image_mime_types.sizer_class is NULL'';
    execute ''comment on table cms_mime_types is NULL'';
    execute ''comment on column cms_mime_types.file_extension is NULL'';

  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''pre_convert_html'';

  if (v_exists) then
    execute ''drop table pre_convert_html'';
  end if;

  select count(*) into v_exists
    from pg_class
   where relkind = ''r''
         and lower(relname) = ''post_convert_html'';

  if (v_exists) then
    execute ''drop table post_convert_html'';
  end if;

  return TRUE;
end;
' language 'plpgsql';

select temp_mime_types();
drop function temp_mime_types();

create table pre_convert_html (
    id INTEGER not null
        constraint pre_convert_html_id_p_osi1n
          primary key,
    content BYTEA
);

create table post_convert_html (
    query_id INTEGER not null
        constraint post_conve_htm_quer_id_p_qgdg9
          primary key,
    document TEXT
);

update cms_mime_types
   set java_class = 'com.arsdigita.mimetypes.' || substring(java_class from 1 + char_length('com.arsdigita.cms.'))
 where position('com.arsdigita.cms.' in java_class) = 1;

update cms_image_mime_types
   set sizer_class = 'com.arsdigita.mimetypes.' || substring(sizer_class from 1 + length('com.arsdigita.cms.'))
 where position('com.arsdigita.cms.' in sizer_class) = 1;

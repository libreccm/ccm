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


declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'cms_image_mime_types';

  if (v_exists = '0') then
    execute immediate '
    create table cms_image_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_ima_mim_typ_mim_ty_p_9jrgn
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        sizer_class VARCHAR(4000)
    )';

    execute immediate '
    create table cms_mime_extensions (
        file_extension VARCHAR(200) not null
            constraint cms_mim_ext_fil_extens_p_pnyhk
              primary key,
        mime_type VARCHAR(200) not null
    )';

    execute immediate '
    create table cms_mime_status (
        mime_status_id INTEGER not null
            constraint cms_mim_sta_mim_sta_id_p_m5ygm
              primary key,
        hash_code INTEGER not null,
        inso_filter_works INTEGER not null
    )';

    execute immediate '
    create table cms_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_mim_type_mime_type_p_kl0ds
              primary key,
        label VARCHAR(200) not null,
        file_extension VARCHAR(200) not null,
        java_class VARCHAR(4000) not null,
        object_type VARCHAR(4000) not null
    )';

    execute immediate '
    create table cms_text_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_tex_mim_typ_mim_ty_p_3qbec
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        is_inso CHAR(1) not null
    )';

    execute immediate '
    alter table cms_image_mime_types add
        constraint cms_ima_mim_typ_mim_ty_f_s0zsx foreign key (mime_type)
          references cms_mime_types(mime_type)';

    execute immediate '
    alter table cms_text_mime_types add
        constraint cms_tex_mim_typ_mim_ty_f__tubf foreign key (mime_type)
          references cms_mime_types(mime_type)';
  else
    execute immediate 'comment on table cms_image_mime_types is ''''';
    execute immediate 'comment on column cms_image_mime_types.sizer_class is ''''';
    execute immediate 'comment on table cms_mime_types is ''''';
    execute immediate 'comment on column cms_mime_types.file_extension is ''''';
    execute immediate 'comment on column cms_mime_types.java_class is ''''';
    execute immediate 'comment on column cms_mime_types.object_type is ''''';
    execute immediate 'comment on table cms_text_mime_types is ''''';
    execute immediate 'comment on column cms_text_mime_types.is_inso is ''''';

  end if;
end;
/
show errors;

declare
  v_exists char(1);
begin

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'pre_convert_html';

  if (v_exists = '1') then
    execute immediate 'drop index convert_to_html_index';
    execute immediate 'drop table pre_convert_html';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'post_convert_html';

  if (v_exists = '1') then
    execute immediate 'drop table post_convert_html';
  end if;

end;
/
show errors;

create table pre_convert_html (
    id INTEGER not null
        constraint pre_convert_html_id_p_osi1n
          primary key,
    content BLOB
);
create index convert_to_html_index on pre_convert_html(content) indextype is
ctxsys.context parameters('filter ctxsys.inso_filter');

create table post_convert_html (
    query_id INTEGER not null
        constraint post_conve_htm_quer_id_p_qgdg9
          primary key,
    document CLOB
);

update cms_mime_types
   set java_class = 'com.arsdigita.mimetypes.' || substr(java_class, 1 + length('com.arsdigita.cms.'))
 where instr(java_class, 'com.arsdigita.cms.') = 1;

update cms_image_mime_types
   set sizer_class = 'com.arsdigita.mimetypes.' || substr(sizer_class, 1 + length('com.arsdigita.cms.'))
 where instr(sizer_class, 'com.arsdigita.cms.') = 1;

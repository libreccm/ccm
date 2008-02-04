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
-- $Id: cms_category_template_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create table cms_category_template_map (
    mapping_id INTEGER not null
        constraint cms_cat_tem_map_map_id_p_iq1jy
          primary key,
        -- referential constraint for mapping_id deferred due to circular dependencies
    category_id INTEGER not null,
        -- referential constraint for category_id deferred due to circular dependencies
    type_id INTEGER not null,
        -- referential constraint for type_id deferred due to circular dependencies
    section_id INTEGER not null,
        -- referential constraint for section_id deferred due to circular dependencies
    template_id INTEGER not null,
        -- referential constraint for template_id deferred due to circular dependencies
    use_context VARCHAR(200) not null,
    is_default CHAR(1) not null
);

alter table cms_category_template_map add
    constraint cms_cat_tem_map_cat_id_f_fa56u foreign key (category_id)
      references cat_categories(category_id);
alter table cms_category_template_map add
    constraint cms_cat_tem_map_map_id_f_8eq3y foreign key (mapping_id)
      references acs_objects(object_id);
alter table cms_category_template_map add
    constraint cms_cat_tem_map_sec_id_f_3p3qy foreign key (section_id)
      references content_sections(section_id);
alter table cms_category_template_map add
    constraint cms_cat_tem_map_tem_id_f_rdnza foreign key (template_id)
      references cms_templates(template_id);
alter table cms_category_template_map add
    constraint cms_cat_tem_map_typ_id_f_ls_qa foreign key (type_id)
      references content_types(type_id);

create unique index cms_ctm_unique on cms_category_template_map
  (category_id, type_id, template_id, use_context);

comment on table cms_category_template_map is '
  Maps (category + type) to templates. Each content type
  within each category can have a different subset of templates.
  This table defines a set of templates which could possibly
  be assigned to content items.
';

comment on column cms_category_template_map.use_context is '
  A string that describes the context in which the template is
  to be used. There can be multiple templates registered to
  a content type in a certain context; for example, a
  public template may be used to display items on the public
  pages, and a summary template may be used to display items
  as search results.
';

comment on column cms_category_template_map.is_default is '
  A boolean value that determines whether the given template
  is the default template for its use context.
';

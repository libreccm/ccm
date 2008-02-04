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
-- $Id: misc.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- Comments
comment on table cms_image_mime_types is NULL;
comment on column cms_image_mime_types.sizer_class is NULL;


-- Fix Constraint Ordering
-- NOTE: The following ddl assumes that no tables have a referential constraint against one
--       of these tables.  This holds for core and *should* hold in general as these tables
--       are unlikely cadidates for foreign keys.
--------------------------------------------------------------------------------
alter table content_section_type_map drop constraint content_section_type_map_pk;
alter table content_section_type_map add
    constraint cont_sec_typ_map_sec_i_p_cjrtg
        primary key(type_id, section_id);

alter table section_workflow_template_map drop constraint sect_wor_tem_map_sec_i_p_jaofv;
alter table section_workflow_template_map add
    constraint sect_wor_tem_map_sec_i_p_jaofv
        primary key(wf_template_id, section_id);


-- Misc tables added
--------------------------------------------------------------------------------
create table cms_wf_notifications (
    task_id integer
        constraint cms_wf_not_pk primary key
        constraint cms_wf_not_fk references cms_tasks
          on delete cascade,
    last_sent_date date,
    num_sent integer
);

create table ct_content_groups (
    group_id INTEGER not null
        constraint ct_conte_group_grou_id_p_gfzk0
          primary key
        -- referential constraint for group_id deferred due to circular dependencies
);

alter table ct_content_groups add
    constraint ct_conte_group_grou_id_f_xtb5p foreign key (group_id)
      references cms_items(item_id);

create table ct_content_group_item_map (
    item_id INTEGER not null
        constraint ct_con_gro_ite_map_ite_p_2whhj
          primary key,
        -- referential constraint for item_id deferred due to circular dependencies
    group_id INTEGER not null,
        -- referential constraint for group_id deferred due to circular dependencies
    related_item_id INTEGER,
        -- referential constraint for related_item_id deferred due to circular dependencies
    sort_key INTEGER not null
);

alter table ct_content_group_item_map add
    constraint ct_con_gro_ite_map_gro_f_fr6ny foreign key (group_id)
      references ct_content_groups(group_id);
alter table ct_content_group_item_map add
    constraint ct_con_gro_ite_map_ite_f_rq3ic foreign key (item_id)
      references cms_items(item_id);
alter table ct_content_group_item_map add
    constraint ct_con_gro_ite_map_rel_f_gbf0_ foreign key (related_item_id)
      references cms_items(item_id);

create table ct_item_file_attachments (
    file_id INTEGER not null
        constraint ct_ite_fil_atta_fil_id_p_hq5uv
          primary key,
        -- referential constraint for file_id deferred due to circular dependencies
    owner_id INTEGER
        -- referential constraint for owner_id deferred due to circular dependencies
);

alter table ct_item_file_attachments add
    constraint ct_ite_fil_atta_fil_id_f_maoph foreign key (file_id)
      references cms_files(file_id);
alter table ct_item_file_attachments add
    constraint ct_ite_fil_atta_own_id_f_28vk4 foreign key (owner_id)
      references cms_items(item_id);

create table portlet_tasks (
    portlet_id INTEGER not null
        constraint portle_task_portlet_id_p_w11sv
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    numTasks INTEGER
);

alter table portlet_tasks add
    constraint portle_task_portlet_id_f_95ljj foreign key (portlet_id)
      references portlets(portlet_id);

create table portlet_content_item (
    portlet_id INTEGER not null
        constraint port_cont_ite_portl_id_p_fikuf
          primary key,
    item_id INTEGER
);

alter table portlet_content_item add
    constraint port_cont_ite_portl_id_f_n19z_ foreign key (portlet_id)
      references portlets(portlet_id);
alter table portlet_content_item add
    constraint portl_conte_ite_ite_id_f_aft9p foreign key (item_id)
      references cms_items(item_id);

-- The 'is_homepage' column of the 'ct_news' table went from integer to char(1)
--------------------------------------------------------------------------------
create table temp_ct_news (
    item_id integer,
    is_homepage char(1)
);

insert into temp_ct_news
  (item_id, is_homepage)
select item_id,
       case is_homepage when 0 then '0'
                        else '1'
       end as is_homepage_char
  from ct_news;

alter table ct_news drop column is_homepage;
alter table ct_news add is_homepage CHAR(1);

update ct_news
   set is_homepage = ( select temp_ct_news.is_homepage
                         from temp_ct_news
                        where temp_ct_news.item_id = ct_news.item_id);

alter table ct_news alter is_homepage set not null;

drop table temp_ct_news;

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

-- Actions that require PL/SQL
----------------------------------------------------------------------------------
declare
  version varchar2(4000);
  compatibility varchar2(4000);
  v_constraint_name varchar2(32);
  v_index_name varchar2(32);
  v_exists char(1);
begin

  -- Rename constraints
  --------------------------------------------------------------------------------
  DBMS_UTILITY.DB_VERSION (version, compatibility);
  if (compatibility >= '9.2.0.0.0') then
    -- The following ddl will only work on Oracle 9.2 or greater

    v_constraint_name := 'cms_item_template_map_pk';

    select count(*) into v_exists
      from user_constraints uc
     where lower(table_name) = 'cms_item_template_map'
       and constraint_type = 'P'
       and lower(constraint_name) != v_constraint_name;

    if (v_exists = '1') then
      select constraint_name into v_constraint_name
        from user_constraints uc
       where lower(table_name) = 'cms_item_template_map'
         and constraint_type = 'P'
         and lower(constraint_name) != v_constraint_name;

      execute immediate 'alter table cms_item_template_map rename constraint ' ||  v_constraint_name || ' to cms_item_template_map_pk';
    end if;

    v_constraint_name := 'cms_section_template_map_pk';

    select count(*) into v_exists
      from user_constraints uc
     where lower(table_name) = 'cms_section_template_map'
       and constraint_type = 'P'
       and lower(constraint_name) != v_constraint_name;

    if (v_exists = '1') then
      select constraint_name into v_constraint_name
        from user_constraints uc
       where lower(table_name) = 'cms_section_template_map'
         and constraint_type = 'P'
         and lower(constraint_name) != v_constraint_name;
      execute immediate 'alter table cms_section_template_map rename constraint ' ||  v_constraint_name || ' to cms_section_template_map_pk';
    end if;
  end if;

  select count(*) into v_exists
    from user_indexes ui, user_ind_columns uic
   where lower(ui.table_name) = 'cms_section_template_map'
     and lower(ui.index_name) != 'cms_section_template_map_pk'
     and lower(uic.column_name) = 'mapping_id'
     and uic.column_position = 1
     and ui.index_name = uic.index_name;

  if (v_exists = '1') then
    select ui.index_name into v_index_name
      from user_indexes ui, user_ind_columns uic
     where lower(ui.table_name) = 'cms_section_template_map'
       and lower(ui.index_name) != 'cms_section_template_map_pk'
       and lower(uic.column_name) = 'mapping_id'
       and uic.column_position = 1
       and ui.index_name = uic.index_name;
    execute immediate 'alter index ' ||  v_index_name || ' rename to cms_section_template_map_pk';
  end if;

  select count(*) into v_exists
    from user_indexes ui, user_ind_columns uic
   where lower(ui.table_name) = 'cms_item_template_map'
     and lower(ui.index_name) != 'cms_item_template_map_pk'
     and lower(uic.column_name) = 'mapping_id'
     and uic.column_position = 1
     and ui.index_name = uic.index_name;

  if (v_exists = '1') then
    select ui.index_name into v_index_name
      from user_indexes ui, user_ind_columns uic
     where lower(ui.table_name) = 'cms_item_template_map'
       and lower(ui.index_name) != 'cms_item_template_map_pk'
       and lower(uic.column_name) = 'mapping_id'
       and uic.column_position = 1
       and ui.index_name = uic.index_name;
    execute immediate 'alter index ' ||  v_index_name || ' rename to cms_item_template_map_pk';
  end if;
end;
/
show errors;

-- From varchar(1) to char(1)
alter table cms_text_mime_types modify (is_inso char(1));

-- From varchar(4000) to varchar(3209) due to index length limitations
alter table cms_items modify (
    ancestors varchar2(3209)
);

declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_indexes uc
   where lower(index_name) = 'cms_items_ancestors_idx';

  if (v_exists = '0') then
    execute immediate 'create index cms_items_ancestors_idx on cms_items(ancestors)';
  end if;
end;
/
show errors;

-- Comments
comment on table cms_image_mime_types is '';
comment on column cms_image_mime_types.sizer_class is '';


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
       decode (is_homepage, 0, '0', '1')
  from ct_news;

alter table ct_news drop column is_homepage;
alter table ct_news add (
  is_homepage CHAR(1)
);

update ct_news
   set is_homepage = ( select temp_ct_news.is_homepage
                         from temp_ct_news
                        where temp_ct_news.item_id = ct_news.item_id);

alter table ct_news modify (
  is_homepage not null
);

commit;

drop table temp_ct_news;

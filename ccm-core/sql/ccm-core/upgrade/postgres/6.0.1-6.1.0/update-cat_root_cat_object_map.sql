--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: update-cat_root_cat_object_map.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table cat_root_cat_object_map_temp (
    id NUMERIC not null
        constraint temp_cat_1
          primary key,
    category_id INTEGER not null,
    object_id INTEGER not null,
    use_context VARCHAR(700),
    constraint temp_cat_2
      unique(object_id, use_context)
);

insert into cat_root_cat_object_map_temp
  (id, category_id, object_id, use_context)
select nextval('acs_object_id_seq'), category_id, object_id, null
  from cat_root_cat_object_map;

drop table cat_root_cat_object_map;
alter table cat_root_cat_object_map_temp rename to cat_root_cat_object_map;

alter table cat_root_cat_object_map drop constraint temp_cat_1;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_id_p_qw9kr
      primary key (id);

alter table cat_root_cat_object_map drop constraint temp_cat_2;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_ob_u_gqgrh
      unique (object_id, use_context);

alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ca_f_jqvmd foreign key (category_id)
      references cat_categories(category_id);
alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ob_f_anfmx foreign key (object_id)
      references acs_objects(object_id);

create index cat_roo_cat_obj_map_cat_id_idx on cat_root_cat_object_map(category_id);

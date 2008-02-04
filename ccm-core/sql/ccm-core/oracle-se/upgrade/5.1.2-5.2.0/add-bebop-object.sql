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
-- $Id: add-bebop-object.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table bebop_object_type (
    type_id integer
        constraint bebop_object_type_type_id_fk references
        acs_objects (object_id) on delete cascade
        constraint bebop_object_type_type_id_pk primary key,
    app_type varchar(20)
        constraint bebop_object_type_app_nn not null,
    class_name varchar(120)
        constraint bebop_object_type_class_nn not null,
    constraint bebop_object_type_un unique(app_type, class_name)
);

comment on table bebop_object_type is '
  This table defines the base object types whose subclasses we
  need to store meta data on.
';
comment on column bebop_object_type.type_id is '
  The unique indentifier for the object base type. This does
  not need to be a sequence since the table is statically
  populated at install time.
';
comment on column bebop_object_type.app_type is '
  The type of application using the object type.
';
comment on column bebop_object_type.class_name is '
  The fully qualified java class name of the base type.
';

create table bebop_meta_object (
    object_id integer
        constraint bebop_meta_obj_object_id_fk references
        acs_objects on delete cascade
        constraint bebop_meta_obj_object_id_pk primary key,
    type_id integer
        constraint bebop_meta_object_type_id_nn not null
        constraint bebop_meta_object_type_id_fk references
        bebop_object_type on delete cascade,
    pretty_name varchar(50),
    pretty_plural varchar(50),
    class_name varchar(200),
    props_form varchar(200),
    constraint bebop_meta_obj_un unique (type_id, class_name)
);

comment on table bebop_meta_object is '
  This table maintains a registry of all meta data for 
  various persistent classes, such as process listeners
  and persistent widgets.
';
comment on column bebop_meta_object.object_id is '
  The unique object identifier for the widget type.
';
comment on column bebop_meta_object.type_id is '
  The id of the base object type.
';
comment on column bebop_meta_object.pretty_name is '
  The user facing ''pretty'' name for the widget type
';
comment on column bebop_meta_object.pretty_plural is '
  The plural equivalent of the pretty_name attribute
';
comment on column bebop_meta_object.class_name is '
  The fully qualified java class name of the widget. The class
  should inherit from com.arsdigita.formbuilder.PersistentWidget
';
comment on column bebop_meta_object.props_form is '
  The fully qualfied java class name of a form section for
  editing the properties of a widget. The class should be a
  subclass of com.arsdigita.bebop.Bebopection.
';

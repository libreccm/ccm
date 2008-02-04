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
-- $Id: comment-bebop_meta_object.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

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

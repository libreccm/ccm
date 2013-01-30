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
-- $Id: add-forms.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table forms_widget_label (
    label_id integer
        constraint forms_wgt_label_label_id_fk
        references bebop_widgets (widget_id) on delete cascade
        constraint forms_wgt_label_label_id_pk primary key,
    widget_id integer
        constraint forms_wgt_label_widget_id_fk
        references bebop_widgets (widget_id) on delete cascade
);

comment on table forms_widget_label is '
  This table maintains an association between a label and
  the widget it is labelling
';
comment on column forms_widget_label.label_id is '
  The unique id of the label.
';
comment on column forms_widget_label.widget_id is '
  The id of the widget being labelled.
';

create table forms_lstnr_conf_email (
    listener_id integer
        constraint forms_lstnr_conf_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_conf_email_pk primary key,
    sender varchar(120),
    subject varchar(120),
    -- XXX may need to make this a blob
    body varchar(4000)
);

comment on table forms_lstnr_conf_email is '
  Stores details of the confirmation email to be sent
  upon submission.
';
comment on column forms_lstnr_conf_email.sender is '
  Email address of sender for confirmation email
';
comment on column forms_lstnr_conf_email.subject is '
  The subject line of the mail
';
comment on column forms_lstnr_conf_email.body is '
  The text of the email optionally containing
  placeholders of the form "::foo.bar::"
';

create table forms_lstnr_conf_redirect (
    listener_id integer
        constraint forms_lstnr_conf_redirect_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_conf_redirect_pk primary key,
    url varchar(160)
);

comment on table forms_lstnr_conf_redirect is '
  Details of the URL redirect upon form submission
';

create table forms_lstnr_simple_email (
    listener_id integer
        constraint forms_lstnr_simple_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_simple_email_pk primary key,
    recipient varchar(120),
    subject varchar(120)
);

comment on table forms_lstnr_simple_email is '
  Details of the email containing the submitted 
  form values.
';

create table forms_lstnr_tmpl_email (
    listener_id integer
        constraint forms_lstnr_tmpl_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_tmpl_email_pk primary key,
    recipient varchar(120),
    subject varchar(120),
    -- XXX may need to make this a blob
    body varchar(4000)
);

comment on table forms_lstnr_tmpl_email is '
  Details of the templated email containing submitted
  form values
';

create table forms_lstnr_xml_email (
    listener_id integer
        constraint forms_lstnr_xml_email_fk references
        bebop_process_listeners on delete cascade
        constraint forms_lstnr_xml_email_pk primary key,
    recipient varchar(120),
    subject varchar(120)
);

comment on table forms_lstnr_xml_email is '
  Details of the XML email containing submitted
  forms values
';

create table forms_dataquery (
    query_id integer 
        constraint forms_dq_query_id_pk primary key
        constraint forms_dq_query_id_fk 
        references acs_objects (object_id) on delete cascade,
    type_id integer
        constraint forms_dq_query_type_id_fk references
        bebop_object_type on delete cascade,
    description varchar(200),
    name varchar(60),
    constraint forms_dataquery_un unique (type_id, name)
);

comment on table forms_dataquery is '
  Details of canned data queries available for populating
  a database backed select box.
';

create table forms_dd_select (
    widget_id integer 
        constraint forms_dds_widget_id_pk primary key
        constraint forms_dds_widget_id_fk 
        references bebop_widgets (widget_id) on delete cascade,
    multiple_p char(1) check (multiple_p in ('1', '0')),
    query_id integer
        constraint forms_dds_query_id_fk
        references forms_dataquery (query_id) on delete cascade
);

comment on table forms_dd_select is '
  Select widget populated using canned database queries.
';

create sequence forms_unique_id_seq;

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
-- $Id: update-applications.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

-- In 6.0.1, the 'admin' and 'sitemap' package types already exist,
-- but they aren't application types.  Let's add the two missing rows
-- to the application_types table.

insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  nextval('acs_object_id_seq'), 'com.arsdigita.ui.admin.Admin',
  'CCM Admin Application', 'CCM user and group administration',
  't', 't', 'f', 'f', package_type_id
from apm_package_types
where package_key = 'admin';


insert into application_types
  (application_type_id, object_type, title, description,
   workspace_application_p, has_full_page_view_p, has_embedded_view_p,
   singleton_p, package_type_id)
select
  nextval('acs_object_id_seq'), 'com.arsdigita.ui.sitemap.SiteMap',
  'SiteMap Admin Application', 'CCM sitemap administration',
  't', 't', 'f', 'f', package_type_id
from apm_package_types
where package_key = 'sitemap';

-- Now that we have converted two package types into application
-- types, let's convert two existing package instances into
-- applications.

insert into acs_objects
  (object_id, object_type, display_name, default_domain_class)
values
  (nextval('acs_object_id_seq'), 'com.arsdigita.ui.admin.Admin',
   'CCM Admin', 'com.arsdigita.ui.admin.Admin');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  currval('acs_object_id_seq'),
  ap.pretty_name,
  at.application_type_id,
  currentDate(),
  sn.url,
  ap.package_id
from
  apm_packages ap,
  apm_package_types apt,
  application_types at,
  site_nodes sn
where
  ap.package_type_id = at.package_type_id
  and sn.object_id = ap.package_id
  and ap.package_type_id = apt.package_type_id
  and apt.package_key = 'admin';


insert into acs_objects
  (object_id, object_type, display_name, default_domain_class)
values
  (nextval('acs_object_id_seq'), 'com.arsdigita.ui.sitemap.SiteMap',
   'CCM Admin Sitemap', 'com.arsdigita.ui.sitemap.SiteMap');

insert into applications
  (application_id, title, application_type_id, timestamp,
   primary_url, package_id)
select
  currval('acs_object_id_seq'),
  ap.pretty_name,
  at.application_type_id,
  currentDate(),
  sn.url,
  ap.package_id
from
  apm_packages ap,
  apm_package_types apt,
  application_types at,
  site_nodes sn
where
  ap.package_type_id = at.package_type_id
  and sn.object_id = ap.package_id
  and ap.package_type_id = apt.package_type_id
  and apt.package_key = 'sitemap';

insert into admin_app
select application_id
  from applications apps, application_types app_types
 where apps.application_type_id = app_types.application_type_id
   and app_types.object_type = 'com.arsdigita.ui.admin.Admin'
   and application_id not in (select application_id
                                from admin_app);
insert into sitemap_app
select application_id
  from applications apps, application_types app_types
 where apps.application_type_id = app_types.application_type_id
   and app_types.object_type = 'com.arsdigita.ui.sitemap.SiteMap'
   and application_id not in (select application_id
                                from sitemap_app);

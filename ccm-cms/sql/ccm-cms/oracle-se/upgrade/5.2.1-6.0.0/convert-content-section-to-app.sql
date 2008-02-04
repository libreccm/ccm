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
-- $Id: convert-content-section-to-app.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- don't complain about constraints until we're done

set constraints all deferred;

-----------------------------------
-- 1. drop acs_objects reference constraint on content_sections.section_id
-----------------------------------
alter table content_sections
drop constraint csections_section_id_fk;

-----------------------------------
-- 2. Create content-section Application Type
-----------------------------------
insert into application_types
(application_type_id, description, has_embedded_view_p,
 has_full_page_view_p, object_type,
 package_type_id,
 singleton_p, title, workspace_application_p)
values
(ACS_OBJECT_ID_SEQ.nextval, 'A CMS Content Section', 0,
 1, 'com.arsdigita.cms.ContentSection',
 (select package_type_id from apm_package_types where package_key = 'content-section'),
 0, 'CMS Content Section', 1);

-----------------------------------
-- 3. Create content-section application instances for each
--    content-section package instance.
-----------------------------------

declare
   v_app_id      integer;
   v_app_type_id integer;
   v_url         varchar2(4000);

   cursor c1 is
   select p.package_id, p.pretty_name
     from apm_packages p, apm_package_types t
    where p.package_type_id = t.package_type_id
      and t.package_key = 'content-section';
BEGIN
   -- get the app type
   select application_type_id into v_app_type_id
     from application_types
    where package_type_id =
          (select package_type_id
             from apm_package_types
            where package_key = 'content-section');

   FOR p IN c1 LOOP
      -- get the content section id
      select section_id into v_app_id
        from content_sections
       where content_sections.package_id = p.package_id;

      -- get the url
      select url into v_url
      from site_nodes
      where object_id = p.package_id;

      -- create the app instance for the content section
      insert into applications
      (application_id, application_type_id, package_id,
       primary_url, timestamp, title)
      values
      (v_app_id, v_app_type_id, p.package_id,
       v_url, sysdate, p.pretty_name);
   END LOOP;

   EXCEPTION
     WHEN NO_DATA_FOUND THEN NULL;
END;
/
show errors;

-----------------------------------
-- 4. Drop package_id column from content_sections
-----------------------------------
drop index content_sections_package_idx;

alter table content_sections
drop column package_id;

-----------------------------------
-- 5. Add constraint to section_id to reference applications
-----------------------------------
alter table content_sections add
    constraint csections_section_id_fk foreign key (section_id)
      references applications(application_id) on delete cascade;

-- enable constraints again
set constraints all immediate;

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
-- alter table content_sections
-- drop constraint csections_section_id_fk restrict;

-----------------------------------
-- 2. Create content-section Application Type
-----------------------------------
insert into application_types
(application_type_id, description, has_embedded_view_p,
 has_full_page_view_p, object_type,
 package_type_id,
 singleton_p, title, workspace_application_p)
values
(nextval('ACS_OBJECT_ID_SEQ'), 'A CMS Content Section', FALSE,
 TRUE, 'com.arsdigita.cms.ContentSection',
 (select package_type_id from apm_package_types where package_key = 'content-section'),
 FALSE, 'CMS Content Section', TRUE);

-----------------------------------
-- 3. Create content-section application instances for each
--    content-section package instance.
-----------------------------------

--procedure for creating application instances from package_instances
create or replace function convert_content_sec_pkg() 
RETURNS INTEGER AS '
DECLARE
     v_app_id      integer;
     v_app_type_id integer;
     v_url	   varchar(4000);
     p	           record;
BEGIN
     -- get the app type
     select application_type_id into v_app_type_id
     from application_types
     where package_type_id =
      (select package_type_id
       from apm_package_types
       where package_key = ''content-section'');

     FOR p IN select pk.package_id, pk.pretty_name
     	      from apm_packages pk, apm_package_types t
     	      where pk.package_type_id = t.package_type_id
     	      and t.package_key = ''content-section'' 
     	LOOP
        -- get the content section id
        select section_id into v_app_id
        from content_sections
        where package_id = p.package_id;

	-- get the url
	select url into v_url
	from site_nodes
        where object_id = p.package_id;

        -- create the app instance for the content section
        insert into applications
        (application_id, application_type_id, package_id,
         primary_url,
         timestamp, title)
        values
        (v_app_id, v_app_type_id, p.package_id,
	 v_url,
         current_timestamp, p.pretty_name);
     END LOOP;
     RETURN 1;
END;
' LANGUAGE 'plpgsql';

-- run the procedure
select convert_content_sec_pkg();

--drop the procedure now that we're done with it
drop function convert_content_sec_pkg();

-----------------------------------
-- 4. Drop package_id column from content_sections
-----------------------------------
drop index content_sections_package_idx;

alter table content_sections
drop column package_id restrict;

-----------------------------------
-- 5. Add constraint to section_id to reference applications
-----------------------------------
alter table content_sections
add constraint csections_section_id_fk foreign key (section_id)
references applications(application_id);

-- enable constraints again
set constraints all immediate;

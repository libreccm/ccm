--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $Id: upd_system_tables.sql pboy $

-- adjust various system tables to the new name of application

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;
alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name='com.arsdigita.themedirector.Initializer'
 where class_name='com.arsdigita.london.theme.Initializer' ;

update init_requirements
   set init='com.arsdigita.themedirector.Initializer'
 where init='com.arsdigita.london.theme.Initializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);


update application_types
   set object_type='com.arsdigita.themedirector.ThemeDirector',
             title='CCM Themes Administration',
         description='CCM themes administration'
 where   object_type='com.arsdigita.london.theme.ThemeApplication' ;

update applications
   set       title='CCM Themes Administration',
         description='CCM themes administration'
 where   primary_url='/admin/themes/' ;

-- table apm_package_types doesn't require an update
-- table apm_packages doesn't require an update either
-- table site_nodes doesn't require an update either


-- update application type in acs_objects
update acs_objects
    set (object_type,display_name,default_domain_class) =
            ('com.arsdigita.themedirector.ThemeDirector' ,
             'CCM Themes Administration',
             'com.arsdigita.themedirector.ThemeDirector' )
    where default_domain_class like 'com.arsdigita.london.theme.ThemeApplication' ;

-- update themes in acs_objects
update acs_objects
    set display_name=replace(display_name,'london.theme', 'themedirector')
    where default_domain_class like 'com.arsdigita.london.theme.Theme' ;

update acs_objects
    set (object_type,default_domain_class) =
            ('com.arsdigita.themedirector.Theme' ,
             'com.arsdigita.themedirector.Theme' )
    where default_domain_class like 'com.arsdigita.london.theme.Theme' ;

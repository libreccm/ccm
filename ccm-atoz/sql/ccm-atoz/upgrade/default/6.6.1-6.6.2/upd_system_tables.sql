--
-- Copyright (C) 2012 Peter Boy All Rights Reserved.
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

-- rename cm-ldn-atoz to ccm-atoz
-- adjust various system tables to the new name of application

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;
alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name='com.arsdigita.atoz.Initializer'
 where class_name='com.arsdigita.london.atoz.Initializer' ;

update init_requirements
   set init='com.arsdigita.atoz.Initializer'
 where init='com.arsdigita.london.atoz.Initializer' ;

update init_requirements
   set required_init='com.arsdigita.atoz.Initializer'
 where required_init='com.arsdigita.london.atoz.Initializer' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);


update application_types
   set object_type = replace(object_type,'london.atoz.AtoZ','atoz.AtoZ')
 where object_type like '%london.atoz.AtoZ%' ;

-- table applications doesn't require an update
-- update applications
--    set       title='CCM Themes Administration',
--          description='CCM themes administration'
--  where   primary_url='/admin/themes/' ;

-- table apm_package_types doesn't require an update
-- table apm_packages doesn't require an update either
-- table site_nodes doesn't require an update either


-- update application type in acs_objects
-- update acs_objects
--     set (object_type,display_name,default_domain_class) =
--             ('com.arsdigita.themedirector.ThemeDirector' ,
--              'CCM Themes Administration',
--              'com.arsdigita.themedirector.ThemeDirector' )
--     where default_domain_class like 'com.arsdigita.london.theme.ThemeApplication' ;

-- update atoz in acs_objects
-- replace String london.atoz by atoz for all atoz.AtoZ*
update acs_objects
   set object_type = replace(object_type,'london.atoz', 'atoz'),
       default_domain_class = replace(default_domain_class,'london.atoz', 'atoz')
 where object_type like '%london.atoz.AtoZ%' ;

-- rename AtoZItemProvider to ItemProvider
update acs_objects
   set object_type = replace(object_type,'AtoZItemProvider', 'ItemProvider'),
       default_domain_class = replace(default_domain_class,'AtoZItemProvider', 'ItemProvider')
 where object_type like '%AtoZItemProvider%' ;

-- rename AtoZCategoryProvider to CategoryProvider
update acs_objects
   set object_type = replace(object_type,'AtoZCategoryProvider', 'CategoryProvider'),
       default_domain_class = replace(default_domain_class,'AtoZCategoryProvider', 'CategoryProvider')
 where object_type like '%AtoZCategoryProvider%' ;

-- rename AtoZSiteProxyProvider to siteproxy.SiteProxyProvider
update acs_objects
   set object_type = replace(object_type,'AtoZSiteProxyProvider', 'siteproxy.SiteProxyProvider'),
       default_domain_class = replace(default_domain_class,'AtoZSiteProxyProvider', 'siteproxy.SiteProxyProvider')
 where object_type like '%AtoZSiteProxyProvider%' ;

-- rename terms.DomainProvider to DomainProvider
update acs_objects
   set object_type = replace(object_type,'terms.DomainProvider', 'DomainProvider'),
       default_domain_class = replace(default_domain_class,'terms.DomainProvider', 'DomainProvider')
 where object_type like '%atoz.terms.DomainProvider%' ;


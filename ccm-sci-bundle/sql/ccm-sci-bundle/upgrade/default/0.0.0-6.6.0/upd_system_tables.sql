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

-- adjust various system tables to the new name of application (ccm-sci-bundle)

alter table init_requirements drop constraint init_requirements_init_f_cmmdn ;

alter table init_requirements drop constraint init_require_requ_init_f_i6rgg ;

update inits
   set class_name=replace(class_name,'arsdigita.aplaws', 'arsdigita.bundle')
 where class_name like '%arsdigita.aplaws%' ;

update init_requirements
   set init=replace(init,'arsdigita.aplaws', 'arsdigita.bundle')
 where init  like  '%arsdigita.aplaws%' ;

update init_requirements
   set required_init=replace(required_init,'arsdigita.aplaws', 'arsdigita.bundle')
 where required_init  like  '%arsdigita.aplaws%' ;

ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);

ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);

-- update application type in acs_objects
update acs_objects
    set (object_type,default_domain_class) =
            (replace(object_type,'arsdigita.aplaws', 'arsdigita.bundle') ,
             replace(default_domain_class,'arsdigita.aplaws', 'arsdigita.bundle') )
    where object_type like '%arsdigita.aplaws%' ;

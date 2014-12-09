--
-- Copyright (C) 2013 Peter Boy. All Rights Reserved.
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
-- $Id: upd_inits.sql  $

-- Drop constraints for init_requirements temporaly (otherwise we can't update 
-- the tables)
ALTER TABLE init_requirements DROP CONSTRAINT init_requirements_init_f_cmmdn ;
ALTER TABLE init_requirements DROP CONSTRAINT init_require_requ_init_f_i6rgg ;

-- Adjust the class name of the Initializer
UPDATE inits
   SET class_name='com.arsdigita.cms.contenttypes.DecisionTreeInitializer'
 WHERE class_name='com.arsdigita.camden.cms.contenttypes.DecisionTreeInitializer';

-- Adjust the class name of the Initializer in init-requirements
UPDATE init_requirements
   SET init='com.arsdigita.cms.contenttypes.DecisionTreeInitializer'
 WHERE init='com.arsdigita.camden.cms.contenttypes.DecisionTreeInitializer';

-- Restore the constraints for init_requirements
ALTER TABLE init_requirements
  ADD CONSTRAINT init_requirements_init_f_cmmdn FOREIGN KEY (init)
      REFERENCES inits (class_name);
ALTER TABLE init_requirements
  ADD CONSTRAINT init_require_requ_init_f_i6rgg FOREIGN KEY (required_init)
      REFERENCES inits (class_name);

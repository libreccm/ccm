--
-- Copyright (C) 2012 Peter Boy. All Rights Reserved.
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
-- $Id: adjust_app_properties.sql  $

-- ccm-subsite is now initialized as a legacy free type of application.
-- Application properties as title have to be adjusted in order to make
-- XSL style sheets locatable.


-- Rename title from CCM Subsite Admin to just Subsite
update application_types   
   set title='Subsite'  
 where title like 'CMS Subsite Admin';

-- Adjust description
update application_types   
   set description='The CCM subsite administration application.'  
 where title like 'Subsite';
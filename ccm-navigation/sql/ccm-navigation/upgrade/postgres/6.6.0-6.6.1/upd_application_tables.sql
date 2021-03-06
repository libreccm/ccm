--
-- Copyright (C) 2010 Peter Boy. All Rights Reserved.
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
-- $Id: upd_application_tables.sql  $

-- NavigationTreePortlet has been moved from ccm-ldn-portal (where it existed as
-- NavigationDirectoryPortlet) to ccm-ldn-navigation because it depends on
-- navigation and was misplaced there (generating horizontal dependencies).
-- At the time of this update both ccm-ldn-portal and ccm-ldn-navigation must
-- be installed because of those horizontal dependencies. So existence of
-- ccm-ldn-navigatgion can be taken granted for now.


update application_types
    set (object_type,title,description) =
            ('com.arsdigita.london.navigation.portlet.NavigationTreePortlet',
             'Navigation Tree',
             'Displays a tree of navigation categories' )
    where object_type
        like 'com.arsdigita.london.portal.portlet.NavigationDirectoryPortlet' ;

update applications
    set (title,description) =
            ('Navigation Tree',
             'Displays a tree of navigation categories' )
    where title
        like 'Navigation Directory' ;


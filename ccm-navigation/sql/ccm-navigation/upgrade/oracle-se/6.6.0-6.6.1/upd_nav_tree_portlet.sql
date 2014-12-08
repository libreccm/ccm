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
-- $Id: upd_nav_tree_portlet.sql  $

-- NavigationTreePortlet has been moved from ccm-ldn-portal (where it existed as
-- NavigationDirectoryPortlet) to ccm-ldn-navigation because it depends on
-- navigation and was misplaced there (generating horizontal dependencies).


alter table portlet_navigation_directory
    drop constraint port_navig_dire_por_id_p_n4lfs;

alter table portlet_navigation_directory
    drop constraint port_navi_dir_navig_id_f_rq14k;

alter table portlet_navigation_directory
    drop constraint port_navig_dire_por_id_f_vsyyc;


alter table portlet_navigation_directory
    rename to portlet_navigation_tree;


alter table portlet_navigation_tree
    add constraint port_naviga_tre_por_id_p_ivbko PRIMARY KEY (portlet_id);

alter table portlet_navigation_tree
    add constraint port_navi_tre_navig_id_f_b30fa FOREIGN KEY (navigation_id)
    REFERENCES nav_app(application_id);

alter table portlet_navigation_tree
    add constraint port_naviga_tre_por_id_f_wgp6z FOREIGN KEY (portlet_id)
    REFERENCES portlets(portlet_id);

update application_types
    set object_type = 'com.arsdigita.navigation.portlet.NavigationTreePortlet'
    where object_type
    like 'com.arsdigita.portalworkspace.portlet.NavigationDirectoryPortlet' ;

update application_types
    set title = 'Navigation Tree'
    where object_type
    like 'com.arsdigita.navigation.portlet.NavigationTreePortlet' ;

update application_types
    set description = 'Displays a tree of navigation categories'
    where object_type
    like 'com.arsdigita.navigation.portlet.NavigationTreePortlet' ;
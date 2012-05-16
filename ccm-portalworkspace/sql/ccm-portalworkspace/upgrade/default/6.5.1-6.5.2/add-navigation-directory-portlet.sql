-- Copyright (C) 2008 Permeance Technologies Pty Ltd. All Rights Reserved.
-- 
-- This library is free software; you can redistribute it and/or modify it under
-- the terms of the GNU Lesser General Public License as published by the Free
-- Software Foundation; either version 2.1 of the License, or (at your option)
-- any later version.
-- 
-- This library is distributed in the hope that it will be useful, but WITHOUT
-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
-- details.
-- 
-- You should have received a copy of the GNU Lesser General Public License
-- along with this library; if not, write to the Free Software Foundation, Inc.,
-- 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

create table portlet_navigation_directory (
    portlet_id INTEGER not null
        constraint port_navig_dire_por_id_p_n4lfs
          primary key,
    navigation_id INTEGER not null,
    depth INTEGER not null
);

alter table portlet_navigation_directory add 
    constraint port_navi_dir_navig_id_f_rq14k foreign key (navigation_id)
      references nav_app(application_id);
      
alter table portlet_navigation_directory add 
    constraint port_navig_dire_por_id_f_vsyyc foreign key (portlet_id)
      references portlets(portlet_id);    
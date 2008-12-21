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

create table portlet_flash (
    portlet_id INTEGER not null
        constraint portle_flas_portlet_id_p_d00yp
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    background_colour VARCHAR(7),
    detect_key VARCHAR(32),
    swf_file VARCHAR(2048) not null,
    height VARCHAR(8) not null,
    parameters VARCHAR(1024),
    quality VARCHAR(8),
    redirect_url VARCHAR(2048),
    variables VARCHAR(1024),
    version VARCHAR(8) not null,
    width VARCHAR(8) not null,
    xi_redirect_url VARCHAR(2048)
);

alter table portlet_flash add 
    constraint portle_flas_portlet_id_f_bhmp4 foreign key (portlet_id)
      references portlets(portlet_id);
   
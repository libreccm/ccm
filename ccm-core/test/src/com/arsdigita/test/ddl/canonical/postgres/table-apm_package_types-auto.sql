--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: table-apm_package_types-auto.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
create table apm_package_types (
    package_type_id INTEGER not null
        constraint apm_pac_typ_pac_typ_id_p_q7ayv
          primary key,
    dispatcher_class VARCHAR(100),
    package_key VARCHAR(100) not null
        constraint apm_pack_typ_packa_key_u_xjbf1
          unique,
    package_uri VARCHAR(1500) not null
        constraint apm_pack_typ_packa_uri_u_ish63
          unique,
    pretty_name VARCHAR(100) not null
        constraint apm_pack_typ_pret_name_u_8xzvk
          unique,
    pretty_plural VARCHAR(100)
        constraint apm_pac_typ_pre_plural_u_kqgl6
          unique,
    servlet_package VARCHAR(100)
);

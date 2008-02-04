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
-- $Id: add-applications.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create table applications (
    application_id INTEGER not null
        constraint applicati_applicati_id_p_ogstm
          primary key,
        -- referential constraint for application_id deferred due to circular dependencies
    application_type_id INTEGER not null,
        -- referential constraint for application_type_id deferred due to circular dependencies
    cell_number INTEGER,
    description VARCHAR(4000),
    package_id INTEGER,
        -- referential constraint for package_id deferred due to circular dependencies
    parent_application_id INTEGER,
        -- referential constraint for parent_application_id deferred due to circular dependencies
    primary_url VARCHAR(4000),
    sort_key INTEGER,
    timestamp DATE not null,
    title VARCHAR(200)
);

create table application_types (
    application_type_id INTEGER not null
        constraint appli_typ_appli_typ_id_p_r5e8o
          primary key,
    description VARCHAR(4000),
    has_embedded_view_p CHAR(1),
    has_full_page_view_p CHAR(1),
    object_type VARCHAR(100) not null
        constraint applicat_typ_obje_type_u_pf2uk
          unique,
    package_type_id INTEGER,
        -- referential constraint for package_type_id deferred due to circular dependencies
    profile VARCHAR(20),
    provider_id INTEGER,
        -- referential constraint for provider_id deferred due to circular dependencies
    singleton_p CHAR(1),
    title VARCHAR(200),
    workspace_application_p CHAR(1)
);

create table application_type_privilege_map (
    privilege VARCHAR(100) not null,
        -- referential constraint for privilege deferred due to circular dependencies
    application_type_id INTEGER not null,
        -- referential constraint for application_type_id deferred due to circular dependencies
    constraint appl_typ_pri_map_app_t_p_dc1jg
      primary key(application_type_id, privilege)
);

alter table application_type_privilege_map add 
    constraint appl_typ_pri_map_app_t_f_kgrfj foreign key (application_type_id)
      references application_types(application_type_id) on delete cascade;
alter table application_type_privilege_map add 
    constraint appl_typ_pri_map_privi_f_s3pwb foreign key (privilege)
      references acs_privileges(privilege) on delete cascade;
alter table application_types add 
    constraint applica_typ_pac_typ_id_f_v80ma foreign key (package_type_id)
      references apm_package_types(package_type_id);
alter table application_types add 
    constraint applicat_typ_provid_id_f_bm274 foreign key (provider_id)
      references application_types(application_type_id);
alter table applications add 
    constraint applica_applica_typ_id_f_k2bi3 foreign key (application_type_id)
      references application_types(application_type_id);
alter table applications add 
    constraint applica_par_applica_id_f_hvxh7 foreign key (parent_application_id)
      references applications(application_id);
alter table applications add 
    constraint applicati_applicati_id_f_a35g2 foreign key (application_id)
      references acs_objects(object_id) on delete cascade;
alter table applications add 
    constraint application_package_id_f_cdaho foreign key (package_id)
      references apm_packages(package_id);

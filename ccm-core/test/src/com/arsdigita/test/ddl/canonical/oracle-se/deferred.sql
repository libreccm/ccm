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
-- $Id: deferred.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
alter table acs_permissions add 
    constraint acs_permiss_creat_user_f_hiyn9 foreign key (creation_user)
      references users(user_id);
alter table acs_permissions add 
    constraint acs_permissi_privilege_f_p76ev foreign key (privilege)
      references acs_privileges(privilege) on delete cascade;
alter table acs_permissions add 
    constraint acs_permissio_grant_id_f_vmo0e foreign key (grantee_id)
      references parties(party_id) on delete cascade;
alter table acs_permissions add 
    constraint acs_permissio_objec_id_f_5swtm foreign key (object_id)
      references acs_objects(object_id) on delete cascade;
alter table acs_stylesheet_node_map add 
    constraint acs_sty_nod_map_nod_id_f_q55q3 foreign key (node_id)
      references site_nodes(node_id) on delete cascade;
alter table acs_stylesheet_node_map add 
    constraint acs_sty_nod_map_sty_id_f_guej5 foreign key (stylesheet_id)
      references acs_stylesheets(stylesheet_id) on delete cascade;
alter table acs_stylesheet_type_map add 
    constraint acs_sty_typ_map_pac_ty_f_emkua foreign key (package_type_id)
      references apm_package_types(package_type_id) on delete cascade;
alter table acs_stylesheet_type_map add 
    constraint acs_sty_typ_map_sty_id_f_38x8p foreign key (stylesheet_id)
      references acs_stylesheets(stylesheet_id) on delete cascade;
alter table acs_stylesheets add 
    constraint acs_stylesh_stylesh_id_f_2fiok foreign key (stylesheet_id)
      references acs_objects(object_id) on delete cascade;
alter table acs_stylesheets add 
    constraint acs_styleshee_local_id_f_wjfrg foreign key (locale_id)
      references g11n_locales(locale_id);
alter table apm_package_type_listener_map add 
    constraint apm_pac_typ_lis_map_li_f_i78gw foreign key (listener_id)
      references apm_listeners(listener_id) on delete cascade;
alter table apm_package_type_listener_map add 
    constraint apm_pac_typ_lis_map_pa_f_0_qfw foreign key (package_type_id)
      references apm_package_types(package_type_id) on delete cascade;
alter table apm_packages add 
    constraint apm_packa_packa_typ_id_f_adr4w foreign key (package_type_id)
      references apm_package_types(package_type_id) on delete cascade;
alter table apm_packages add 
    constraint apm_package_package_id_f_46may foreign key (package_id)
      references acs_objects(object_id) on delete cascade;
alter table apm_packages add 
    constraint apm_packages_locale_id_f_qlps4 foreign key (locale_id)
      references g11n_locales(locale_id);
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
alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ca_f_jqvmd foreign key (category_id)
      references cat_categories(category_id) on delete cascade;
alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ob_f_anfmx foreign key (object_id)
      references acs_objects(object_id) on delete cascade;
alter table group_member_map add 
    constraint grou_memb_map_membe_id_f_bs3u_ foreign key (member_id)
      references users(user_id) on delete cascade;
alter table group_member_map add 
    constraint grou_membe_map_grou_id_f_d7lhm foreign key (group_id)
      references groups(group_id) on delete cascade;
alter table group_subgroup_map add 
    constraint grou_subg_map_subgr_id_f_1jo4e foreign key (subgroup_id)
      references groups(group_id) on delete cascade;
alter table group_subgroup_map add 
    constraint grou_subgro_map_gro_id_f_todnr foreign key (group_id)
      references groups(group_id) on delete cascade;
alter table groups add 
    constraint groups_group_id_f_l4tvr foreign key (group_id)
      references parties(party_id) on delete cascade;
alter table object_container_map add 
    constraint obje_cont_map_conta_id_f_v66b1 foreign key (container_id)
      references acs_objects(object_id) on delete cascade;
alter table object_container_map add 
    constraint obje_contai_map_obj_id_f_guads foreign key (object_id)
      references acs_objects(object_id) on delete cascade;
alter table object_context add 
    constraint objec_contex_contex_id_f_crdh1 foreign key (context_id)
      references acs_objects(object_id) on delete cascade;
alter table object_context add 
    constraint objec_contex_object_id_f_mbuxe foreign key (object_id)
      references acs_objects(object_id) on delete cascade;
alter table parameterized_privileges add 
    constraint para_pri_bas_privilege_f_elb6t foreign key (base_privilege)
      references acs_privileges(privilege);
alter table parties add 
    constraint parties_party_id_f_j4k1i foreign key (party_id)
      references acs_objects(object_id) on delete cascade;
alter table party_email_map add 
    constraint part_emai_map_party_id_f_7_00_ foreign key (party_id)
      references parties(party_id) on delete cascade;
alter table portals add 
    constraint portals_portal_id_f_kbx1t foreign key (portal_id)
      references applications(application_id) on delete cascade;
alter table portlets add 
    constraint portlets_portal_id_f_bombq foreign key (portal_id)
      references portals(portal_id);
alter table portlets add 
    constraint portlets_portlet_id_f_erf4o foreign key (portlet_id)
      references applications(application_id) on delete cascade;
alter table roles add 
    constraint role_implicit_group_id_f_o6g0p foreign key (implicit_group_id)
      references groups(group_id) on delete cascade;
alter table roles add 
    constraint roles_group_id_f_doyeu foreign key (group_id)
      references groups(group_id);
alter table search_test_author add 
    constraint sear_tes_auth_autho_id_f_klil2 foreign key (author_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book add 
    constraint searc_tes_book_book_id_f_eqgc0 foreign key (book_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book_chapter add 
    constraint sear_tes_boo_cha_cha_i_f_fonpi foreign key (chapter_id)
      references acs_objects(object_id) on delete cascade;
alter table site_nodes add 
    constraint site_nodes_node_id_f_n1m2y foreign key (node_id)
      references acs_objects(object_id) on delete cascade;
alter table site_nodes add 
    constraint site_nodes_object_id_f_ked74 foreign key (object_id)
      references apm_packages(package_id);
alter table site_nodes add 
    constraint site_nodes_parent_id_f_sacav foreign key (parent_id)
      references site_nodes(node_id) on delete cascade;
alter table user_authentication add 
    constraint user_authentica_aut_id_f_0bgpj foreign key (auth_id)
      references parties(party_id) on delete cascade;
alter table user_authentication add 
    constraint user_authentica_use_id_f_z1jvj foreign key (user_id)
      references users(user_id);
alter table users add 
    constraint users_name_id_f_0xbbm foreign key (name_id)
      references person_names(name_id);
alter table users add 
    constraint users_user_id_f_t_lso foreign key (user_id)
      references parties(party_id) on delete cascade;
alter table vc_blob_operations add 
    constraint vc_blo_operat_opera_id_f_qcpj1 foreign key (operation_id)
      references vc_operations(operation_id) on delete cascade;
alter table vc_clob_operations add 
    constraint vc_clo_operat_opera_id_f_k752z foreign key (operation_id)
      references vc_operations(operation_id) on delete cascade;

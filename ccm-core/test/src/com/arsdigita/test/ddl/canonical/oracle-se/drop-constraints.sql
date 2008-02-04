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
-- $Id: drop-constraints.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $
alter table acs_permissions
 drop constraint acs_permiss_creat_user_f_hiyn9;
alter table acs_permissions
 drop constraint acs_permissi_privilege_f_p76ev;
alter table acs_permissions
 drop constraint acs_permissio_grant_id_f_vmo0e;
alter table acs_permissions
 drop constraint acs_permissio_objec_id_f_5swtm;
alter table acs_stylesheet_node_map
 drop constraint acs_sty_nod_map_nod_id_f_q55q3;
alter table acs_stylesheet_node_map
 drop constraint acs_sty_nod_map_sty_id_f_guej5;
alter table acs_stylesheet_type_map
 drop constraint acs_sty_typ_map_pac_ty_f_emkua;
alter table acs_stylesheet_type_map
 drop constraint acs_sty_typ_map_sty_id_f_38x8p;
alter table acs_stylesheets
 drop constraint acs_stylesh_stylesh_id_f_2fiok;
alter table acs_stylesheets
 drop constraint acs_styleshee_local_id_f_wjfrg;
alter table apm_package_type_listener_map
 drop constraint apm_pac_typ_lis_map_li_f_i78gw;
alter table apm_package_type_listener_map
 drop constraint apm_pac_typ_lis_map_pa_f_0_qfw;
alter table apm_packages
 drop constraint apm_packa_packa_typ_id_f_adr4w;
alter table apm_packages
 drop constraint apm_package_package_id_f_46may;
alter table apm_packages
 drop constraint apm_packages_locale_id_f_qlps4;
alter table application_type_privilege_map
 drop constraint appl_typ_pri_map_app_t_f_kgrfj;
alter table application_type_privilege_map
 drop constraint appl_typ_pri_map_privi_f_s3pwb;
alter table application_types
 drop constraint applica_typ_pac_typ_id_f_v80ma;
alter table application_types
 drop constraint applicat_typ_provid_id_f_bm274;
alter table applications
 drop constraint applica_applica_typ_id_f_k2bi3;
alter table applications
 drop constraint applica_par_applica_id_f_hvxh7;
alter table applications
 drop constraint applicati_applicati_id_f_a35g2;
alter table applications
 drop constraint application_package_id_f_cdaho;
alter table cat_root_cat_object_map
 drop constraint cat_roo_cat_obj_map_ca_f_jqvmd;
alter table cat_root_cat_object_map
 drop constraint cat_roo_cat_obj_map_ob_f_anfmx;
alter table group_member_map
 drop constraint grou_memb_map_membe_id_f_bs3u_;
alter table group_member_map
 drop constraint grou_membe_map_grou_id_f_d7lhm;
alter table group_subgroup_map
 drop constraint grou_subg_map_subgr_id_f_1jo4e;
alter table group_subgroup_map
 drop constraint grou_subgro_map_gro_id_f_todnr;
alter table groups
 drop constraint groups_group_id_f_l4tvr;
alter table object_container_map
 drop constraint obje_cont_map_conta_id_f_v66b1;
alter table object_container_map
 drop constraint obje_contai_map_obj_id_f_guads;
alter table object_context
 drop constraint objec_contex_contex_id_f_crdh1;
alter table object_context
 drop constraint objec_contex_object_id_f_mbuxe;
alter table parameterized_privileges
 drop constraint para_pri_bas_privilege_f_elb6t;
alter table parties
 drop constraint parties_party_id_f_j4k1i;
alter table party_email_map
 drop constraint part_emai_map_party_id_f_7_00_;
alter table portals
 drop constraint portals_portal_id_f_kbx1t;
alter table portlets
 drop constraint portlets_portal_id_f_bombq;
alter table portlets
 drop constraint portlets_portlet_id_f_erf4o;
alter table roles
 drop constraint role_implicit_group_id_f_o6g0p;
alter table roles
 drop constraint roles_group_id_f_doyeu;
alter table search_test_author
 drop constraint sear_tes_auth_autho_id_f_klil2;
alter table search_test_book
 drop constraint searc_tes_book_book_id_f_eqgc0;
alter table search_test_book_chapter
 drop constraint sear_tes_boo_cha_cha_i_f_fonpi;
alter table site_nodes
 drop constraint site_nodes_node_id_f_n1m2y;
alter table site_nodes
 drop constraint site_nodes_object_id_f_ked74;
alter table site_nodes
 drop constraint site_nodes_parent_id_f_sacav;
alter table user_authentication
 drop constraint user_authentica_aut_id_f_0bgpj;
alter table user_authentication
 drop constraint user_authentica_use_id_f_z1jvj;
alter table users
 drop constraint users_name_id_f_0xbbm;
alter table users
 drop constraint users_user_id_f_t_lso;
alter table vc_blob_operations
 drop constraint vc_blo_operat_opera_id_f_qcpj1;
alter table vc_clob_operations
 drop constraint vc_clo_operat_opera_id_f_k752z;

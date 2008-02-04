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
-- $Id: update-constraints.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

alter table acs_permissions drop constraint acs_premissions_pk;
alter table acs_permissions add 
    constraint acs_per_gra_id_obj_id__p_lrweb
      primary key(grantee_id, object_id, privilege);

-- primary key of cat_category_purpose_map has changed
alter table cat_category_purpose_map drop constraint cat_cat_pur_map_pk;
alter table cat_category_purpose_map add 
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id);


--drop unused constraints
alter table apm_package_type_listener_map drop constraint apm_listener_map_id_class_un;
alter table group_member_map drop constraint gmm_group_member_un;
alter table group_subgroup_map drop constraint gsm_group_party_un;
alter table parameterized_privileges drop constraint param_priv_un;
alter table party_email_map drop constraint pem_party_email_uq;
alter table site_nodes drop constraint site_nodes_un;
alter table user_authentication drop constraint user_auth_user_un;
alter table cat_category_purpose_map drop constraint cat_obj_map_purpose_id_fk;
alter table site_nodes drop constraint site_nodes_directory_p_ck;
alter table site_nodes drop constraint site_nodes_object_id_fk;
alter table site_nodes drop constraint site_nodes_pattern_p_ck;

-- add new constraints
alter table acs_stylesheet_node_map add 
    constraint acs_sty_nod_map_nod_id_p_xf2u7
    primary key(node_id, stylesheet_id);

alter table acs_stylesheet_type_map add 
    constraint acs_sty_typ_map_pac_ty_p_afjeo
    primary key(package_type_id, stylesheet_id);

alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_li_p_6_z6o
    primary key(listener_id, package_type_id);

alter table group_member_map add
    constraint grou_mem_map_gro_id_me_p_9zo_i
    primary key(group_id, member_id);

alter table group_subgroup_map add
    constraint grou_sub_map_gro_id_su_p_8caa0
    primary key(group_id, subgroup_id);

alter table parameterized_privileges add
    constraint para_pri_bas_pri_par_k_p_a1rpb
    primary key(base_privilege, param_key);

alter table party_email_map add
    constraint part_ema_map_ema_add_p_p_px7u4
    primary key(email_address, party_id);

alter table site_nodes add 
    constraint site_node_nam_paren_id_u_a3b4a
    unique(name, parent_id);

alter table cat_category_purpose_map add
    constraint cat_obj_map_purpose_id_fk
    foreign key (purpose_id) references cat_purposes(purpose_id) on delete cascade;

alter table site_nodes add 
    constraint site_nodes_node_id_f_n1m2y foreign key (node_id)
    references acs_objects(object_id) on delete cascade;

alter table site_nodes add 
    constraint site_nodes_object_id_f_ked74 foreign key (object_id)
    references apm_packages(package_id);

alter table user_authentication add 
    constraint user_authentica_aut_id_f_0bgpj foreign key (auth_id)
      references parties(party_id) on delete cascade;

-- The following ddl will only work on Oracle 9.2 or greater
---- rename constraints
---- (primary key)
--alter table acs_objects rename constraint acs_objects_pk to acs_objects_object_id_p_hhkb1;
--alter table acs_privileges rename constraint acs_privileges_pk to acs_privileg_privilege_p_hdekj;
--alter table acs_stylesheets rename constraint acs_stylesheets_pk to acs_stylesh_stylesh_id_p_d9uk7;
--alter table apm_listeners rename constraint apm_listeners_pk to apm_listene_listene_id_p_yebi8;
--alter table apm_package_types rename constraint apm_package_types_pk to apm_pac_typ_pac_typ_id_p_q7ayv;
--alter table apm_packages rename constraint apm_packages_pack_id_pk to apm_package_package_id_p_vrfsh;
--alter table email_addresses rename constraint email_addresses_pk to emai_addre_ema_address_p_8hqha;
--alter table groups rename constraint groups_pk to groups_group_id_p_rv_hr;
--alter table object_container_map rename constraint aocm_object_id_pk to obje_contai_map_obj_id_p_ymkb5;
--alter table object_context rename constraint object_context_pk to objec_contex_object_id_p_32pb_;
--alter table parties rename constraint parties_pk to parties_party_id_p_jojxy;
--alter table person_names rename constraint person_names_pk to person_names_name_id_p_vog3f;
--alter table roles rename constraint roles_role_id_pk to roles_role_id_p_qlqi_;
--alter table site_nodes rename constraint site_nodes_node_id_pk to site_nodes_node_id_p_omovr;
--alter table user_authentication rename constraint user_auth_pk to user_authentica_aut_id_p_0o1jz;
--alter table users rename constraint users_pk to users_user_id_p_rpupb;
--alter table vc_blob_operations rename constraint vc_blob_operations_pk to vc_blo_operat_opera_id_p_zsnap;
--alter table vc_clob_operations rename constraint vc_clob_operations_pk to vc_clo_operat_opera_id_p_t3jh7;
---- (unique)
--alter table apm_listeners rename constraint apm_listeners_class_un to apm_listen_liste_class_u_cr5q1;
--alter table apm_package_types rename constraint apm_packages_types_p_uri_un to apm_pack_typ_packa_uri_u_ish63;
--alter table apm_package_types rename constraint apm_package_types_key_un to apm_pack_typ_packa_key_u_xjbf1;
--alter table apm_package_types rename constraint apm_package_types_pretty_n_un to apm_pack_typ_pret_name_u_8xzvk;
--alter table apm_package_types rename constraint apm_package_types_pretty_pl_un to apm_pac_typ_pre_plural_u_kqgl6;
--alter table roles rename constraint roles_group_id_name_un to roles_group_id_name_u_g5v82;
--alter table users rename constraint users_screen_name_un to users_screen_name_u__c5u2;
---- (foreign key)
--alter table acs_permissions rename constraint acs_perm_creation_user_fk to acs_permiss_creat_user_f_hiyn9;
--alter table acs_permissions rename constraint acs_permissions_grantee_id_fk to acs_permissio_grant_id_f_vmo0e;
--alter table acs_permissions rename constraint acs_permissions_on_what_id_fk to acs_permissio_objec_id_f_5swtm;
--alter table acs_permissions rename constraint acs_permissions_priv_fk to acs_permissi_privilege_f_p76ev;
--alter table acs_stylesheet_node_map rename constraint acs_stylesheet_node_node_fk to acs_sty_nod_map_nod_id_f_q55q3;
--alter table acs_stylesheet_node_map rename constraint acs_stylesheet_node_sheet_fk to acs_sty_nod_map_sty_id_f_guej5;
--alter table acs_stylesheet_type_map rename constraint acs_stylesheet_type_sheet_fk to acs_sty_typ_map_sty_id_f_38x8p;
--alter table acs_stylesheet_type_map rename constraint acs_stylesheet_type_type_fk to acs_sty_typ_map_pac_ty_f_emkua;
--alter table acs_stylesheets rename constraint acs_stylesheet_id_fk to acs_stylesh_stylesh_id_f_2fiok;
--alter table acs_stylesheets rename constraint acs_stylesheets_locale_fk to acs_styleshee_local_id_f_wjfrg;
--alter table apm_package_type_listener_map rename constraint apm_listener_map_list_id_fk to apm_pac_typ_lis_map_li_f_i78gw;
--alter table apm_package_type_listener_map rename constraint apm_listener_map_pt_id_fk to apm_pac_typ_lis_map_pa_f_0_qfw;
--alter table apm_packages rename constraint apm_packages_locale_id_fk to apm_packages_locale_id_f_qlps4;
--alter table apm_packages rename constraint apm_packages_package_id_fk to apm_package_package_id_f_46may;
--alter table apm_packages rename constraint apm_packages_type_id_fk to apm_packa_packa_typ_id_f_adr4w;
--alter table group_member_map rename constraint gmm_group_id_fk to grou_membe_map_grou_id_f_d7lhm;
--alter table group_member_map rename constraint gmm_member_id_fk to grou_memb_map_membe_id_f_bs3u_;
--alter table group_subgroup_map rename constraint gsm_group_id_fk to grou_subgro_map_gro_id_f_todnr;
--alter table group_subgroup_map rename constraint gsm_subgroup_id_fk to grou_subg_map_subgr_id_f_1jo4e;
--alter table groups rename constraint groups_group_id_fk to groups_group_id_f_l4tvr;
--alter table object_container_map rename constraint aocm_container_id_fk to obje_cont_map_conta_id_f_v66b1;
--alter table object_container_map rename constraint aocm_object_id_fk to obje_contai_map_obj_id_f_guads;
--alter table object_context rename constraint object_context_context_id_fk to objec_contex_contex_id_f_crdh1;
--alter table object_context rename constraint object_context_object_id_fk to objec_contex_object_id_f_mbuxe;
--alter table parameterized_privileges rename constraint param_priv_base_privilege_fk to para_pri_bas_privilege_f_elb6t;
--alter table parties rename constraint parties_party_id_fk to parties_party_id_f_j4k1i;
--alter table party_email_map rename constraint pem_party_id_fk to part_emai_map_party_id_f_7_00_;
--alter table roles rename constraint group_roles_group_id_fk to roles_group_id_f_doyeu;
--alter table roles rename constraint group_roles_impl_group_id_fk to role_implicit_group_id_f_o6g0p;
--alter table site_nodes rename constraint site_nodes_parent_id_fk to site_nodes_parent_id_f_sacav;
--alter table user_authentication rename constraint user_auth_user_id_fk to user_authentica_use_id_f_z1jvj;
--alter table users rename constraint users_person_name_id_fk to users_name_id_f_0xbbm;
--alter table users rename constraint users_user_id_fk to users_user_id_f_t_lso;
--alter table vc_blob_operations rename constraint vc_blob_operations_fk to vc_blo_operat_opera_id_f_qcpj1;
--alter table vc_clob_operations rename constraint vc_clob_operations_fk to vc_clo_operat_opera_id_f_k752z;

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
-- $Id: update-indexes.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

alter index acs_objects_pk rename to acs_objects_object_id_p_hhkb1;
alter index acs_privileges_pk rename to acs_privileg_privilege_p_hdekj;
alter index acs_stylesheets_pk rename to acs_stylesh_stylesh_id_p_d9uk7;
alter index apm_listeners_class_un rename to apm_listen_liste_class_u_cr5q1;
alter index apm_listeners_pk rename to apm_listene_listene_id_p_yebi8;
alter index apm_packages_pack_id_pk rename to apm_package_package_id_p_vrfsh;
alter index apm_packages_types_p_uri_un rename to apm_pack_typ_packa_uri_u_ish63;
alter index apm_package_types_key_un rename to apm_pack_typ_packa_key_u_xjbf1;
alter index apm_package_types_pk rename to apm_pac_typ_pac_typ_id_p_q7ayv;
alter index apm_package_types_pretty_n_un rename to apm_pack_typ_pret_name_u_8xzvk;
alter index apm_package_types_pretty_pl_un rename to apm_pac_typ_pre_plural_u_kqgl6;
alter index email_addresses_pk rename to emai_addre_ema_address_p_8hqha;
alter index groups_pk rename to groups_group_id_p_rv_hr;
alter index aocm_object_id_pk rename to obje_contai_map_obj_id_p_ymkb5;
alter index object_context_pk rename to objec_contex_object_id_p_32pb_;
alter index parties_pk rename to parties_party_id_p_jojxy;
alter index person_names_pk rename to person_names_name_id_p_vog3f;
alter index roles_group_id_name_un rename to roles_group_id_name_u_g5v82;
alter index roles_role_id_pk rename to roles_role_id_p_qlqi_;
alter index section_wf_template_map_pk rename to sect_wor_tem_map_sec_i_p_jaofv;
alter index site_nodes_node_id_pk rename to site_nodes_node_id_p_omovr;
alter index users_person_name_id_un rename to users_name_id_idx;
alter index users_pk rename to users_user_id_p_rpupb;
alter index users_screen_name_un rename to users_screen_name_u__c5u2;
alter index user_auth_pk rename to user_authentica_aut_id_p_0o1jz;
alter index vc_blob_operations_pk rename to vc_blo_operat_opera_id_p_zsnap;
alter index vc_clob_operations_pk rename to vc_clo_operat_opera_id_p_t3jh7;

create index acs_perm_object_id_idx on acs_permissions(object_id);
create index apm_pac_typ_lis_map_p_t_id_idx on apm_package_type_listener_map(package_type_id);
create index appl_typ_pri_map_privilege_idx on application_type_privilege_map(privilege);
create index appli_typ_package_type_id_idx on application_types(package_type_id);
create index appli_typ_provider_id_idx on application_types(provider_id);
create index applicati_applicati_typ_id_idx on applications(application_type_id);
create index applicati_package_id_idx on applications(package_id);
create index applicati_parent_app_id_idx on applications(parent_application_id);
create index cat_cat_subcat_index_sb_id_idx on cat_cat_subcat_trans_index(subcategory_id);
create index cat_roo_cat_obj_map_cat_id_idx on cat_root_cat_object_map(category_id);
create index forms_dds_query_id_idx on forms_dd_select(query_id);
create index forms_wgt_label_widget_id_idx on forms_widget_label(widget_id);
create index part_ema_map_party_id_idx on party_email_map(party_id);
create index portlets_portal_id_idx on portlets(portal_id);
create index site_nodes_parent_id_idx on site_nodes (parent_id);
create index user_authentica_user_id_idx on user_authentication(user_id);

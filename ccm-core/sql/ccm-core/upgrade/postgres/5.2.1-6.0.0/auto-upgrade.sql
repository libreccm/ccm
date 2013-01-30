--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: auto-upgrade.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $


--------------------------------------------------------------------------------
-- These columns went from 'not nullable' to 'nullable'.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These columns went from 'nullable' to 'not nullable'.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES alter ABSTRACT_P set not null;
alter table CAT_CATEGORIES alter ENABLED_P set not null;
alter table PORTLETS alter PORTAL_ID set not null;

--------------------------------------------------------------------------------
-- These default values for these columns changed.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES alter ABSTRACT_P drop default;

--------------------------------------------------------------------------------
-- These char(1) boolean check constraints have been added.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These 'not null' check constraints have been dropped.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These foreign key constraints have change their action for 'on delete'.
-- Their names may have changed as well.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These constraints have changed their name.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These constraints have been added.
--------------------------------------------------------------------------------
ALTER TABLE acs_auditing ADD CONSTRAINT audited_acs_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id) ON DELETE CASCADE;
ALTER TABLE acs_auditing ADD CONSTRAINT audited_creation_user_fk FOREIGN KEY(creation_user) references users(user_id);
ALTER TABLE acs_auditing ADD CONSTRAINT audited_modifying_user_fk FOREIGN KEY(modifying_user) references users(user_id);
ALTER TABLE acs_permissions ADD CONSTRAINT acs_permiss_creat_user_f_hiyn9 FOREIGN KEY(creation_user) references users(user_id);
ALTER TABLE acs_permissions ADD CONSTRAINT acs_permissi_privilege_f_p76ev FOREIGN KEY(privilege) references acs_privileges(privilege);
ALTER TABLE acs_permissions ADD CONSTRAINT acs_permissio_grant_id_f_vmo0e FOREIGN KEY(grantee_id) references parties(party_id);
ALTER TABLE acs_permissions ADD CONSTRAINT acs_permissio_objec_id_f_5swtm FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE acs_stylesheet_node_map ADD CONSTRAINT acs_sty_nod_map_nod_id_f_q55q3 FOREIGN KEY(node_id) references site_nodes(node_id);
ALTER TABLE acs_stylesheet_node_map ADD CONSTRAINT acs_sty_nod_map_sty_id_f_guej5 FOREIGN KEY(stylesheet_id) references acs_stylesheets(stylesheet_id);
ALTER TABLE acs_stylesheet_type_map ADD CONSTRAINT acs_sty_typ_map_pac_ty_f_emkua FOREIGN KEY(package_type_id) references apm_package_types(package_type_id);
ALTER TABLE acs_stylesheet_type_map ADD CONSTRAINT acs_sty_typ_map_sty_id_f_38x8p FOREIGN KEY(stylesheet_id) references acs_stylesheets(stylesheet_id);
ALTER TABLE acs_stylesheets ADD CONSTRAINT acs_stylesh_stylesh_id_f_2fiok FOREIGN KEY(stylesheet_id) references acs_objects(object_id);
ALTER TABLE acs_stylesheets ADD CONSTRAINT acs_styleshee_local_id_f_wjfrg FOREIGN KEY(locale_id) references g11n_locales(locale_id);
ALTER TABLE apm_listeners ADD CONSTRAINT apm_listen_liste_class_u_cr5q1 UNIQUE (listener_class);
ALTER TABLE apm_package_type_listener_map ADD CONSTRAINT apm_pac_typ_lis_map_li_f_i78gw FOREIGN KEY(listener_id) references apm_listeners(listener_id);
ALTER TABLE apm_package_type_listener_map ADD CONSTRAINT apm_pac_typ_lis_map_pa_f_0_qfw FOREIGN KEY(package_type_id) references apm_package_types(package_type_id);
ALTER TABLE apm_package_types ADD CONSTRAINT apm_pac_typ_pre_plural_u_kqgl6 UNIQUE (pretty_plural);
ALTER TABLE apm_package_types ADD CONSTRAINT apm_pack_typ_packa_key_u_xjbf1 UNIQUE (package_key);
ALTER TABLE apm_package_types ADD CONSTRAINT apm_pack_typ_packa_uri_u_ish63 UNIQUE (package_uri);
ALTER TABLE apm_package_types ADD CONSTRAINT apm_pack_typ_pret_name_u_8xzvk UNIQUE (pretty_name);
ALTER TABLE apm_packages ADD CONSTRAINT apm_packa_packa_typ_id_f_adr4w FOREIGN KEY(package_type_id) references apm_package_types(package_type_id);
ALTER TABLE apm_packages ADD CONSTRAINT apm_package_package_id_f_46may FOREIGN KEY(package_id) references acs_objects(object_id);
ALTER TABLE apm_packages ADD CONSTRAINT apm_packages_locale_id_f_qlps4 FOREIGN KEY(locale_id) references g11n_locales(locale_id);
ALTER TABLE application_type_privilege_map ADD CONSTRAINT appl_typ_pri_map_app_t_f_kgrfj FOREIGN KEY(application_type_id) references application_types(application_type_id);
ALTER TABLE application_type_privilege_map ADD CONSTRAINT appl_typ_pri_map_privi_f_s3pwb FOREIGN KEY(privilege) references acs_privileges(privilege);
ALTER TABLE application_types ADD CONSTRAINT applica_typ_pac_typ_id_f_v80ma FOREIGN KEY(package_type_id) references apm_package_types(package_type_id);
ALTER TABLE application_types ADD CONSTRAINT applicat_typ_obje_type_u_pf2uk UNIQUE (object_type);
ALTER TABLE application_types ADD CONSTRAINT applicat_typ_provid_id_f_bm274 FOREIGN KEY(provider_id) references application_types(application_type_id);
ALTER TABLE applications ADD CONSTRAINT applica_applica_typ_id_f_k2bi3 FOREIGN KEY(application_type_id) references application_types(application_type_id);
ALTER TABLE applications ADD CONSTRAINT applica_par_applica_id_f_hvxh7 FOREIGN KEY(parent_application_id) references applications(application_id);
ALTER TABLE applications ADD CONSTRAINT applicati_applicati_id_f_a35g2 FOREIGN KEY(application_id) references acs_objects(object_id);
ALTER TABLE applications ADD CONSTRAINT application_package_id_f_cdaho FOREIGN KEY(package_id) references apm_packages(package_id);
ALTER TABLE bebop_component_hierarchy ADD CONSTRAINT bebop_component_hierarchy_un UNIQUE (container_id,component_id);
ALTER TABLE bebop_component_hierarchy ADD CONSTRAINT bebop_component_hierarchyci_fk FOREIGN KEY(container_id) references bebop_components(component_id);
ALTER TABLE bebop_component_hierarchy ADD CONSTRAINT bebop_component_hierarchyco_fk FOREIGN KEY(component_id) references bebop_components(component_id);
ALTER TABLE bebop_components ADD CONSTRAINT bebop_components_id_fk FOREIGN KEY(component_id) references acs_objects(object_id);
ALTER TABLE bebop_form_process_listeners ADD CONSTRAINT bebop_form_process_lstnr_fs_fk FOREIGN KEY(form_section_id) references bebop_form_sections(form_section_id);
ALTER TABLE bebop_form_process_listeners ADD CONSTRAINT bebop_form_process_lstnr_li_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE bebop_form_process_listeners ADD CONSTRAINT bebop_form_process_lstnr_un UNIQUE (form_section_id,position);
ALTER TABLE bebop_form_sections ADD CONSTRAINT bebop_form_sections_id_fk FOREIGN KEY(form_section_id) references bebop_components(component_id);
ALTER TABLE bebop_listener_map ADD CONSTRAINT bebop_listener_map_cid_fk FOREIGN KEY(component_id) references bebop_components(component_id);
ALTER TABLE bebop_listener_map ADD CONSTRAINT bebop_listener_map_lid_fk FOREIGN KEY(listener_id) references bebop_listeners(listener_id);
ALTER TABLE bebop_listener_map ADD CONSTRAINT bebop_listener_map_un UNIQUE (component_id,listener_id);
ALTER TABLE bebop_listeners ADD CONSTRAINT bebop_listeners_id_fk FOREIGN KEY(listener_id) references acs_objects(object_id);
ALTER TABLE bebop_meta_object ADD CONSTRAINT bebop_meta_obj_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE bebop_meta_object ADD CONSTRAINT bebop_meta_obj_un UNIQUE (type_id,class_name);
ALTER TABLE bebop_meta_object ADD CONSTRAINT bebop_meta_object_type_id_fk FOREIGN KEY(type_id) references bebop_object_type(type_id) ON DELETE CASCADE;
ALTER TABLE bebop_object_type ADD CONSTRAINT bebop_object_type_type_id_fk FOREIGN KEY(type_id) references acs_objects(object_id);
ALTER TABLE bebop_object_type ADD CONSTRAINT bebop_object_type_un UNIQUE (app_type,class_name);
ALTER TABLE bebop_process_listeners ADD CONSTRAINT bebop_process_listeners_fk FOREIGN KEY(listener_id) references acs_objects(object_id);
ALTER TABLE bebop_widgets ADD CONSTRAINT bebop_widgets_id_fk FOREIGN KEY(widget_id) references bebop_components(component_id);
ALTER TABLE cat_cat_subcat_trans_index ADD CONSTRAINT cat_cat_subcat_index_c_fk FOREIGN KEY(category_id) references cat_categories(category_id) ON DELETE CASCADE;
ALTER TABLE cat_cat_subcat_trans_index ADD CONSTRAINT cat_cat_subcat_index_s_fk FOREIGN KEY(subcategory_id) references cat_categories(category_id) ON DELETE CASCADE;
ALTER TABLE cat_categories ADD CONSTRAINT cat_categori_catego_id_f__xtwr FOREIGN KEY(category_id) references acs_objects(object_id);
ALTER TABLE cat_category_category_map ADD CONSTRAINT cat_cat_catmap_un UNIQUE (category_id,related_category_id);
ALTER TABLE cat_category_category_map ADD CONSTRAINT cat_cat_map_category_id_fk FOREIGN KEY(related_category_id) references cat_categories(category_id);
ALTER TABLE cat_category_category_map ADD CONSTRAINT cat_cat_map_parent_id_fk FOREIGN KEY(category_id) references cat_categories(category_id);
ALTER TABLE cat_category_purpose_map ADD CONSTRAINT cat_cat_pur_map_cat_id_fk FOREIGN KEY(category_id) references cat_categories(category_id);
ALTER TABLE cat_category_purpose_map ADD CONSTRAINT cat_obj_map_purpose_id_fk FOREIGN KEY(purpose_id) references cat_purposes(purpose_id);
ALTER TABLE cat_object_category_map ADD CONSTRAINT cat_obj_cat_map_cat_id_fk FOREIGN KEY(category_id) references cat_categories(category_id);
ALTER TABLE cat_object_category_map ADD CONSTRAINT cat_obj_cat_map_un UNIQUE (category_id,object_id);
ALTER TABLE cat_object_category_map ADD CONSTRAINT cat_obj_map_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE cat_object_root_category_map ADD CONSTRAINT cat_obj_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id) ON DELETE CASCADE;
ALTER TABLE cat_object_root_category_map ADD CONSTRAINT cat_obj_package_id_fk FOREIGN KEY(package_id) references apm_packages(package_id) ON DELETE CASCADE;
ALTER TABLE cat_object_root_category_map ADD CONSTRAINT cat_obj_root_map_fk FOREIGN KEY(root_category_id) references cat_categories(category_id) ON DELETE CASCADE;
ALTER TABLE cat_purposes ADD CONSTRAINT cat_purposes_key_un UNIQUE (key);
ALTER TABLE cat_purposes ADD CONSTRAINT cat_purposes_purpose_id_fk FOREIGN KEY(purpose_id) references acs_objects(object_id);
ALTER TABLE cat_root_cat_object_map ADD CONSTRAINT cat_roo_cat_obj_map_ca_f_jqvmd FOREIGN KEY(category_id) references cat_categories(category_id);
ALTER TABLE cat_root_cat_object_map ADD CONSTRAINT cat_roo_cat_obj_map_ob_f_anfmx FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE cw_process_definitions ADD CONSTRAINT process_def_id_fk FOREIGN KEY(process_def_id) references cw_processes(process_id);
ALTER TABLE cw_processes ADD CONSTRAINT process_process_def_id_fk FOREIGN KEY(process_def_id) references cw_tasks(task_id) ON DELETE CASCADE;
ALTER TABLE cw_processes ADD CONSTRAINT process_task_id_fk FOREIGN KEY(process_id) references cw_tasks(task_id);
ALTER TABLE cw_processes ADD CONSTRAINT processes_object_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE cw_system_tasks ADD CONSTRAINT system_tasks_task_id_fk FOREIGN KEY(task_id) references cw_tasks(task_id) ON DELETE CASCADE;
ALTER TABLE cw_task_comments ADD CONSTRAINT task_comments_task_id_fk FOREIGN KEY(task_id) references cw_tasks(task_id);
ALTER TABLE cw_task_dependencies ADD CONSTRAINT cw_tas_depe_dep_tas_id_f_bn0m5 FOREIGN KEY(dependent_task_id) references cw_tasks(task_id);
ALTER TABLE cw_task_dependencies ADD CONSTRAINT cw_tas_dependen_tas_id_f_b1uoz FOREIGN KEY(task_id) references cw_tasks(task_id);
ALTER TABLE cw_task_group_assignees ADD CONSTRAINT cw_tas_gro_assi_gro_id_f_or5kj FOREIGN KEY(group_id) references groups(group_id);
ALTER TABLE cw_task_group_assignees ADD CONSTRAINT cw_tas_gro_assi_tas_id_f_mhi2k FOREIGN KEY(task_id) references cw_user_tasks(task_id);
ALTER TABLE cw_task_listeners ADD CONSTRAINT cw_tas_list_lis_tas_id_f_x1n02 FOREIGN KEY(listener_task_id) references cw_tasks(task_id);
ALTER TABLE cw_task_listeners ADD CONSTRAINT cw_tas_listener_tas_id_f_s2fj9 FOREIGN KEY(task_id) references cw_tasks(task_id);
ALTER TABLE cw_task_user_assignees ADD CONSTRAINT cw_tas_use_assi_tas_id_f_feri7 FOREIGN KEY(task_id) references cw_user_tasks(task_id);
ALTER TABLE cw_task_user_assignees ADD CONSTRAINT cw_tas_use_assi_use_id_f_w856_ FOREIGN KEY(user_id) references users(user_id);
ALTER TABLE cw_tasks ADD CONSTRAINT task_parent_task_id FOREIGN KEY(parent_task_id) references cw_tasks(task_id) ON DELETE CASCADE;
ALTER TABLE cw_user_tasks ADD CONSTRAINT user_tasks_task_id_fk FOREIGN KEY(task_id) references cw_tasks(task_id);
ALTER TABLE forms_dataquery ADD CONSTRAINT forms_dataquery_un UNIQUE (type_id,name);
ALTER TABLE forms_dataquery ADD CONSTRAINT forms_dq_query_id_fk FOREIGN KEY(query_id) references acs_objects(object_id);
ALTER TABLE forms_dataquery ADD CONSTRAINT forms_dq_query_type_id_fk FOREIGN KEY(type_id) references bebop_object_type(type_id) ON DELETE CASCADE;
ALTER TABLE forms_dd_select ADD CONSTRAINT forms_dds_query_id_fk FOREIGN KEY(query_id) references forms_dataquery(query_id) ON DELETE CASCADE;
ALTER TABLE forms_dd_select ADD CONSTRAINT forms_dds_widget_id_fk FOREIGN KEY(widget_id) references bebop_widgets(widget_id);
ALTER TABLE forms_lstnr_conf_email ADD CONSTRAINT forms_lstnr_conf_email_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE forms_lstnr_conf_redirect ADD CONSTRAINT forms_lstnr_conf_redirect_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE forms_lstnr_simple_email ADD CONSTRAINT forms_lstnr_simple_email_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE forms_lstnr_tmpl_email ADD CONSTRAINT forms_lstnr_tmpl_email_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE forms_lstnr_xml_email ADD CONSTRAINT forms_lstnr_xml_email_fk FOREIGN KEY(listener_id) references bebop_process_listeners(listener_id);
ALTER TABLE forms_widget_label ADD CONSTRAINT forms_wgt_label_label_id_fk FOREIGN KEY(label_id) references bebop_widgets(widget_id);
ALTER TABLE forms_widget_label ADD CONSTRAINT forms_wgt_label_widget_id_fk FOREIGN KEY(widget_id) references bebop_widgets(widget_id) ON DELETE CASCADE;
ALTER TABLE g11n_catalogs ADD CONSTRAINT g11n_catalogs_locale_id_fk FOREIGN KEY(locale_id) references g11n_locales(locale_id);
ALTER TABLE g11n_locale_charset_map ADD CONSTRAINT g11n_lcm_charset_id_fk FOREIGN KEY(charset_id) references g11n_charsets(charset_id);
ALTER TABLE g11n_locale_charset_map ADD CONSTRAINT g11n_lcm_locale_id_fk FOREIGN KEY(locale_id) references g11n_locales(locale_id);
ALTER TABLE g11n_locales ADD CONSTRAINT g11n_locales_def_charset_id_fk FOREIGN KEY(default_charset_id) references g11n_charsets(charset_id);
ALTER TABLE granted_context_non_leaf_map ADD CONSTRAINT gcnlm_implied_context_id_fk FOREIGN KEY(implied_context_id) references acs_objects(object_id);
ALTER TABLE granted_context_non_leaf_map ADD CONSTRAINT gcnlm_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE group_member_map ADD CONSTRAINT grou_memb_map_membe_id_f_bs3u_ FOREIGN KEY(member_id) references users(user_id);
ALTER TABLE group_member_map ADD CONSTRAINT grou_membe_map_grou_id_f_d7lhm FOREIGN KEY(group_id) references groups(group_id);
ALTER TABLE group_member_trans_index ADD CONSTRAINT gmti_group_id_fk FOREIGN KEY(group_id) references groups(group_id) ON DELETE CASCADE;
ALTER TABLE group_member_trans_index ADD CONSTRAINT gmti_subgroup_id_fk FOREIGN KEY(member_id) references users(user_id) ON DELETE CASCADE;
ALTER TABLE group_subgroup_map ADD CONSTRAINT grou_subg_map_subgr_id_f_1jo4e FOREIGN KEY(subgroup_id) references groups(group_id);
ALTER TABLE group_subgroup_map ADD CONSTRAINT grou_subgro_map_gro_id_f_todnr FOREIGN KEY(group_id) references groups(group_id);
ALTER TABLE group_subgroup_trans_index ADD CONSTRAINT gsti_group_id_fk FOREIGN KEY(group_id) references groups(group_id) ON DELETE CASCADE;
ALTER TABLE group_subgroup_trans_index ADD CONSTRAINT gsti_subgroup_id_fk FOREIGN KEY(subgroup_id) references groups(group_id) ON DELETE CASCADE;
ALTER TABLE groups ADD CONSTRAINT groups_group_id_f_l4tvr FOREIGN KEY(group_id) references parties(party_id);
ALTER TABLE message_parts ADD CONSTRAINT message_parts_message_id_fk FOREIGN KEY(message_id) references messages(message_id);
ALTER TABLE message_threads ADD CONSTRAINT msg_threads_root_id_fk FOREIGN KEY(root_id) references messages(message_id);
ALTER TABLE message_threads ADD CONSTRAINT msg_threads_root_id_un UNIQUE (root_id);
ALTER TABLE message_threads ADD CONSTRAINT msg_threads_sender_fk FOREIGN KEY(sender) references parties(party_id);
ALTER TABLE message_threads ADD CONSTRAINT msg_threads_thread_id_fk FOREIGN KEY(thread_id) references acs_objects(object_id);
ALTER TABLE messages ADD CONSTRAINT messages_message_id_fk FOREIGN KEY(message_id) references acs_objects(object_id);
ALTER TABLE messages ADD CONSTRAINT messages_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id) ON DELETE CASCADE;
ALTER TABLE messages ADD CONSTRAINT messages_reply_to_fk FOREIGN KEY(in_reply_to) references messages(message_id) ON DELETE SET NULL;
ALTER TABLE messages ADD CONSTRAINT messages_root_id_fk FOREIGN KEY(root_id) references messages(message_id) ON DELETE CASCADE;
ALTER TABLE messages ADD CONSTRAINT messages_sender_fk FOREIGN KEY(sender) references parties(party_id);
ALTER TABLE nt_digests ADD CONSTRAINT nt_digest_fk FOREIGN KEY(digest_id) references acs_objects(object_id);
ALTER TABLE nt_digests ADD CONSTRAINT nt_digest_party_from_fk FOREIGN KEY(party_from) references parties(party_id);
ALTER TABLE nt_queue ADD CONSTRAINT nt_queue_party_to_fk FOREIGN KEY(party_to) references parties(party_id) ON DELETE CASCADE;
ALTER TABLE nt_queue ADD CONSTRAINT nt_queue_request_fk FOREIGN KEY(request_id) references nt_requests(request_id) ON DELETE CASCADE;
ALTER TABLE nt_requests ADD CONSTRAINT nt_requests_digest_fk FOREIGN KEY(digest_id) references nt_digests(digest_id);
ALTER TABLE nt_requests ADD CONSTRAINT nt_requests_fk FOREIGN KEY(request_id) references acs_objects(object_id);
ALTER TABLE nt_requests ADD CONSTRAINT nt_requests_message_fk FOREIGN KEY(message_id) references messages(message_id) ON DELETE CASCADE;
ALTER TABLE nt_requests ADD CONSTRAINT nt_requests_party_to_fk FOREIGN KEY(party_to) references parties(party_id);
ALTER TABLE object_container_map ADD CONSTRAINT obje_cont_map_conta_id_f_v66b1 FOREIGN KEY(container_id) references acs_objects(object_id);
ALTER TABLE object_container_map ADD CONSTRAINT obje_contai_map_obj_id_f_guads FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE object_context ADD CONSTRAINT objec_contex_contex_id_f_crdh1 FOREIGN KEY(context_id) references acs_objects(object_id);
ALTER TABLE object_context ADD CONSTRAINT objec_contex_object_id_f_mbuxe FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE object_context_map ADD CONSTRAINT ocm_context_id_fk FOREIGN KEY(context_id) references acs_objects(object_id);
ALTER TABLE object_context_map ADD CONSTRAINT ocm_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE object_grants ADD CONSTRAINT object_grants_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE parameterized_privileges ADD CONSTRAINT para_pri_bas_privilege_f_elb6t FOREIGN KEY(base_privilege) references acs_privileges(privilege);
ALTER TABLE parties ADD CONSTRAINT parties_party_id_f_j4k1i FOREIGN KEY(party_id) references acs_objects(object_id);
ALTER TABLE party_email_map ADD CONSTRAINT part_emai_map_party_id_f_7_00_ FOREIGN KEY(party_id) references parties(party_id);
ALTER TABLE persistence_dynamic_assoc ADD CONSTRAINT pers_dyn_assoc_pdl_id_fk FOREIGN KEY(pdl_id) references acs_objects(object_id);
ALTER TABLE persistence_dynamic_assoc ADD CONSTRAINT pers_dyn_assoc_un UNIQUE (model_name,object_type_one,property_one,object_type_two,property_two);
ALTER TABLE persistence_dynamic_ot ADD CONSTRAINT persist_dynamic_ot_dot_un UNIQUE (dynamic_object_type);
ALTER TABLE persistence_dynamic_ot ADD CONSTRAINT persist_dynamic_ot_pdl_id_fk FOREIGN KEY(pdl_id) references acs_objects(object_id);
ALTER TABLE portals ADD CONSTRAINT portals_portal_id_f_kbx1t FOREIGN KEY(portal_id) references applications(application_id);
ALTER TABLE portlets ADD CONSTRAINT portlets_portal_id_f_bombq FOREIGN KEY(portal_id) references portals(portal_id);
ALTER TABLE portlets ADD CONSTRAINT portlets_portlet_id_f_erf4o FOREIGN KEY(portlet_id) references applications(application_id);
ALTER TABLE preferences ADD CONSTRAINT preferences_parent_fk FOREIGN KEY(parent_id) references preferences(preference_id);
ALTER TABLE roles ADD CONSTRAINT role_implicit_group_id_f_o6g0p FOREIGN KEY(implicit_group_id) references groups(group_id);
ALTER TABLE roles ADD CONSTRAINT roles_group_id_f_doyeu FOREIGN KEY(group_id) references groups(group_id);
ALTER TABLE roles ADD CONSTRAINT roles_group_id_name_u_g5v82 UNIQUE (group_id,name);
ALTER TABLE site_nodes ADD CONSTRAINT site_nodes_node_id_f_n1m2y FOREIGN KEY(node_id) references acs_objects(object_id);
ALTER TABLE site_nodes ADD CONSTRAINT site_nodes_object_id_f_ked74 FOREIGN KEY(object_id) references apm_packages(package_id);
ALTER TABLE site_nodes ADD CONSTRAINT site_nodes_parent_id_f_sacav FOREIGN KEY(parent_id) references site_nodes(node_id);
ALTER TABLE ungranted_context_non_leaf_map ADD CONSTRAINT ucnlm_implied_context_id_fk FOREIGN KEY(implied_context_id) references acs_objects(object_id);
ALTER TABLE ungranted_context_non_leaf_map ADD CONSTRAINT ucnlm_object_id_fk FOREIGN KEY(object_id) references acs_objects(object_id);
ALTER TABLE user_authentication ADD CONSTRAINT user_authentica_aut_id_f_0bgpj FOREIGN KEY(auth_id) references parties(party_id);
ALTER TABLE user_authentication ADD CONSTRAINT user_authentica_use_id_f_z1jvj FOREIGN KEY(user_id) references users(user_id);
ALTER TABLE users ADD CONSTRAINT users_name_id_f_0xbbm FOREIGN KEY(name_id) references person_names(name_id);
ALTER TABLE users ADD CONSTRAINT users_screen_name_u__c5u2 UNIQUE (screen_name);
ALTER TABLE users ADD CONSTRAINT users_user_id_f_t_lso FOREIGN KEY(user_id) references parties(party_id);
ALTER TABLE vc_generic_operations ADD CONSTRAINT vc_gen_operations_fk FOREIGN KEY(operation_id) references vc_operations(operation_id) ON DELETE CASCADE;
ALTER TABLE vc_objects ADD CONSTRAINT vc_objects_mst_fk FOREIGN KEY(master_id) references acs_objects(object_id) ON DELETE SET NULL;
ALTER TABLE vc_objects ADD CONSTRAINT vc_objects_obj_fk FOREIGN KEY(object_id) references acs_objects(object_id) ON DELETE CASCADE;


--------------------------------------------------------------------------------
-- These indexes have changed their name.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These indexes have been added.
--------------------------------------------------------------------------------
create index group_member_map_group_id_idx on group_member_map(group_id);
create index group_subgroup_map_grp_id_idx on group_subgroup_map(group_id);
create index vcx_obj_changes_txn_id_idx on vcx_obj_changes(txn_id);
create index vcx_operations_change_id_idx on vcx_operations(change_id);
create index vcx_operations_class_id_idx on vcx_operations(class_id);
create index vcx_operations_evnt_typ_id_idx on vcx_operations(event_type_id);
create index vcx_tags_txn_id_idx on vcx_tags(txn_id);
create index vcx_txns_modifying_user_idx on vcx_txns(modifying_user);

--------------------------------------------------------------------------------
-- These indexes have been dropped.
--------------------------------------------------------------------------------

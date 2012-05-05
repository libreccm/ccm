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
-- $Id: oracle-se-create.sql 2141 2011-01-16 12:17:15Z pboy $
-- $DateTime: 2004/08/16 18:10:38 $

@@ oracle-se/function-currentDate.sql

@@ ddl/oracle-se/create.sql



@@ default/globalization/table-g11n_charsets.sql
@@ default/globalization/index-g11n_charsets.sql
@@ default/globalization/table-g11n_locales.sql
@@ default/globalization/index-g11n_locales.sql
@@ default/globalization/table-g11n_locale_charset_map.sql
@@ default/globalization/index-g11n_locale_charset_map.sql
@@ default/globalization/table-g11n_catalogs.sql
@@ default/globalization/index-g11n_catalogs.sql



@@ oracle-se/preferences/table-preferences.sql
@@ default/preferences/index-preferences.sql
@@ default/preferences/comment-preferences.sql



@@ default/kernel/package-hierarchy_denormalization.sql
@@ default/kernel/sequence-acs_object_id_seq.sql
@@ default/kernel/index-acs_objects.sql
@@ default/kernel/index-object_container_map.sql

@@ default/kernel/comment-email_addresses.sql
@@ default/kernel/index-parties.sql
@@ default/kernel/comment-parties.sql
@@ default/kernel/index-party_email_map.sql
@@ default/kernel/comment-party_email_map.sql
@@ default/kernel/comment-person_names.sql
@@ default/kernel/index-users.sql
@@ default/kernel/comment-users.sql

@@ default/kernel/index-roles.sql
@@ default/kernel/comment-roles.sql

@@ default/kernel/index-user_authentication.sql

@@ default/kernel/table-group_subgroup_trans_index.sql
@@ default/kernel/index-group_subgroup_trans_index.sql
@@ default/kernel/table-group_member_trans_index.sql
@@ default/kernel/index-group_member_trans_index.sql
@@ default/kernel/package-parties_denormalization.sql
@@ default/kernel/trigger-acs_parties.sql
@@ default/kernel/view-group_subgroup_trans_map.sql
@@ default/kernel/view-group_member_trans_map.sql
@@ default/kernel/view-party_member_trans_map.sql

@@ default/kernel/index-group_member_map_group_id_idx.sql
@@ default/kernel/index-group_subgroup_map_grp_id_idx.sql

@@ default/kernel/comment-acs_privileges.sql
@@ default/kernel/table-acs_privilege_hierarchy.sql
@@ default/kernel/index-acs_privilege_hierarchy.sql
@@ default/kernel/comment-acs_privilege_hierarchy.sql

@@ default/kernel/table-dnm_privileges.sql
@@ default/kernel/comment-dnm_privileges.sql
@@ default/kernel/table-dnm_privilege_col_map.sql
@@ default/kernel/comment-dnm_privilege_col_map.sql
@@ default/kernel/table-dnm_privilege_hierarchy_map.sql
@@ default/kernel/table-dnm_privilege_hierarchy.sql
@@ default/kernel/comment-dnm_privilege_hierarchy.sql

@@ default/kernel/table-dnm_permissions.sql
@@ default/kernel/comment-dnm_permissions.sql
@@ default/kernel/index-dnm_permissions.sql

@@ oracle-se/kernel/package-dnm_privileges.sql
@@ oracle-se/kernel/triggers-dnm_privileges.sql

@@ default/kernel/comment-acs_permissions.sql

@@ default/kernel/insert-privileges.sql

@@ default/kernel/comment-object_context.sql
@@ default/kernel/insert-object_zero.sql

@@ default/kernel/table-dnm_object_1_granted_context.sql
@@ default/kernel/table-dnm_object_grants.sql
@@ default/kernel/table-dnm_granted_context.sql

@@ default/kernel/index-dnm_object_1_granted_context.sql
@@ default/kernel/index-dnm_granted_context.sql

@@ oracle-se/kernel/package-dnm_context.sql
@@ default/kernel/insert-dnm_context.sql
@@ oracle-se/kernel/triggers-dnm_context.sql

@@ default/kernel/table-dnm_group_membership.sql
@@ default/kernel/index-dnm_group_membership.sql
@@ default/kernel/table-dnm_party_grants.sql

@@ oracle-se/kernel/package-dnm_parties.sql
@@ default/kernel/insert-dnm_group_membership.sql
@@ oracle-se/kernel/triggers-dnm_parties.sql

@@ default/kernel/index-acs_permissions.sql

@@ default/kernel/insert-users.sql
@@ default/kernel/insert-groups.sql
@@ default/kernel/insert-permissions.sql

@@ default/kernel/index-site_nodes.sql
@@ default/kernel/index-apm_packages.sql
@@ default/kernel/index-apm_package_type_listener_map.sql
@@ default/kernel/view-object_package_map.sql

-- @@ default/kernel/index-acs_stylesheets.sql
-- @@ default/kernel/index-acs_stylesheet_type_map.sql
-- @@ default/kernel/index-acs_stylesheet_node_map.sql
@@ default/kernel/function-package_id_for_object_id.sql

@@ default/kernel/constraint-email_addresses.sql
@@ default/kernel/constraint-group_subgroup_map.sql
@@ default/kernel/constraint-site_nodes.sql
@@ default/kernel/constraint-roles.sql

@@ default/categorization/index-cat_cat_deflt_ancestors.sql
@@ default/categorization/comment-cat_categories.sql
@@ default/categorization/table-cat_category_category_map.sql
@@ default/categorization/comment-cat_category_category_map.sql
@@ default/categorization/table-cat_object_category_map.sql
@@ default/categorization/index-cat_object_category_map.sql
@@ default/categorization/comment-cat_object_category_map.sql
@@ default/categorization/table-cat_object_root_category_map.sql
@@ default/categorization/index-cat_object_root_category_map.sql
@@ default/categorization/table-cat_purposes.sql
@@ default/categorization/comment-cat_purposes.sql
@@ default/categorization/table-cat_category_purpose_map.sql
@@ default/categorization/index-cat_category_purpose_map.sql
@@ default/categorization/table-cat_cat_subcat_trans_index.sql
@@ default/categorization/index-cat_cat_subcat_trans_index.sql
@@ default/categorization/trigger-cat_category_category_map.sql
@@ default/categorization/insert-acs_privileges.sql
@@ default/categorization/index-cat_root_cat_object_map.sql

@@ oracle-se/auditing/table-acs_auditing.sql
@@ default/auditing/index-acs_auditing.sql

@@ oracle-se/messaging/table-messages.sql
@@ default/messaging/index-messages.sql
@@ default/messaging/comment-messages.sql
@@ default/messaging/table-message_parts.sql
@@ default/messaging/index-message_parts.sql
@@ default/messaging/comment-message_parts.sql
@@ oracle-se/messaging/table-message_threads.sql

@@ oracle-se/notification/table-nt_digests.sql
@@ default/notification/index-nt_digests.sql
@@ oracle-se/notification/table-nt_requests.sql
@@ default/notification/index-nt_requests.sql
@@ default/notification/table-nt_queue.sql

-- Oracle Intermedia
-- @@ default/search/table-search_content.sql
-- @@ default/search/block-autogroup.sql
-- @@ default/search/index-xml_content_index.sql
-- @@ default/search/index-raw_content_index.sql
-- @@ default/search/table-content_change_time.sql
-- @@ default/search/table-search_indexing_jobs.sql
-- @@ default/search/insert-dummy.sql
-- @@ default/search/package-search_indexing.sql

@@ default/versioning/table-vc_objects.sql
@@ default/versioning/comment-vc_objects.sql
@@ oracle-se/versioning/table-vc_transactions.sql
@@ default/versioning/index-vc_transactions.sql
@@ default/versioning/comment-vc_transactions.sql
@@ default/versioning/table-vc_actions.sql
@@ default/versioning/comment-vc_actions.sql
@@ default/versioning/insert-vc_actions.sql
@@ default/versioning/table-vc_operations.sql
@@ default/versioning/index-vc_operations.sql
@@ default/versioning/comment-vc_operations.sql
@@ default/versioning/table-vc_generic_operations.sql
@@ default/versioning/comment-vc_generic_operations.sql

@@ default/versioning/index-vcx_obj_changes_txn_id_idx.sql
@@ default/versioning/index-vcx_operations_change_id_idx.sql
@@ default/versioning/index-vcx_operations_class_id_idx.sql
@@ default/versioning/index-vcx_operations_evnt_typ_id_idx.sql
@@ default/versioning/index-vcx_tags_txn_id_idx.sql
@@ default/versioning/index-vcx_txns_modifying_user_idx.sql

@@ default/versioning/insert-vcx_event_types.sql
@@ default/versioning/insert-vcx_java_classes.sql
@@ default/versioning/sequence-vcx_txns_id_seq.sql
@@ default/versioning/sequence-vcx_id_seq.sql

@@ default/workflow/sequence-cw_sequences.sql
@@ default/workflow/table-cw_tasks.sql
@@ default/workflow/index-cw_tasks.sql
@@ oracle-se/workflow/table-cw_user_tasks.sql
@@ default/workflow/index-cw_task_dependencies.sql
@@ oracle-se/workflow/table-cw_task_comments.sql
@@ default/workflow/index-cw_task_comments.sql
@@ default/workflow/table-cw_system_tasks.sql
@@ default/workflow/index-cw_task_listeners.sql
@@ default/workflow/index-cw_task_listeners_tid_ltid.sql
@@ default/workflow/index-cw_task_user_assignees.sql
@@ default/workflow/index-cw_task_group_assignees.sql
@@ default/workflow/table-cw_processes.sql
@@ default/workflow/index-cw_processes.sql
@@ default/workflow/table-cw_process_definitions.sql

@@ default/formbuilder/table-bebop_components.sql
@@ default/formbuilder/comment-bebop_components.sql
@@ default/formbuilder/table-bebop_widgets.sql
@@ default/formbuilder/comment-bebop_widgets.sql
@@ default/formbuilder/table-bebop_options.sql
@@ default/formbuilder/comment-bebop_options.sql
@@ default/formbuilder/table-bebop_form_sections.sql
@@ default/formbuilder/comment-bebop_form_sections.sql
@@ default/formbuilder/table-bebop_process_listeners.sql
@@ default/formbuilder/comment-bebop_process_listeners.sql
@@ default/formbuilder/table-bebop_form_process_listeners.sql
@@ default/formbuilder/comment-bebop_form_process_listeners.sql
@@ default/formbuilder/table-bebop_component_hierarchy.sql
@@ default/formbuilder/comment-bebop_component_hierarchy.sql
@@ default/formbuilder/table-bebop_listeners.sql
@@ default/formbuilder/comment-bebop_listeners.sql
@@ default/formbuilder/table-bebop_listener_map.sql
@@ default/formbuilder/comment-bebop_listener_map.sql
@@ default/formbuilder/table-bebop_object_type.sql
@@ default/formbuilder/comment-bebop_object_type.sql
@@ default/formbuilder/table-bebop_meta_object.sql
@@ default/formbuilder/comment-bebop_meta_object.sql
@@ default/formbuilder/table-forms_widget_label.sql
@@ default/formbuilder/index-forms_widget_label.sql
@@ default/formbuilder/comment-forms_widget_label.sql
@@ default/formbuilder/table-forms_lstnr_conf_email.sql
@@ default/formbuilder/comment-forms_lstnr_conf_email.sql
@@ default/formbuilder/table-forms_lstnr_conf_redirect.sql
@@ default/formbuilder/comment-forms_lstnr_conf_redirect.sql
@@ default/formbuilder/table-forms_lstnr_simple_email.sql
@@ default/formbuilder/comment-forms_lstnr_simple_email.sql
@@ default/formbuilder/table-forms_lstnr_tmpl_email.sql
@@ default/formbuilder/comment-forms_lstnr_tmpl_email.sql
@@ default/formbuilder/table-forms_lstnr_xml_email.sql
@@ default/formbuilder/comment-forms_lstnr_xml_email.sql
@@ default/formbuilder/table-forms_dataquery.sql
@@ default/formbuilder/comment-forms_dataquery.sql
@@ default/formbuilder/table-forms_dd_select.sql
@@ default/formbuilder/index-forms_dd_select.sql
@@ default/formbuilder/comment-forms_dd_select.sql
@@ default/formbuilder/sequence-forms_unique_id_seq.sql
@@ default/formbuilder/comment-forms_unique_id_seq.sql

@@ default/persistence/table-persistence_dynamic_ot.sql
@@ default/persistence/table-persistence_dynamic_assoc.sql

@@ default/portal/index-portlets.sql

@@ default/web/index-applications.sql
@@ default/web/index-application_types.sql
@@ default/web/index-application_type_privilege_map.sql

@@ oracle-se/lucene/proc-update-dirty.sql
@@ oracle-se/lucene/trigger-sync_lucene_docs.sql

@@ default/kernel/index-foreign_keys.sql

-- Oracle INSO filtering
-- @@ oracle-se/mimetypes/index-convert_to_html_index.sql
-- @@ oracle-se/mimetypes/function-convert_to_html.sql

@@ default/mimetypes/insert-cms_mime_status.sql

@@ ddl/oracle-se/deferred.sql

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
-- $Id: postgres-create.sql 725 2005-08-23 16:56:33Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

begin;

\i postgres/oracle-compatibility.sql

\i default/function-currentDate.sql

\i ddl/postgres/create.sql
\i default/globalization/table-g11n_charsets.sql
\i default/globalization/index-g11n_charsets.sql
\i default/globalization/table-g11n_locales.sql
\i default/globalization/index-g11n_locales.sql
\i default/globalization/table-g11n_locale_charset_map.sql
\i default/globalization/index-g11n_locale_charset_map.sql
\i postgres/globalization/table-g11n_catalogs.sql
\i default/globalization/index-g11n_catalogs.sql



\i default/preferences/table-preferences.sql
\i default/preferences/index-preferences.sql
\i default/preferences/comment-preferences.sql



\i default/kernel/sequence-acs_object_id_seq.sql
\i postgres/kernel/package-hierarchy_denormalization.sql

\i default/kernel/table-group_subgroup_trans_index.sql
\i default/kernel/index-group_subgroup_trans_index.sql
\i default/kernel/table-group_member_trans_index.sql
\i default/kernel/index-group_member_trans_index.sql
\i postgres/kernel/package-parties_denormalization.sql
\i postgres/kernel/trigger-acs_parties.sql
\i default/kernel/view-group_subgroup_trans_map.sql
\i default/kernel/view-group_member_trans_map.sql
\i default/kernel/view-party_member_trans_map.sql
\i default/kernel/index-party_email_map.sql
\i default/kernel/index-users.sql
\i default/kernel/index-user_authentication.sql
\i default/kernel/index-apm_package_type_listener_map.sql

\i default/kernel/index-group_member_map_group_id_idx.sql
\i default/kernel/index-group_subgroup_map_grp_id_idx.sql

\i default/kernel/table-acs_privilege_hierarchy.sql
\i default/kernel/index-acs_privilege_hierarchy.sql
\i default/kernel/table-dnm_privileges.sql
\i default/kernel/comment-dnm_privileges.sql
\i default/kernel/table-dnm_privilege_col_map.sql
\i default/kernel/comment-dnm_privilege_col_map.sql
\i default/kernel/table-dnm_privilege_hierarchy_map.sql
\i default/kernel/table-dnm_privilege_hierarchy.sql
\i default/kernel/comment-dnm_privilege_hierarchy.sql

\i postgres/kernel/package-dnm_privileges.sql
\i postgres/kernel/triggers-dnm_privileges.sql
\i default/kernel/table-dnm_permissions.sql
\i default/kernel/comment-dnm_permissions.sql
\i default/kernel/index-dnm_permissions.sql

\i default/kernel/insert-privileges.sql

\i default/kernel/insert-object_zero.sql

\i default/kernel/table-dnm_object_1_granted_context.sql
\i default/kernel/table-dnm_object_grants.sql
\i default/kernel/table-dnm_granted_context.sql
\i postgres/kernel/table-dnm_ungranted_context.sql

\i postgres/kernel/index-dnm_object_1_granted_context.sql
\i postgres/kernel/index-dnm_granted_context.sql
\i postgres/kernel/index-dnm_ungranted_context.sql

\i postgres/kernel/package-dnm_context.sql
\i default/kernel/insert-dnm_context.sql
\i postgres/kernel/triggers-dnm_context.sql

\i default/kernel/table-dnm_group_membership.sql
\i postgres/kernel/index-dnm_group_membership.sql
\i default/kernel/table-dnm_party_grants.sql

\i postgres/kernel/package-dnm_parties.sql
\i default/kernel/insert-dnm_group_membership.sql
\i postgres/kernel/triggers-dnm_parties.sql

\i postgres/kernel/stats-dnm_tables.sql

\i default/kernel/index-acs_permissions.sql

\i default/kernel/insert-users.sql
\i default/kernel/insert-groups.sql
\i default/kernel/insert-permissions.sql

\i postgres/kernel/function-package_id_for_object_id.sql
\i default/kernel/constraint-email_addresses.sql
\i default/kernel/constraint-group_subgroup_map.sql
\i default/kernel/constraint-site_nodes.sql
\i default/kernel/constraint-roles.sql

-- XXX
--\i default/kernel/view-object_package_map.sql

\i default/categorization/index-cat_cat_deflt_ancestors.sql
\i default/categorization/comment-cat_categories.sql
\i default/categorization/table-cat_category_category_map.sql
\i default/categorization/comment-cat_category_category_map.sql
\i default/categorization/table-cat_object_category_map.sql
\i default/categorization/index-cat_object_category_map.sql
\i default/categorization/comment-cat_object_category_map.sql
\i default/categorization/table-cat_object_root_category_map.sql
\i default/categorization/index-cat_object_root_category_map.sql
\i default/categorization/table-cat_purposes.sql
\i default/categorization/comment-cat_purposes.sql
\i default/categorization/table-cat_category_purpose_map.sql
\i default/categorization/index-cat_category_purpose_map.sql
\i default/categorization/table-cat_cat_subcat_trans_index.sql
\i postgres/categorization/trigger-cat_category_category_map.sql
\i default/categorization/insert-acs_privileges.sql
\i default/categorization/index-cat_cat_subcat_trans_index.sql
\i default/categorization/index-cat_root_cat_object_map.sql

\i default/auditing/table-acs_auditing.sql
\i default/auditing/index-acs_auditing.sql

\i default/messaging/table-messages.sql
\i default/messaging/index-messages.sql
\i default/messaging/comment-messages.sql
\i postgres/messaging/table-message_parts.sql
\i default/messaging/index-message_parts.sql
\i default/messaging/comment-message_parts.sql
\i default/messaging/table-message_threads.sql

\i default/notification/table-nt_digests.sql
\i default/notification/index-nt_digests.sql
\i default/notification/table-nt_requests.sql
\i default/notification/index-nt_requests.sql
\i default/notification/table-nt_queue.sql

-- Not a hope in hell of intermedia working with PG ;-)
--\i default/search/table-search_content.sql
--\i default/search/block-autogroup.sql
--\i default/search/index-xml_content_index.sql
--\i default/search/index-raw_content_index.sql
--\i default/search/table-content_change_time.sql
--\i default/search/table-search_indexing_jobs.sql
--\i default/search/insert-dummy.sql
--\i default/search/package-search_indexing.sql

\i default/versioning/table-vc_objects.sql
\i default/versioning/comment-vc_objects.sql
\i default/versioning/table-vc_transactions.sql
\i default/versioning/index-vc_transactions.sql
\i default/versioning/comment-vc_transactions.sql
\i default/versioning/table-vc_actions.sql
\i default/versioning/comment-vc_actions.sql
\i default/versioning/insert-vc_actions.sql
\i default/versioning/table-vc_operations.sql
\i default/versioning/index-vc_operations.sql
\i default/versioning/comment-vc_operations.sql
\i default/versioning/table-vc_generic_operations.sql
\i default/versioning/comment-vc_generic_operations.sql

\i default/versioning/index-vcx_obj_changes_txn_id_idx.sql
\i default/versioning/index-vcx_operations_change_id_idx.sql
\i default/versioning/index-vcx_operations_class_id_idx.sql
\i default/versioning/index-vcx_operations_evnt_typ_id_idx.sql
\i default/versioning/index-vcx_tags_txn_id_idx.sql
\i default/versioning/index-vcx_txns_modifying_user_idx.sql

\i default/versioning/insert-vcx_event_types.sql
\i default/versioning/insert-vcx_java_classes.sql
\i default/versioning/sequence-vcx_txns_id_seq.sql
\i default/versioning/sequence-vcx_id_seq.sql

\i postgres/workflow/sequence-cw_sequences.sql
\i default/workflow/table-cw_tasks.sql
\i default/workflow/index-cw_tasks.sql
\i default/workflow/table-cw_user_tasks.sql
\i default/workflow/index-cw_task_dependencies.sql
\i default/workflow/table-cw_task_comments.sql
\i default/workflow/index-cw_task_comments.sql
\i default/workflow/table-cw_system_tasks.sql
\i default/workflow/index-cw_task_listeners.sql
\i default/workflow/index-cw_task_listeners_tid_ltid.sql
\i default/workflow/index-cw_task_user_assignees.sql
\i default/workflow/index-cw_task_group_assignees.sql
\i default/workflow/table-cw_processes.sql
\i default/workflow/index-cw_processes.sql
\i default/workflow/table-cw_process_definitions.sql

\i default/formbuilder/table-bebop_components.sql
\i default/formbuilder/comment-bebop_components.sql
\i default/formbuilder/table-bebop_widgets.sql
\i default/formbuilder/comment-bebop_widgets.sql
\i default/formbuilder/table-bebop_options.sql
\i default/formbuilder/comment-bebop_options.sql
\i default/formbuilder/table-bebop_form_sections.sql
\i default/formbuilder/comment-bebop_form_sections.sql
\i default/formbuilder/table-bebop_process_listeners.sql
\i default/formbuilder/comment-bebop_process_listeners.sql
\i default/formbuilder/table-bebop_form_process_listeners.sql
\i default/formbuilder/comment-bebop_form_process_listeners.sql
\i default/formbuilder/table-bebop_component_hierarchy.sql
\i default/formbuilder/comment-bebop_component_hierarchy.sql
\i default/formbuilder/table-bebop_listeners.sql
\i default/formbuilder/comment-bebop_listeners.sql
\i default/formbuilder/table-bebop_listener_map.sql
\i default/formbuilder/comment-bebop_listener_map.sql
\i default/formbuilder/table-bebop_object_type.sql
\i default/formbuilder/comment-bebop_object_type.sql
\i default/formbuilder/table-bebop_meta_object.sql
\i default/formbuilder/comment-bebop_meta_object.sql
\i default/formbuilder/table-forms_widget_label.sql
\i default/formbuilder/comment-forms_widget_label.sql
\i default/formbuilder/table-forms_lstnr_conf_email.sql
\i default/formbuilder/comment-forms_lstnr_conf_email.sql
\i default/formbuilder/table-forms_lstnr_conf_redirect.sql
\i default/formbuilder/comment-forms_lstnr_conf_redirect.sql
\i default/formbuilder/table-forms_lstnr_simple_email.sql
\i default/formbuilder/comment-forms_lstnr_simple_email.sql
\i default/formbuilder/table-forms_lstnr_tmpl_email.sql
\i default/formbuilder/comment-forms_lstnr_tmpl_email.sql
\i default/formbuilder/table-forms_lstnr_xml_email.sql
\i default/formbuilder/comment-forms_lstnr_xml_email.sql
\i default/formbuilder/table-forms_dataquery.sql
\i default/formbuilder/comment-forms_dataquery.sql
\i default/formbuilder/table-forms_dd_select.sql
\i default/formbuilder/comment-forms_dd_select.sql
\i default/formbuilder/sequence-forms_unique_id_seq.sql
\i default/formbuilder/comment-forms_unique_id_seq.sql
\i default/formbuilder/index-forms_widget_label.sql
\i default/formbuilder/index-forms_dd_select.sql

\i postgres/persistence/table-persistence_dynamic_ot.sql
\i postgres/persistence/table-persistence_dynamic_assoc.sql

\i default/kernel/index-foreign_keys.sql 

\i default/portal/index-portlets.sql
\i default/web/index-applications.sql
\i default/web/index-application_types.sql
\i default/web/index-application_type_privilege_map.sql


\i postgres/lucene/proc-update-dirty.sql
\i postgres/lucene/trigger-sync_lucene_docs.sql

\i default/mimetypes/insert-cms_mime_status.sql

\i ddl/postgres/deferred.sql

commit;

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
-- $Id: oracle-se-create.sql 1305 2006-09-01 08:54:11Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

@@ ddl/oracle-se/create.sql

@@ default/content-section/table-cms_privileges.sql
@@ default/content-section/insert-privileges.sql
@@ default/content-section/insert-acs_privilege_hierarchy.sql
@@ default/content-section/index-phase_def_cycle_definition_idx.sql
@@ default/content-section/index-lifecycles_definition_id_idx.sql
@@ default/content-section/index-phases_definition_id_idx.sql
@@ default/content-section/index-phases_cycle_id_idx.sql
@@ default/content-section/index-acs_object_cycle_map_cycle_idx.sql
@@ default/content-section/table-publication_status.sql
@@ default/content-section/comment-publication_phases.sql
@@ default/content-section/table-content_types.sql
@@ default/content-section/comment-content_types.sql
@@ default/content-section/table-cms_items.sql
@@ default/content-section/index-cms_items_ancestors_idx.sql
@@ default/content-section/index-cms_items_parent_id_idx.sql
@@ default/content-section/index-cms_items_type_id_idx.sql
@@ default/content-section/index-cms_items_name_idx.sql
@@ default/content-section/index-cms_items_section_id_idx.sql
@@ default/content-section/index-cms_items_vmi_idx.sql
@@ default/content-section/index-cms_items_name_id_version_parent_idx.sql
@@ default/content-section/comment-cms_items.sql
@@ default/content-section/index-cms_folders_index_id_idx.sql
@@ default/content-section/comment-cms_folders.sql
@@ default/content-section/table-content_sections.sql
@@ default/content-section/index-content_sections_rt_folder_idx.sql
@@ default/content-section/index-content_sections_staff_grp_idx.sql
@@ default/content-section/index-content_sections_vwrs_grp_idx.sql
@@ default/content-section/index-content_sections_tp_folder_idx.sql
@@ default/content-section/comment-content_sections.sql
@@ default/content-section/comment-content_section_type_map.sql
@@ default/content-section/table-content_type_lifecycle_map.sql
@@ default/content-section/index-content_type_lc_type_idx.sql
@@ default/content-section/index-content_type_lc_cycle_def_idx.sql
@@ default/content-section/comment-content_type_lifecycle_map.sql
@@ default/content-section/table-content_type_workflow_map.sql
@@ default/content-section/index-content_type_wf_map_type_idx.sql
@@ default/content-section/index-content_type_wf_map_templa_idx.sql
@@ default/content-section/comment-content_type_workflow_map.sql
@@ default/content-section/table-cms_section_locales_map.sql
@@ default/content-section/index-section_locales_map_sectn_idx.sql
@@ default/content-section/index-section_locales_map_locale_idx.sql
@@ default/content-section/comment-cms_section_locales_map.sql
@@ default/content-section/table-authoring_kits.sql
@@ default/content-section/comment-authoring_kits.sql
@@ default/content-section/index-cms_assets_mime_type_idx.sql
@@ default/content-section/index-cms_text_pages_text_id_idx.sql
@@ default/content-section/table-cms_article_image_map.sql
@@ default/content-section/insert-cms_resource_types.sql
@@ default/content-section/index-cms_resources_type_idx.sql
@@ default/content-section/table-cms_resource_map.sql
@@ default/content-section/index-cms_resource_map_resource_idx.sql
@@ default/content-section/comment-cms_resource_map.sql
@@ default/content-section/table-cms_tasks.sql
@@ default/content-section/index-cms_tasks_task_type_id_idx.sql
@@ default/content-section/table-cms_wf_notifications.sql
@@ default/content-section/insert-task-types.sql
@@ default/content-section/table-cw_process_definitions.sql
@@ default/content-section/sequence-convert_format_seq.sql
@@ default/content-section/table-cms_templates.sql
@@ default/content-section/comment-cms_templates.sql
@@ default/content-section/index-cms_category_template_map.sql
@@ default/content-section/comment-cms_category_template_map.sql
@@ default/content-section/table-cms_template_use_contexts.sql
@@ default/content-section/comment-cms_template_use_contexts.sql
@@ default/content-section/insert-cms_template_use_contexts.sql
@@ default/content-section/table-cms_section_template_map.sql
@@ default/content-section/index-cms_stm_unique.sql
@@ default/content-section/comment-cms_section_template_map.sql
@@ default/content-section/table-cms_item_template_map.sql
@@ default/content-section/index-cms_itm_unique.sql
@@ default/content-section/comment-cms_item_template_map.sql
@@ default/content-section/table-cms_standalone_pages.sql
@@ default/content-section/comment-cms_standalone_pages.sql
@@ default/content-section/index-publish_to_fs_queue_inproc_idx.sql
@@ default/content-section/sequence-publish_to_file_system_seq.sql
@@ default/content-section/index-publish_to_fs_files_item_idx.sql
@@ default/content-section/index-publish_to_fs_files_draft_idx.sql
@@ default/content-section/table-publish_to_fs_links.sql
@@ default/content-section/index-publish_to_fs_links_source_idx.sql
@@ default/content-section/index-publish_to_fs_links_target_idx.sql
@@ default/content-section/table-publish_to_fs_notify_broken.sql
@@ default/content-section/index-akit_step_map_step_id_idx.sql
@@ default/content-section/index-cms_artcl_img_map_img_id_idx.sql
@@ default/content-section/index-cms_itm_tplt_map_tplt_id_idx.sql
@@ default/content-section/index-cms_item_tplt_map_use_ctx_idx.sql
@@ default/content-section/index-cms_sec_tplt_map_tplt_id_idx.sql
@@ default/content-section/index-cms_sec_tplt_map_typ_id_idx.sql
@@ default/content-section/index-cms_sec_tplt_map_use_ctx_idx.sql
@@ default/content-section/index-cms_stdlne_pgs_pg_id_idx.sql
@@ default/content-section/index-cms_stdlne_pgs_tplt_id_idx.sql
@@ default/content-section/index-cont_sec_typ_map_typ_id_idx.sql
@@ default/content-section/index-cont_typs_itm_frm_id_idx.sql
@@ default/content-section/index-sec_lc_def_map_cyc_def_id_idx.sql
@@ default/content-section/index-sec_wf_tplt_map_wf_tplt_id_idx.sql
@@ default/content-section/index-cms_top_level_pages.sql
@@ default/content-section/index-section_lifecycle_def_map.sql
@@ default/content-section/index-acs_object_cycl_map_itm_idx.sql
@@ default/content-section/index-cms_artcl_imag_map_art_id_idx.sql
@@ default/content-section/index-cms_ctgry_tmpl_map_sctn_id_idx.sql
@@ default/content-section/index-cms_ctgry_tmpl_map_tmpl_id_idx.sql
@@ default/content-section/index-cms_ctgry_tmpl_map_type_id_idx.sql
@@ default/content-section/index-cms_items_master_id_idx.sql
@@ default/content-section/index-content_sctn_typ_map_sctn_idx.sql
@@ default/content-section/index-ct_cnt_grp_it_map_grp_id_idx.sql
@@ default/content-section/index-ct_cnt_grp_it_map_rltd_itm_idx.sql
@@ default/content-section/index-cw_tsk_grp_assign_task_id_idx.sql
@@ default/content-section/index-publish_to_fs_files_hst_id_idx.sql
@@ default/content-section/index-publish_to_fs_queue_hst_id_idx.sql
@@ default/content-section/index-sctn_wrkflw_tmplt_map_sctn_idx.sql
@@ default/content-section/index-foreign_keys.sql
@@ default/portlet/table-portlet_content_item.sql
@@ default/portlet/index-portlet_content_itm_itm_id_idx.sql

@@ ddl/oracle-se/deferred.sql
@@ oracle-se/content-section/triggers-cms_items_ancestors.sql

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
-- $DateTime: 2004/08/17 23:15:09 $


--------------------------------------------------------------------------------
-- These columns went from 'not nullable' to 'nullable'.
--------------------------------------------------------------------------------
alter table CMS_ITEMS alter ANCESTORS drop not null;
alter table PUBLISH_TO_FS_QUEUE alter ITEM_ID drop not null;

--------------------------------------------------------------------------------
-- These columns went from 'nullable' to 'not nullable'.
--------------------------------------------------------------------------------
alter table CMS_RESOURCES alter TYPE set not null;
alter table PUBLISH_TO_FS_QUEUE alter ITEM_TYPE set not null;

--------------------------------------------------------------------------------
-- These default values for these columns changed.
--------------------------------------------------------------------------------
alter table CMS_MIME_TYPES alter JAVA_CLASS drop default;
alter table CMS_MIME_TYPES alter OBJECT_TYPE drop default;

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
update pg_constraint set conname = 'cms_article_article_id_p_s67nq' where UPPER(conname) = UPPER('cms_articles_pk');
update pg_constraint set conname = 'cms_folders_folder_id_p_oglqk' where UPPER(conname) = UPPER('cms_folders_pk');
update pg_constraint set conname = 'cms_ima_mim_typ_mim_ty_p_9jrgn' where UPPER(conname) = UPPER('cms_image_mime_types_pk');
update pg_constraint set conname = 'cms_item_template_map_pk' where UPPER(conname) = UPPER('cms_item_template_map_pkey');
update pg_constraint set conname = 'cms_mim_ext_fil_extens_p_pnyhk' where UPPER(conname) = UPPER('cms_mime_extensions_pk');
update pg_constraint set conname = 'cms_mim_type_mime_type_p_kl0ds' where UPPER(conname) = UPPER('cms_mime_types_pk');
update pg_constraint set conname = 'cms_resourc_resourc_id_p_034xh' where UPPER(conname) = UPPER('cms_resources_pk');
update pg_constraint set conname = 'cms_resourc_types_type_p_eo30h' where UPPER(conname) = UPPER('cms_resource_types_pk');
update pg_constraint set conname = 'cms_section_template_map_pk' where UPPER(conname) = UPPER('cms_section_template_map_pkey');
update pg_constraint set conname = 'cms_tex_mim_typ_mim_ty_p_3qbec' where UPPER(conname) = UPPER('cms_text_mime_types_pk');
update pg_constraint set conname = 'cms_text_pages_item_id_p_7tnky' where UPPER(conname) = UPPER('cms_text_pages_pk');
update pg_constraint set conname = 'publ_to_fs_fil_fil_nam_u_3zkgd' where UPPER(conname) = UPPER('publish_to_fs_files_un');
update pg_constraint set conname = 'publish_to_fs_files_id_p_j7xj1' where UPPER(conname) = UPPER('publish_to_fs_files_pk');

--------------------------------------------------------------------------------
-- These constraints have been added.
--------------------------------------------------------------------------------
ALTER TABLE acs_object_lifecycle_map ADD CONSTRAINT acs_obj_lif_map_cyc_id_f_hme4h FOREIGN KEY(cycle_id) references lifecycles(cycle_id);
ALTER TABLE acs_object_lifecycle_map ADD CONSTRAINT acs_obj_lif_map_ite_id_f_2cb3k FOREIGN KEY(item_id) references acs_objects(object_id);
ALTER TABLE authoring_kit_step_map ADD CONSTRAINT auth_kit_ste_map_kit_i_f_1mur9 FOREIGN KEY(kit_id) references authoring_kits(kit_id);
ALTER TABLE authoring_kit_step_map ADD CONSTRAINT auth_kit_ste_map_ste_i_f_z4lxs FOREIGN KEY(step_id) references authoring_steps(step_id);
ALTER TABLE authoring_kits ADD CONSTRAINT authoring_kits_kit_id_fk FOREIGN KEY(kit_id) references acs_objects(object_id);
ALTER TABLE authoring_kits ADD CONSTRAINT authoring_kits_type_id_fk FOREIGN KEY(type_id) references content_types(type_id);
ALTER TABLE authoring_kits ADD CONSTRAINT authoring_kits_type_id_un UNIQUE (type_id);
ALTER TABLE authoring_steps ADD CONSTRAINT authorin_steps_step_id_f_tm6xl FOREIGN KEY(step_id) references acs_objects(object_id);
ALTER TABLE cms_article_image_map ADD CONSTRAINT caim_article_id_fk FOREIGN KEY(article_id) references cms_articles(article_id);
ALTER TABLE cms_article_image_map ADD CONSTRAINT caim_image_id_fk FOREIGN KEY(image_id) references cms_images(image_id);
ALTER TABLE cms_article_image_map ADD CONSTRAINT cms_article_image_map_id_fk FOREIGN KEY(map_id) references cms_items(item_id);
ALTER TABLE cms_articles ADD CONSTRAINT cms_article_article_id_f_ekqk1 FOREIGN KEY(article_id) references cms_text_pages(item_id);
ALTER TABLE cms_assets ADD CONSTRAINT cms_assets_asset_id_f_mlsfs FOREIGN KEY(asset_id) references cms_items(item_id);
ALTER TABLE cms_assets ADD CONSTRAINT cms_assets_mime_type_f_cyiog FOREIGN KEY(mime_type) references cms_mime_types(mime_type);
ALTER TABLE cms_files ADD CONSTRAINT cms_files_file_id_f_oyuiz FOREIGN KEY(file_id) references cms_assets(asset_id);
ALTER TABLE cms_folders ADD CONSTRAINT cms_folders_folder_id_f_8p2ge FOREIGN KEY(folder_id) references cms_items(item_id);
ALTER TABLE cms_folders ADD CONSTRAINT cms_folders_index_id_f_b8p_0 FOREIGN KEY(index_id) references cms_items(item_id);
ALTER TABLE cms_form_item ADD CONSTRAINT cms_form_item_fk FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE cms_form_item ADD CONSTRAINT cms_form_item_frm_fk FOREIGN KEY(form_id) references bebop_form_sections(form_section_id) ON DELETE CASCADE;
ALTER TABLE cms_form_section_item ADD CONSTRAINT cms_form_section_item_frm_fk FOREIGN KEY(form_section_id) references bebop_form_sections(form_section_id) ON DELETE CASCADE;
ALTER TABLE cms_form_section_item ADD CONSTRAINT cms_form_section_item_id_fk FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE cms_image_mime_types ADD CONSTRAINT cms_ima_mim_typ_mim_ty_f_s0zsx FOREIGN KEY(mime_type) references cms_mime_types(mime_type);
ALTER TABLE cms_images ADD CONSTRAINT cms_images_image_id_f_70gz8 FOREIGN KEY(image_id) references cms_assets(asset_id);
ALTER TABLE cms_item_template_map ADD CONSTRAINT cms_itm_item_id_fk FOREIGN KEY(item_id) references cms_items(item_id);
ALTER TABLE cms_item_template_map ADD CONSTRAINT cms_itm_mapping_id_fk FOREIGN KEY(mapping_id) references acs_objects(object_id);
ALTER TABLE cms_item_template_map ADD CONSTRAINT cms_itm_template_id_fk FOREIGN KEY(template_id) references cms_templates(template_id);
ALTER TABLE cms_item_template_map ADD CONSTRAINT cms_itm_use_ctx_fx FOREIGN KEY(use_context) references cms_template_use_contexts(use_context);
ALTER TABLE cms_items ADD CONSTRAINT cms_items_item_id_fk FOREIGN KEY(item_id) references acs_objects(object_id);
ALTER TABLE cms_items ADD CONSTRAINT cms_items_parent_id_fk FOREIGN KEY(parent_id) references acs_objects(object_id);
ALTER TABLE cms_items ADD CONSTRAINT cms_items_type_id_fk FOREIGN KEY(type_id) references content_types(type_id);
ALTER TABLE cms_pages ADD CONSTRAINT cms_pages_item_id_f_gyfqx FOREIGN KEY(item_id) references cms_items(item_id);
ALTER TABLE cms_resource_map ADD CONSTRAINT cms_resource_map_section_id_fk FOREIGN KEY(section_id) references content_sections(section_id) ON DELETE CASCADE;
ALTER TABLE cms_resource_map ADD CONSTRAINT cms_resrc_map_resource_id_fk FOREIGN KEY(resource_id) references cms_resources(resource_id) ON DELETE CASCADE;
ALTER TABLE cms_resources ADD CONSTRAINT cms_resources_type_f_ic7i1 FOREIGN KEY(type) references cms_resource_types(type);
ALTER TABLE cms_section_locales_map ADD CONSTRAINT section_locales_locale_id_fk FOREIGN KEY(locale_id) references g11n_locales(locale_id) ON DELETE CASCADE;
ALTER TABLE cms_section_locales_map ADD CONSTRAINT section_locales_section_id_fk FOREIGN KEY(section_id) references content_sections(section_id) ON DELETE CASCADE;
ALTER TABLE cms_section_template_map ADD CONSTRAINT cms_stm_mapping_id_fk FOREIGN KEY(mapping_id) references acs_objects(object_id);
ALTER TABLE cms_section_template_map ADD CONSTRAINT cms_stm_section_id_fk FOREIGN KEY(section_id) references content_sections(section_id);
ALTER TABLE cms_section_template_map ADD CONSTRAINT cms_stm_template_id_fk FOREIGN KEY(template_id) references cms_templates(template_id);
ALTER TABLE cms_section_template_map ADD CONSTRAINT cms_stm_type_id_fk FOREIGN KEY(type_id) references content_types(type_id);
ALTER TABLE cms_section_template_map ADD CONSTRAINT cms_stm_use_ctx_fk FOREIGN KEY(use_context) references cms_template_use_contexts(use_context);
ALTER TABLE cms_standalone_pages ADD CONSTRAINT cms_std_page_id_fk FOREIGN KEY(page_id) references cms_pages(item_id);
ALTER TABLE cms_standalone_pages ADD CONSTRAINT cms_std_page_template_id_fk FOREIGN KEY(template_id) references cms_templates(template_id);
ALTER TABLE cms_tasks ADD CONSTRAINT cms_tasks_task_id_fk FOREIGN KEY(task_id) references cw_tasks(task_id);
ALTER TABLE cms_tasks ADD CONSTRAINT cms_tasks_type_id_fk FOREIGN KEY(task_type_id) references cms_task_types(task_type_id) ON DELETE CASCADE;
ALTER TABLE cms_templates ADD CONSTRAINT cms_templates_templ_id_fk FOREIGN KEY(template_id) references cms_text(text_id);
ALTER TABLE cms_text ADD CONSTRAINT cms_text_text_id_f_fwojq FOREIGN KEY(text_id) references cms_assets(asset_id);
ALTER TABLE cms_text_mime_types ADD CONSTRAINT cms_tex_mim_typ_mim_ty_f__tubf FOREIGN KEY(mime_type) references cms_mime_types(mime_type);
ALTER TABLE cms_text_pages ADD CONSTRAINT cms_text_pages_item_id_f_kfox7 FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE cms_text_pages ADD CONSTRAINT cms_text_pages_text_id_f_uri55 FOREIGN KEY(text_id) references cms_text(text_id);
ALTER TABLE cms_top_level_pages ADD CONSTRAINT cms_top_lev_pag_pag_id_f_a6bhw FOREIGN KEY(page_id) references cms_pages(item_id);
ALTER TABLE cms_top_level_pages ADD CONSTRAINT cms_top_lev_pag_tem_id_f_d26nu FOREIGN KEY(template_id) references cms_templates(template_id);
ALTER TABLE cms_user_defined_items ADD CONSTRAINT cms_use_def_ite_ite_id_f_b1yxo FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE content_section_type_map ADD CONSTRAINT cont_sec_typ_map_sec_i_f_f_tnl FOREIGN KEY(section_id) references content_sections(section_id);
ALTER TABLE content_section_type_map ADD CONSTRAINT cont_sec_typ_map_typ_i_f_z6u9r FOREIGN KEY(type_id) references content_types(type_id);
ALTER TABLE content_sections ADD CONSTRAINT csections_con_exp_dig_id_fk FOREIGN KEY(content_expiration_digest_id) references nt_digests(digest_id);
ALTER TABLE content_sections ADD CONSTRAINT csections_name_un UNIQUE (pretty_name);
ALTER TABLE content_sections ADD CONSTRAINT csections_root_folder_id_fk FOREIGN KEY(root_folder_id) references cms_folders(folder_id);
ALTER TABLE content_sections ADD CONSTRAINT csections_staff_group_id_fk FOREIGN KEY(staff_group_id) references groups(group_id);
ALTER TABLE content_sections ADD CONSTRAINT csections_temps_folder_id_fk FOREIGN KEY(templates_folder_id) references cms_folders(folder_id);
ALTER TABLE content_sections ADD CONSTRAINT csections_viewers_group_id_fk FOREIGN KEY(viewers_group_id) references groups(group_id);
ALTER TABLE content_type_lifecycle_map ADD CONSTRAINT ctlm_content_type_id_fk FOREIGN KEY(content_type_id) references content_types(type_id) ON DELETE CASCADE;
ALTER TABLE content_type_lifecycle_map ADD CONSTRAINT ctlm_cycle_def_id_fk FOREIGN KEY(cycle_definition_id) references lifecycle_definitions(definition_id) ON DELETE CASCADE;
ALTER TABLE content_type_lifecycle_map ADD CONSTRAINT ctlm_section_id_fk FOREIGN KEY(section_id) references content_sections(section_id) ON DELETE CASCADE;
ALTER TABLE content_type_workflow_map ADD CONSTRAINT ctwf_map_content_type_id_fk FOREIGN KEY(content_type_id) references content_types(type_id) ON DELETE CASCADE;
ALTER TABLE content_type_workflow_map ADD CONSTRAINT ctwf_map_section_id_fk FOREIGN KEY(section_id) references content_sections(section_id) ON DELETE CASCADE;
ALTER TABLE content_type_workflow_map ADD CONSTRAINT ctwf_map_wf_template_id_fk FOREIGN KEY(wf_template_id) references cw_process_definitions(process_def_id) ON DELETE CASCADE;
ALTER TABLE content_types ADD CONSTRAINT content_types_form_id_fk FOREIGN KEY(item_form_id) references bebop_components(component_id);
ALTER TABLE content_types ADD CONSTRAINT content_types_object_type_un UNIQUE (object_type);
ALTER TABLE content_types ADD CONSTRAINT content_types_type_id_fk FOREIGN KEY(type_id) references acs_objects(object_id);
ALTER TABLE ct_agendas ADD CONSTRAINT ct_agendas_item_id_f_410hq FOREIGN KEY(item_id) references cms_text_pages(item_id);
ALTER TABLE ct_articles ADD CONSTRAINT ct_articles_item_id_f_6ofn1 FOREIGN KEY(item_id) references cms_articles(article_id);
ALTER TABLE ct_events ADD CONSTRAINT ct_events_item_id_f_v7kjv FOREIGN KEY(item_id) references cms_text_pages(item_id);
ALTER TABLE ct_jobs ADD CONSTRAINT ct_jobs_item_id_f_zru4k FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE ct_legal_notices ADD CONSTRAINT ct_lega_notice_item_id_f_b3kkq FOREIGN KEY(item_id) references cms_text_pages(item_id);
ALTER TABLE ct_minutes ADD CONSTRAINT ct_minutes_item_id_f_8uhj5 FOREIGN KEY(item_id) references cms_text_pages(item_id);
ALTER TABLE ct_mp_articles ADD CONSTRAINT ct_mp_articl_articl_id_f_mz8ki FOREIGN KEY(article_id) references cms_pages(item_id);
ALTER TABLE ct_mp_sections ADD CONSTRAINT ct_mp_sectio_articl_id_f_ntnsj FOREIGN KEY(article_id) references ct_mp_articles(article_id);
ALTER TABLE ct_mp_sections ADD CONSTRAINT ct_mp_sectio_sectio_id_f_bx3ab FOREIGN KEY(section_id) references cms_pages(item_id);
ALTER TABLE ct_mp_sections ADD CONSTRAINT ct_mp_sections_image_f_evp8x FOREIGN KEY(image) references cms_images(image_id);
ALTER TABLE ct_mp_sections ADD CONSTRAINT ct_mp_sections_text_f_7mvon FOREIGN KEY(text) references cms_text(text_id);
ALTER TABLE ct_news ADD CONSTRAINT ct_news_item_id_f_mduh5 FOREIGN KEY(item_id) references cms_articles(article_id);
ALTER TABLE ct_press_releases ADD CONSTRAINT ct_pres_release_ite_id_f_77vpr FOREIGN KEY(item_id) references cms_text_pages(item_id);
ALTER TABLE ct_service ADD CONSTRAINT ct_service_item_id_f_gzgd8 FOREIGN KEY(item_id) references cms_pages(item_id);
ALTER TABLE lifecycle_definitions ADD CONSTRAINT life_definit_defini_id_f_ohxsm FOREIGN KEY(definition_id) references acs_objects(object_id);
ALTER TABLE lifecycles ADD CONSTRAINT lifecycle_definitio_id_f_52o2c FOREIGN KEY(definition_id) references lifecycle_definitions(definition_id);
ALTER TABLE lifecycles ADD CONSTRAINT lifecycles_cycle_id_f_hynpn FOREIGN KEY(cycle_id) references acs_objects(object_id);
ALTER TABLE phase_definitions ADD CONSTRAINT phas_defin_cyc_defi_id_f_z5qhs FOREIGN KEY(cycle_definition_id) references lifecycle_definitions(definition_id);
ALTER TABLE phase_definitions ADD CONSTRAINT phas_defin_pha_defi_id_f_oz08y FOREIGN KEY(phase_definition_id) references acs_objects(object_id);
ALTER TABLE phases ADD CONSTRAINT phases_cycle_id_f_pxrxc FOREIGN KEY(cycle_id) references lifecycles(cycle_id);
ALTER TABLE phases ADD CONSTRAINT phases_definition_id_f_lmb4y FOREIGN KEY(definition_id) references phase_definitions(phase_definition_id);
ALTER TABLE phases ADD CONSTRAINT phases_phase_id_f_kdkqu FOREIGN KEY(phase_id) references acs_objects(object_id);
ALTER TABLE publish_to_fs_links ADD CONSTRAINT publish_to_fs_links_source_fk FOREIGN KEY(source) references publish_to_fs_files(id) ON DELETE CASCADE;
ALTER TABLE publish_to_fs_links ADD CONSTRAINT publish_to_fs_links_target_fk FOREIGN KEY(target) references publish_to_fs_files(id) ON DELETE CASCADE;
ALTER TABLE publish_to_fs_links ADD CONSTRAINT publish_to_fs_links_un UNIQUE (source,target);
ALTER TABLE section_lifecycle_def_map ADD CONSTRAINT sect_lif_def_map_cyc_d_f_8xa1h FOREIGN KEY(cycle_definition_id) references lifecycle_definitions(definition_id);
ALTER TABLE section_lifecycle_def_map ADD CONSTRAINT sect_lif_def_map_sec_i_f_7si65 FOREIGN KEY(section_id) references content_sections(section_id);
ALTER TABLE section_workflow_template_map ADD CONSTRAINT sect_wor_tem_map_sec_i_f_9dekw FOREIGN KEY(section_id) references content_sections(section_id);
ALTER TABLE section_workflow_template_map ADD CONSTRAINT sect_wor_tem_map_wf_te_f_ne89i FOREIGN KEY(wf_template_id) references cw_process_definitions(process_def_id);

--------------------------------------------------------------------------------
-- These indexes have changed their name.
--------------------------------------------------------------------------------
update pg_class set relname = 'cms_article_article_id_p_s67nq' where UPPER(relname) = UPPER('cms_articles_pk');
update pg_class set relname = 'cms_folders_folder_id_p_oglqk' where UPPER(relname) = UPPER('cms_folders_pk');
update pg_class set relname = 'cms_ima_mim_typ_mim_ty_p_9jrgn' where UPPER(relname) = UPPER('cms_image_mime_types_pk');
update pg_class set relname = 'cms_item_template_map_pk' where UPPER(relname) = UPPER('cms_item_template_map_pkey');
update pg_class set relname = 'cms_mim_ext_fil_extens_p_pnyhk' where UPPER(relname) = UPPER('cms_mime_extensions_pk');
update pg_class set relname = 'cms_mim_type_mime_type_p_kl0ds' where UPPER(relname) = UPPER('cms_mime_types_pk');
update pg_class set relname = 'cms_resourc_types_type_p_eo30h' where UPPER(relname) = UPPER('cms_resource_types_pk');
update pg_class set relname = 'cms_resourc_resourc_id_p_034xh' where UPPER(relname) = UPPER('cms_resources_pk');
update pg_class set relname = 'cms_section_template_map_pk' where UPPER(relname) = UPPER('cms_section_template_map_pkey');
update pg_class set relname = 'cms_tex_mim_typ_mim_ty_p_3qbec' where UPPER(relname) = UPPER('cms_text_mime_types_pk');
update pg_class set relname = 'cms_text_pages_item_id_p_7tnky' where UPPER(relname) = UPPER('cms_text_pages_pk');
update pg_class set relname = 'publish_to_fs_files_id_p_j7xj1' where UPPER(relname) = UPPER('publish_to_fs_files_pk');

--------------------------------------------------------------------------------
-- These indexes have been added.
--------------------------------------------------------------------------------
create index acs_object_cycl_map_itm_idx on acs_object_lifecycle_map(item_id);
create index cms_artcl_imag_map_art_id_idx on cms_article_image_map(article_id);
create index cms_ctgry_tmpl_map_sctn_id_idx on cms_category_template_map(section_id);
create index cms_ctgry_tmpl_map_tmpl_id_idx on cms_category_template_map(template_id);
create index cms_ctgry_tmpl_map_type_id_idx on cms_category_template_map(type_id);
create index cms_form_item_form_id_idx on cms_form_item(form_id);
create index cms_form_section_item_fsi_idx on cms_form_section_item(form_section_id);
create index cms_frm_sctn_wrpr_frm_sctn_idx on cms_form_section_wrapper(form_section_id);
create index cms_items_master_id_idx on cms_items(master_id);
create index cms_top_lev_pag_templat_id_idx on cms_top_level_pages(template_id);
create index content_sctn_typ_map_sctn_idx on content_section_type_map(section_id);
create index ct_cnt_grp_it_map_grp_id_idx on ct_content_group_item_map(group_id);
create index ct_cnt_grp_it_map_rltd_itm_idx on ct_content_group_item_map(related_item_id);
create index ct_itm_file_attchmnts_ownr_idx on ct_item_file_attachments(owner_id);
create index ct_mp_sectio_image_idx on ct_mp_sections(image);
create index ct_mp_sectio_text_idx on ct_mp_sections(text);
create index cw_tsk_grp_assign_task_id_idx on cw_task_group_assignees(task_id);
create index portlet_content_itm_itm_id_idx on portlet_content_item(item_id);
create index publ_to_fs_fil_fil_nam_u_3zkgd on publish_to_fs_files(file_name);
create index publish_to_fs_files_hst_id_idx on publish_to_fs_files(host_id);
create index publish_to_fs_queue_hst_id_idx on publish_to_fs_queue(host_id);
create index sctn_wrkflw_tmplt_map_sctn_idx on section_workflow_template_map(section_id);
create index sect_lif_def_map_sect_id_idx on section_lifecycle_def_map(section_id);

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
-- $DateTime: 2004/08/17 23:15:09 $

alter index acs_object_life_cycle_map_pk rename to acs_obj_lif_map_obj_id_p_sa43c;
alter index authoring_kit_step_map_pk rename to auth_kit_ste_map_kit_i_p_pcd9g;
alter index authoring_steps_pk rename to authorin_steps_step_id_p_ey0as;
alter index cms_assets_pk rename to cms_assets_asset_id_p_obvpa;
alter index cms_files_pk rename to cms_files_file_id_p_pqgn1;
alter index cms_images_pk rename to cms_images_image_id_p_mcjck;
alter index cms_pages_pk rename to cms_pages_item_id_p_rnee1;
alter index cms_text_pk rename to cms_text_text_id_p_o7aps;
alter index cms_user_defined_item_pk rename to cms_use_def_ite_ite_id_p_q7olx;
alter index cms_variant_tags_pk rename to cms_variant_tags_tag_p_vocdw;
alter index ct_agendas_item_id_pk rename to ct_agendas_item_id_p_0h5n7;
alter index ct_articles_pk rename to ct_articles_item_id_p_2be9i;
alter index ct_jobs_item_id_pk rename to ct_jobs_item_id_p_dzhl5;
alter index ct_legal_notices_item_id_pk rename to ct_lega_notice_item_id_p_g6apo;
alter index ct_minutes_item_id_pk rename to ct_minutes_item_id_p_2wwcp;
alter index ct_mp_articles_item_id_pk rename to ct_mp_articl_articl_id_p_pwnmf;
alter index ct_mp_sections_section_id_pk rename to ct_mp_sectio_sectio_id_p_ucvcv;
alter index ct_news_item_id_pk rename to ct_news_item_id_p_l09nd;
alter index ct_press_releases_item_id_pk rename to ct_pres_release_ite_id_p_u0jpn;
alter index ct_service_item_id_pk rename to ct_service_item_id_p_9kbpw;
alter index lifecycle_definitions_pk rename to life_definit_defini_id_p_02z4p;
alter index lifecycles_pk rename to lifecycles_cycle_id_p_8jyyq;
alter index phase_definitions_pk rename to phas_defin_pha_defi_id_p_3ir6h;
alter index phases_pk rename to phases_phase_id_p_oj6k5;
alter index section_lifecycle_def_map_pk rename to sect_lif_def_map_cyc_d_p_5lrl6;

create index cms_cat_ind_ite_map_itm_id_idx on cms_category_index_item_map(item_id);
create index cms_form_item_form_id_idx on cms_form_item(form_id);
create index cms_form_section_item_fsi_idx on cms_form_section_item(form_section_id);
create index cms_top_lev_pag_templat_id_idx on cms_top_level_pages(template_id);
create index ct_mp_sectio_image_idx on ct_mp_sections(image);
create index ct_mp_sectio_text_idx on ct_mp_sections(text);
create index sect_lif_def_map_sect_id_idx on section_lifecycle_def_map(section_id);

drop index publish_to_fs_queue_parent_idx;

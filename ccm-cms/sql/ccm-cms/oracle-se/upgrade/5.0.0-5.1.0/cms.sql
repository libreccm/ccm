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
-- $Id: cms.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

create index akit_step_map_step_id_idx on authoring_kit_step_map(step_id);
create index cms_artcl_img_map_img_id_idx on cms_article_image_map(image_id);
create index cms_itm_tplt_map_tplt_id_idx on cms_item_template_map(template_id);
create index cms_item_tplt_map_use_ctx_idx on cms_item_template_map(use_context);
create index cms_sec_tplt_map_tplt_id_idx on cms_section_template_map(template_id);
create index cms_sec_tplt_map_typ_id_idx on cms_section_template_map(type_id);
create index cms_sec_tplt_map_use_ctx_idx on cms_section_template_map(use_context);
create index cms_stdlne_pgs_pg_id_idx on cms_standalone_pages(page_id);
create index cms_stdlne_pgs_tplt_id_idx on cms_standalone_pages(template_id);
create index cont_sec_cont_exp_dgst_id_idx on content_sections(content_expiration_digest_id);
create index cont_sec_typ_map_typ_id_idx on content_section_type_map(type_id);
create index cont_typs_itm_frm_id_idx on content_types(item_form_id);
create index pub_to_fs_files_server_id_idx on publish_to_fs_files(server_id);
create index pub_to_fs_queue_server_id_idx on publish_to_fs_queue(server_id);
create index sec_lc_def_map_cyc_def_id_idx on section_lifecycle_def_map(cycle_definition_id);
create index sec_wf_tplt_map_wf_tplt_id_idx on section_workflow_template_map(wf_template_id);

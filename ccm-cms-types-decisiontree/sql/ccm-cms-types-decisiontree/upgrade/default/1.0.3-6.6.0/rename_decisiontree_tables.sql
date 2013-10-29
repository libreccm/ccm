--
-- Copyright (C) 2013 Jens Pelzetter All Rights Reserved.
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
-- $Id$

-- The content type decisiontree was orginally developed by/for the London Bourough of Camden.
-- Because this content type is not only useful the E-Government purposes, but also for example
-- for advice centres, for example in a university it has been decided to rename the content type
-- to ccm-cms-types-decisiontree and integrate the type into the main developement line. 
-- This upgrade script renames the database tables and their constraints according to the 
-- general naming conventions of CCM.

-- Rename tables
ALTER TABLE cam_decision_trees RENAME TO ct_decisiontrees;
ALTER TABLE cam_tree_sections RENAME TO ct_decisiontree_sections;
ALTER TABLE cam_section_options RENAME TO ct_decisiontree_section_options;cam_tre_section_tre_id_f_m_sh9
ALTER TABLE cam_option_targets RENAME TO ct_decisiontree_option_targets;

-- Rename constraints. Because it is not possible to rename constraints we drop the old ones 
-- and recreate them.
-- Difficult to figure names and order for droping and creating the constraint. Therefore we will
-- not rename the constraints.
-- ALTER TABLE ct_decisiontree_option_targets DROP CONSTRAINT cam_opt_tar_mat_option_f_fmueq RESTRICT;
-- ALTER TABLE ct_decisiontree_option_targets DROP CONSTRAINT cam_opt_tar_tar_sectio_f_fwfz3 RESTRICT;
-- ALTER TABLE ct_decisiontree_option_targets DROP CONSTRAINT cam_opti_targe_targ_id_f_jvtlm RESTRICT;

-- ALTER TABLE ct_decisiontree_section_options DROP CONSTRAINT cam_sect_optio_opti_id_f_l2tm2 RESTRICT;
-- ALTER TABLE ct_decisiontree_section_options DROP CONSTRAINT cam_sect_opti_secti_id_f_e8da0 RESTRICT;

-- ALTER TABLE ct_decisiontree_sections DROP CONSTRAINT cam_tre_sec_instructio_f_nce9c RESTRICT;
-- ALTER TABLE ct_decisiontree_sections DROP CONSTRAINT cam_tre_secti_secti_id_f_i2mrf RESTRICT;
-- ALTER TABLE ct_decisiontree_sections DROP CONSTRAINT cam_tre_section_tre_id_f_m_sh9 RESTRICT;

-- ALTER TABLE ct_decisiontrees DROP CONSTRAINT cam_dec_tre_fir_sectio_f_9jr7j RESTRICT;
-- ALTER TABLE ct_decisiontrees DROP CONSTRAINT cam_decisi_tree_tre_id_f_g0r8e RESTRICT;
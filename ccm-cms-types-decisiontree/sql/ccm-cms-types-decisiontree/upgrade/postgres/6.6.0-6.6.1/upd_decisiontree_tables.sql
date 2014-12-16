--
-- Copyright (C) 2014 Jens Pelzetter. All Rights Reserved.
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

-- Can't delete primary key constraint because of dependcies.
-- ALTER TABLE ONLY ct_decisiontree_section_options
--    DROP CONSTRAINT ct_deci_sec_opt_opt_id_p_0p52e;

ALTER TABLE ONLY ct_decisiontree_section_options
    DROP CONSTRAINT ct_deci_sec_opt_opt_id_f_ysyhm;

ALTER TABLE ONLY ct_decisiontree_section_options
    DROP CONSTRAINT ct_deci_sec_opt_sec_id_f_129bc;

ALTER TABLE ONLY ct_decisiontree_section_options 
    RENAME TO ct_decisiontree_section_opts;

-- ALTER TABLE ct_decisiontree_section_opts
--    ADD CONSTRAINT ct_deci_sec_opt_opt_id_p_5od37 PRIMARY KEY (option_id)

ALTER TABLE ct_decisiontree_section_opts 
    ADD CONSTRAINT ct_deci_sec_opt_opt_id_f_hb7ct 
    FOREIGN KEY (option_id) REFERENCES cms_items(item_id);

ALTER TABLE ct_decisiontree_section_opts 
    ADD CONSTRAINT ct_deci_sec_opt_sec_id_f_fczee 
    FOREIGN KEY (section_id) REFERENCES ct_decisiontree_sections(section_id);

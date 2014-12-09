--
-- Copyright (C) 2012 Jens Pelzetter. All Rights Reserved.
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
-- Adds the is_default column to the section_workflow_template_map table
--

ALTER TABLE section_workflow_template_map 
ADD is_default CHAR(1) DEFAULT '0' NOT NULL;
ALTER TABLE section_workflow_template_map 
ADD CONSTRAINT sect_wor_tem_map_is_de_c_0mfli check(is_default in ('0', '1'));
UPDATE section_workflow_template_map SET is_default = '0';
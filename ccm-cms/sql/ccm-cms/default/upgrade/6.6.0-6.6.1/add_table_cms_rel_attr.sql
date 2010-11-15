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
-- $Id: add_table_cms_rel_attr.sql pboy $

create table cms_relation_attribute (
    object_id integer NOT NULL,
    attribute character varying(100) NOT NULL,
    attr_key character varying(100) NOT NULL,
    lang character varying(2) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(500)
);

ALTER TABLE ONLY cms_relation_attribute
    ADD CONSTRAINT cms_rel_att_att_key_at_u_nh3g1 UNIQUE (attribute, attr_key, lang);

ALTER TABLE ONLY cms_relation_attribute
    ADD CONSTRAINT cms_rela_attrib_obj_id_p_qdgsr PRIMARY KEY (object_id);
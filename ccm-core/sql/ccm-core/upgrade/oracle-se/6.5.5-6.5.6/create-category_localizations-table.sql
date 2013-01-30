--
-- Copyright (C) 2008 pb@zes.uni-bremen.de    All Rights Reserved.
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

CREATE TABLE cat_category_localizations (
    id integer NOT NULL,
    locale character(2) NOT NULL,
    description character varying(4000),
    name character varying(200) NOT NULL,
    url character varying(200),
    enabled_p character(1) NOT NULL,
    category_id integer NOT NULL
);

ALTER TABLE cat_category_localizations
    ADD CONSTRAINT cat_cate_localizati_id_p_ancqs PRIMARY KEY (id);

ALTER TABLE cat_category_localizations
    ADD CONSTRAINT cat_cat_localiz_cat_id_f_ykbad FOREIGN KEY (category_id) REFERENCES cat_categories(category_id);

ALTER TABLE cat_category_localizations
    ADD CONSTRAINT cat_cate_localizati_id_f__leq0 FOREIGN KEY (id) REFERENCES acs_objects(object_id);



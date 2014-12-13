--
-- Copyright (C) 2008 Red Hat Inc. All Rights Reserved.
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
--

-- classohm: changed AtoZ to not hide a category when an alias has been 
--           defined for it; 
--           changed AtoZ to allow more than one alias to be defined for 
--           a category (version 1.0.5 / r2914, never officially released)
-- $Id: $
 
@@ default/add-cat_aliases.sql

insert into atoz_cat_aliases (object_id, provider_id, category_id, letter, title)
select acs_object_id_seq.nextval, m.provider_id, m.category_id, m.letter, m.title
from atoz_cat_alias_map m;

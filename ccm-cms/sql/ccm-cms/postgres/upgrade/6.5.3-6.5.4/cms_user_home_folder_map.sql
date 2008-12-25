--
-- Copyright (C) 2008 Peter Boy All Rights Reserved.
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
-- $Id: cms_user_home_folder_map.sql, 2008/12/25 12:09:59 pboy Exp $
-- $DateTime: 2004/08/17 23:15:09 $

-- in postgresql 8.3 a mixture of numeric and integer for reference keys
-- is no longer accepted-

ALTER TABLE cms_user_home_folder_map 
    ALTER COLUMN user_id    TYPE INTEGER ,
    ALTER COLUMN section_id TYPE INTEGER;





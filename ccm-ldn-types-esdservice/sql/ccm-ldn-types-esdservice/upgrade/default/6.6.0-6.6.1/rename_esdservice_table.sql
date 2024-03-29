-
-- Copyright (C) 2012 Peter Boy All Rights Reserved.
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
-- $Id: ren_esdservice_table.sql pboy $

-- rename ct_esdservice table to ct_ldn_esdservice table following
-- ccm naming conventions to make maintenance tasks easier


-- if we could figure out the old names we could rename constraints too
-- alter table ct_esdservice drop constraint ... ; 
-- alter table ct_esdservice drop constraint ... ; 
-- alter table ct_esdservice drop constraint ... ; 

ALTER TABLE ct_esdservice RENAME TO ct_ldn_esdservice ;




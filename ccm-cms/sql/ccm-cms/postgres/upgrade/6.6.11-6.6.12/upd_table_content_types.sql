--
-- Copyright (C) 2014 Peter Boy All Rights Reserved.
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
-- $Id: upd_table_content_types.sql pboy $

-- This update adjusts a notify to Oracle specific restrictions (mode is
-- reserved and connot used in Oracle for an identifier, changed to type_mode)
-- There exists an Postgresql version only, because there never had been an
-- Oracle db with that identifier.
-- This update is only applicable for the scientificcms tree up to version 2.2 
-- Don't use for the APLAWS tree! APLAWS is update just from 1.0.4 to 2.3.x 

ALTER TABLE content_types
    DROP CONSTRAINT content_types_mode_ck ;

ALTER TABLE content_types 
    RENAME COLUMN mode TO type_mode;

ALTER TABLE content_types
    ADD CONSTRAINT content_types_mode_ck 
    CHECK ( type_mode in ('D', 'H', 'I') );

-- Should not be necessary, rename shouldn't modify this
-- ALTER TABLE content_types
--     alter type_mode set default '0'::bpchar ;

-- ALTER TABLE content_types
--     alter type_mode set NOT NULL ;

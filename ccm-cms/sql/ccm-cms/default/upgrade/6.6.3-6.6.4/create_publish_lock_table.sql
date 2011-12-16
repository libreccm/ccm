--
-- Copyright (C) 2011 Jens Pelzetter All Rights Reserved.
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
-- $Id: create_publish_lock_table.sql pboy $



CREATE TABLE cms_publish_lock (
    lock_id integer NOT NULL,
    locked_oid character varying(2048),
    lock_timestamp timestamp with time zone,
    action character varying(256)
);

ALTER TABLE ONLY cms_publish_lock
    ADD CONSTRAINT cms_publis_loc_lock_id_p_8n7d0 PRIMARY KEY (lock_id);


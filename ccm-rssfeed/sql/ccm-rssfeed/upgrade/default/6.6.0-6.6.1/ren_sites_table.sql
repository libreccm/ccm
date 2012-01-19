--
-- Copyright (C) 2011 Peter Boy All Rights Reserved.
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
-- $Id: ren_sites_table.sql pboy $

-- rename table subsite_site to subsite_sites following ccm naming conventions
-- to make maintenance tasks easier


alter table rss_feeds drop constraint rss_feeds_feed_id_p_rm_i5 ; 
alter table rss_feeds drop constraint rss_feeds_feed_id_f_2lk3l ; 
alter table rss_feeds drop constraint rss_feeds_url_u_3ul6f ; 

alter table rss_feeds  RENAME TO  rssfeed_feeds ;

alter table rssfeed_feeds 
      add constraint rssfeed_feeds_feed_id_p_493us PRIMARY KEY (feed_id) ;
alter table rssfeed_feeds 
      add constraint rssfeed_feeds_feed_id_f_i4i5z FOREIGN KEY (feed_id) 
      REFERENCES acs_objects (object_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
alter table rssfeed_feeds 
      add constraint rssfeed_feeds_url_u_6xy5m UNIQUE(url) ;


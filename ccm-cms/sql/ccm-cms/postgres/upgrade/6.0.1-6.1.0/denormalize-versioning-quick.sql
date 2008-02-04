--
-- Copyright (C) 2006 Runtime Collective Ltd. All Rights Reserved.
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
-- $Id: denormalize-versioning.sql 285 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $




-- this is slightly quicker than the original upgrade script, for large databases.
-- quicker as in 13 seconds rather than 2+ days

insert into acs_auditing (
           object_id,
           creation_user,
           creation_date,
           creation_ip,
           modifying_user,
           last_modified,
           modifying_ip
)
select tt.itemId, t1.modifying_user, t1.timestamp, t1.modifying_ip, t2.modifying_user, t2.timestamp, t2.modifying_ip
from vcx_txns t1, vcx_txns t2, (select min(oc.txn_id) as minId, max(oc.txn_id) as maxId, i.item_id as itemId
                                from vcx_obj_changes oc, cms_items i
                                where oc.obj_id in (select o.object_type || ';id:1:' || o.object_id 
                                                    from acs_objects o
                                                    where o.object_id = i.item_id)
                                group by i.item_id) tt
where t1.id = tt.minId
and t2.id = tt.maxId;


--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: denormalize-versioning.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


create or replace function denormalizeVersinging() 
returns integer as '
declare
   newEntry                  refcursor;
   v_creation_user           integer;
   v_creation_ip             varchar(400);
   v_creation_timestamp      date;
   v_last_modified_user      integer;
   v_last_modifying_ip       varchar(400);
   v_last_modified_timestamp date;
   item record;
begin
      for item in 
          select item_id from cms_items
      loop

      OPEN newEntry FOR select modifying_user,
                   timestamp,
               modifying_ip
        from vcx_txns where id = 
           (select min(txn_id)
              from vcx_obj_changes 
             where obj_id in (select object_type || '';id:1:'' || object_id 
                                from acs_objects 
                               where object_id = item.item_id));
      FETCH newEntry INTO v_creation_user, v_creation_timestamp, v_creation_ip;
      CLOSE newEntry;

      OPEN newEntry FOR select modifying_user,
                   timestamp,
               modifying_ip
        from vcx_txns where id = 
           (select max(txn_id)
              from vcx_obj_changes 
             where obj_id in (select object_type || '';id:1:'' || object_id 
                                from acs_objects 
                               where object_id = item.item_id));
      FETCH newEntry INTO v_last_modified_user, 
                          v_last_modified_timestamp, 
                          v_last_modifying_ip;
      CLOSE newEntry;

      if (v_creation_user is not null and v_last_modified_user is not null) then
        insert into acs_auditing (
           object_id,
           creation_user,
           creation_date,
           creation_ip,
           modifying_user,
           last_modified,
           modifying_ip
        ) values (
           item.item_id,
           v_creation_user,
           v_creation_timestamp,
           v_creation_ip,
           v_last_modified_user,
           v_last_modified_timestamp,
           v_last_modifying_ip
        );    
      end if;       
   end loop;    
   return 1;
END; 
' LANGUAGE 'plpgsql';

select denormalizeVersinging();
drop function denormalizeVersinging();
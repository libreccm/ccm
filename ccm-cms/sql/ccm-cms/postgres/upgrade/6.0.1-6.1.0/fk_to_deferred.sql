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
-- $Id: fk_to_deferred.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $
create or replace function tmp_deffer_fk()
  returns integer as '
  declare 
    c record;
  begin 
    for c in select ''alter table '' || tab.relname || '' drop constraint '' || uc.conname as drop_cmd
	      from pg_constraint uc LEFT OUTER JOIN pg_class fktab ON (uc.confrelid = fktab.oid)
       	 , pg_class tab
	      WHERE uc.conrelid = tab.oid
	        and tab.relname in (''publish_to_fs_queue'', ''publish_to_fs_files'')
	        and uc.contype = ''f''
	        and fktab.relname = ''web_hosts''
   loop
      execute c.drop_cmd;
    end loop;
    return 1;
end; ' language 'plpgsql'
;

select tmp_deffer_fk();

alter table publish_to_fs_files add 
    constraint publi_to_fs_fil_hos_id_f_nwho1 foreign key (host_id)
      references web_hosts(host_id) deferrable initially deferred;

alter table publish_to_fs_queue add 
    constraint publi_to_fs_que_hos_id_f_9wfcg foreign key (host_id)
      references web_hosts(host_id) deferrable initially deferred;

drop function tmp_deffer_fk();

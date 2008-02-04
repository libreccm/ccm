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
-- $Id: upgrade-dnm_context.sql 285 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

insert into dnm_object_1_granted_context
(pd_object_id, pd_context_id, pd_non_effective_context_id)
select o.object_id, c.pd_context_id, 0
from acs_objects o, dnm_object_1_granted_context c
where o.object_id != 0
and o.object_id = c.pd_object_id;

begin

  for c in (select object_id, context_id 
            from object_context where object_id != 0 and context_id is not null and context_id != 0) loop
    dnm_context.change_context(c.object_id, c.context_id);
  end loop;

  for c in (select object_id from acs_permissions) loop
    dnm_context.add_grant(c.object_id);
  end loop;

end;
/

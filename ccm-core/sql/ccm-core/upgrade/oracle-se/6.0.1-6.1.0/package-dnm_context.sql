--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: package-dnm_context.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace package dnm_context as
   procedure add_object(p_object_id integer, p_context_id integer);
   procedure drop_object(p_object_id integer);
   procedure change_context(p_object_id integer, p_context_id integer);
   procedure add_grant(p_object_id integer);
   procedure remove_grant(p_object_id integer);
end dnm_context;
/
show errors


create or replace package body dnm_context as

   --------------------------------------
   -- Private procedures and functions --
   --------------------------------------
   function get_granted_context (p_object_id integer) return integer
   is
     v_context_id integer;
   begin
     if p_object_id is null then
        return 0;
     end if;

     select pd_context_id
       into v_context_id
       from dnm_object_1_granted_context
       where pd_object_id = p_object_id;
     return v_context_id;
   end;

   -------------------------------------
   -- Global procedures and functions --
   -------------------------------------
   procedure add_object(p_object_id integer, p_context_id integer)
   is
     v_context_id integer := get_granted_context(p_context_id);
     v_count integer;
   begin

     -- object just created , insert new entry into dnm_object_1_granted_context
     insert into dnm_object_1_granted_context  (pd_object_id, pd_context_id, pd_non_effective_context_id)
       values( p_object_id, v_context_id , nvl(p_context_id,0))
     ;

   end;

   procedure drop_object(p_object_id integer)
   is
     v_context_id integer := get_granted_context(p_object_id);
   begin
     delete dnm_granted_context where pd_context_id = p_object_id;
     delete dnm_granted_context where pd_object_id = p_object_id;
     delete dnm_object_grants where pd_object_id = p_object_id;
     delete dnm_object_1_granted_context where pd_object_id = p_object_id;
   end;

   procedure change_context(p_object_id integer, p_context_id integer)
   is
     v_old_context_id integer;
     v_effective_context integer := get_granted_context(p_context_id);
   begin

     select pd_context_id into v_old_context_id
       from dnm_object_1_granted_context where pd_object_id = p_object_id;

     if v_old_context_id != p_object_id then
       -- no direct grant on object

       update dnm_object_1_granted_context
         set pd_non_effective_context_id = nvl(p_context_id,0),
             pd_context_id = v_effective_context
         where pd_object_id = p_object_id;

       -- update non_granted children
       update dnm_object_1_granted_context /*+ INDEX (dnm_object_1_granted_context dnm_o1gc_obj_pk) */
         set pd_context_id = v_effective_context
         where pd_context_id = v_old_context_id
           and pd_object_id in (select /*+ INDEX(dnm_object_1_granted_context dnm_o1gc_necid_oid) */ pd_object_id
                               from dnm_object_1_granted_context
                               where pd_object_id != pd_context_id
                               start with pd_non_effective_context_id = p_object_id
                                 connect by prior pd_object_id = pd_non_effective_context_id)
       ;

       -- take care about granted children
       delete dnm_granted_context
         where pd_object_id in (select pd_object_id
                             from dnm_granted_context
                             -- where clause includes p_object_id itself because of dummy_row
                             where pd_context_id = v_old_context_id)
           and pd_context_id in (select pd_context_id
                                 from dnm_granted_context
                                 where pd_object_id = v_old_context_id)
           -- delete only non dummy rows and leave context_id = 0 rows
           and pd_dummy_flag = 0 and pd_context_id != 0
           -- restirct to p_object_id children, since p_object_id can be non granted
           and pd_object_id in (select /*+ INDEX(dnm_object_1_granted_context dnm_o1gc_necid_oid) */ pd_object_id
                               from dnm_object_1_granted_context
                               where pd_object_id = pd_context_id
                               start with pd_object_id = p_object_id
                                 connect by pd_non_effective_context_id = prior pd_object_id);

       insert into dnm_granted_context (pd_object_id, pd_context_id)
         select i2.pd_object_id, i1.pd_context_id
           from dnm_granted_context i1,
               (select /*+ INDEX(dnm_object_1_granted_context dnm_o1gc_necid_oid) */ pd_object_id
                  from dnm_object_1_granted_context
                  where pd_object_id = pd_context_id
                    start with pd_object_id = p_object_id
                    connect by pd_non_effective_context_id = prior pd_object_id) i2
           where i1.pd_object_id = v_effective_context
             and not exists (select 1 from dnm_granted_context
                                 where pd_object_id = i2.pd_object_id
                                 and pd_context_id = i1.pd_context_id);

      else
       -- object_id has direct grants
       -- update non_effective_context_id
       update dnm_object_1_granted_context
         set pd_non_effective_context_id = nvl(p_context_id,0)
         where pd_object_id = p_object_id;
       -- children of that object have either context_id = p_object_id,
       -- or context_id=object_id if they have direct grants
       -- we need to take care only about children with direct grants , i.e add missing paths

       -- 1) remove  old ancestors from  descendants for p_object_id.
       delete dnm_granted_context
         where pd_object_id in (select pd_object_id
                               from dnm_granted_context
                               -- where clause includes p_object_id itself because of dummy_row
                               where pd_context_id = p_object_id)
           and pd_context_id in (select pd_context_id
                                 from dnm_granted_context
                                 where pd_object_id = p_object_id and pd_dummy_flag = 0)
           -- delete only non dummy rows
           and pd_dummy_flag = 0;

       if v_effective_context = 0 then
         -- p_object_id and all of its should have 0 as an ancestor.
         insert into dnm_granted_context (pd_object_id, pd_context_id)
               select pd_object_id, 0
               from dnm_granted_context
               where pd_context_id = p_object_id;
       else
         -- Now insert new ancestors for descendants of p_object_id.
         insert into dnm_granted_context (pd_object_id, pd_context_id)
           select i2.pd_object_id, i1.pd_context_id
             from dnm_granted_context i1, dnm_granted_context i2
             where i1.pd_object_id = v_effective_context
               and i2.pd_context_id = p_object_id
               and not exists (select 1 from dnm_granted_context
                                 where pd_object_id = i2.pd_object_id
                                 and pd_context_id = i1.pd_context_id);
       end if;
     end if;
   end;

   procedure add_grant(p_object_id integer)
   is
     v_grants integer;
     v_old_context_id integer;
     s date;
   begin

     if p_object_id = 0 then
       -- we don`t wonna touch mother of all objects!
       return;
     end if;

     select count(*) into v_grants from dnm_object_grants where pd_object_id = p_object_id;

     if v_grants > 0 then
       update dnm_object_grants set pd_n_grants = pd_n_grants + 1 where pd_object_id = p_object_id;
       -- we dont need to do anything, since apropriate rows already exist in dnm_granted_context
       return;
     else
       insert into dnm_object_grants values (p_object_id, 1);

       -- insert dummy record
       insert into dnm_granted_context (pd_object_id, pd_context_id, pd_dummy_flag)
         values (p_object_id, p_object_id, 1);

       -- this is first grant for p_object_id, save old granted context
       select pd_context_id into v_old_context_id
         from dnm_object_1_granted_context where pd_object_id = p_object_id;

       -- update context for p_object_id
       update dnm_object_1_granted_context
        set pd_context_id = p_object_id
        where pd_object_id = p_object_id;

       -- update context for all ungranted children excluding subtrees starting with granted children
       update dnm_object_1_granted_context /*+ INDEX (dnm_object_1_granted_context dnm_o1gc_obj_pk) */
         set pd_context_id = p_object_id
         where pd_object_id in (select /*+ INDEX(dnm_object_1_granted_context dnm_o1gc_necid_oid) */ pd_object_id
                               from dnm_object_1_granted_context dn
                               -- exclude granted children with their children
                               where pd_context_id = v_old_context_id
                                 and pd_object_id != v_old_context_id
                               start with pd_non_effective_context_id = p_object_id
                                 connect by pd_non_effective_context_id = prior pd_object_id  )
       ;

       -- insert p_object_id as parent for granted children
       insert into dnm_granted_context (pd_object_id, pd_context_id)
            select /*+ INDEX(dnm_object_1_granted_context dnm_o1gc_necid_oid) */ pd_object_id, p_object_id
                     from dnm_object_1_granted_context
                     where pd_object_id = pd_context_id
                     start with pd_non_effective_context_id = p_object_id
                       connect by pd_non_effective_context_id = prior pd_object_id;

       -- since children of p_object_id have entries for parent objects
       -- i need to add parent objects for p_object_id only
       if v_old_context_id = 0 then
         -- 0 is the id of the security context root object
         insert into dnm_granted_context (pd_object_id, pd_context_id)
           values (p_object_id, 0);
       else
         insert into dnm_granted_context (pd_object_id, pd_context_id)
            select p_object_id, pd_context_id
              from dnm_granted_context
              -- that includes v_old_context_id as well becuase of dummy_row
              where pd_object_id = v_old_context_id
         ;
       end if;
     end if;

   end;

   procedure remove_grant(p_object_id integer)
   is
     v_grants integer;
     v_new_context_id integer;
   begin

     if p_object_id = 0 then
       return;
     end if;

     select pd_n_grants into v_grants from dnm_object_grants where pd_object_id = p_object_id for update;
     -- modify denormalization data only if there is no anymore grants on the object

     if v_grants > 1 then
       update dnm_object_grants set pd_n_grants = pd_n_grants -1 where pd_object_id = p_object_id;
     else

      -- find new effective context
       select pd_context_id into v_new_context_id
         from dnm_object_1_granted_context
         where pd_object_id = (select pd_non_effective_context_id
                              from dnm_object_1_granted_context
                              where pd_object_id = p_object_id);

       -- update context_id to new granted context id in
       update dnm_object_1_granted_context  dnm
         set pd_context_id = v_new_context_id
         -- that will update object and all appropriate children
         where pd_context_id = p_object_id;

       -- clean dnm_granted_context
       delete dnm_granted_context where pd_context_id = p_object_id;
       delete dnm_granted_context where pd_object_id = p_object_id;
       delete dnm_object_grants where pd_object_id = p_object_id;
     end if;
   end;

end;
/
show errors

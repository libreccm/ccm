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


create or replace function dnm_context_get_granted_context (
  integer )
  returns integer as '
  declare  
    p_object_id alias for $1;
    v_granted_context integer;
  begin
    if p_object_id is null then
       return 0;
    end if;

    select pd_context_id
      into v_granted_context
      from dnm_object_1_granted_context
      where pd_object_id = p_object_id;
    return v_granted_context;
  end;' language 'plpgsql'
;


create or replace function dnm_context_add_object (
  integer, integer) 
  returns integer as '
  declare 
    p_object_id alias for $1;
    p_context_id alias for $2;
    v_granted_context integer;
    v_context_id integer;
    v_count integer;
  begin
    v_granted_context :=  dnm_context_get_granted_context(p_context_id);
    v_context_id = coalesce(p_context_id, 0);
     
    -- object just created , insert new entry into dnm_object_1_granted_context
    insert into dnm_object_1_granted_context  (pd_object_id, pd_context_id, pd_non_effective_context_id)
      values( p_object_id, v_granted_context , coalesce(p_context_id,0))
    ;
    if (v_granted_context <> v_context_id) then
      insert into dnm_ungranted_context 
        (granted_context_id, object_id, ancestor_id, n_generations)
        values (v_granted_context, p_object_id, coalesce(p_context_id,0), 1)
      ;
    end if;

    insert into dnm_ungranted_context 
      (granted_context_id, object_id, ancestor_id, n_generations)
      select v_granted_context, p_object_id, ancestor_id, n_generations + 1 
	from dnm_ungranted_context where object_id = coalesce(p_context_id, 0)
    ;

    return null;

  end;' language 'plpgsql'
;

create or replace function dnm_context_drop_object (
  integer)
  returns integer as '
  declare
    p_object_id alias for $1;
    v_granted_context integer;
  begin
    v_granted_context := dnm_context_get_granted_context(p_object_id);

    delete from dnm_granted_context where pd_context_id = p_object_id;
    delete from dnm_granted_context where pd_object_id = p_object_id;
    delete from dnm_object_grants where pd_object_id = p_object_id;
    delete from dnm_object_1_granted_context where pd_object_id = p_object_id;
    delete from dnm_ungranted_context where object_id = p_object_id;
    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_context_add_grant (
  integer)
  returns integer as '
  declare
    p_object_id alias for $1;
    v_n_grants integer;
    v_old_context_id integer;
  begin

    -- get the number of grants on object if any, 
    -- and lock row to prevent incosistency in case of concurrent updates
    select pd_n_grants into v_n_grants
      from dnm_object_grants
      where pd_object_id = p_object_id 
      for update 
    ;

    if (v_n_grants > 0) then
      -- row exists, update counter and exit!
      update dnm_object_grants 
	set pd_n_grants = pd_n_grants + 1
        where pd_object_id = p_object_id
      ;
      return null;

    else 
      -- 1rs grant on the object
      -- add new row to dnm_object_grants
      insert into dnm_object_grants values (p_object_id, 1);

      -- insert dummy_row (selfmapping) into dnm_granted_context
      insert into dnm_granted_context (pd_object_id, pd_context_id, pd_dummy_flag)
        values (p_object_id, p_object_id, 1);
  
      -- save old granted context
      select pd_context_id into v_old_context_id
        from dnm_object_1_granted_context where pd_object_id = p_object_id;

      -- add ancectors for p_object_id in dnm_granted_context 
      insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select p_object_id, pd_context_id, 0
          from dnm_granted_context
          where pd_object_id = v_old_context_id;

      -- insert p_object_id as ancector for granted children
      insert into dnm_granted_context 
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select pd_object_id, p_object_id, 0
          from dnm_granted_context dgc 
          where pd_context_id in (select pd_context_id 
                                    from dnm_object_1_granted_context 
                                    where pd_non_effective_context_id in ( select p_object_id
                                                                           union all
		                                                           select object_id 
						                             from dnm_ungranted_context 
						                             where ancestor_id = p_object_id)
                                      and pd_object_id = pd_context_id)
      ;

      -- update grated context in dnm_object_1_granted_context for p_object_id to p_object_id
      update dnm_object_1_granted_context
        set pd_context_id = p_object_id
        where pd_object_id = p_object_id;

      -- update granted context in dnm_object_1_granted_context for all ungranted children where ancestor_id = object_id
      update dnm_object_1_granted_context 
         set pd_context_id = p_object_id
         where pd_object_id in (select object_id 
		                  from dnm_ungranted_context 
				  where ancestor_id = p_object_id)
      ;
      
      -- remove all p_object_id ancestors for objects where ancestor_id = p_object_id
      delete from dnm_ungranted_context 
        where object_id in (select duc.object_id 
		              from dnm_ungranted_context duc 
			      where duc.ancestor_id = p_object_id)
          and ancestor_id in (select duc1.ancestor_id 
		                from dnm_ungranted_context duc1
				where duc1.object_id = p_object_id)
      ;
      
      -- set granted_context for children to p_object_id
      update dnm_ungranted_context 
        set granted_context_id = p_object_id
        where object_id in ( select object_id 
                              from dnm_ungranted_context
			      where ancestor_id = p_object_id)
      ;

      -- delete all rows where ancestor_id = p_object_id 
      -- since they are implied in dnm_ungranted_context
      delete from dnm_ungranted_context 
        where ancestor_id = p_object_id;

      delete from dnm_ungranted_context 
        where object_id = p_object_id;

    end if;
    return 0;
  end;  ' language 'plpgsql'
;


create or replace function dnm_context_change_context (
  integer, integer) 
  returns integer as '
  declare
    p_object_id alias for $1;
    p_context_id alias for $2;
    v_context_id integer;
    v_new_granted_context integer;
    v_old_granted_context_id integer;
    v_old_context_id integer;
  begin

    v_context_id = coalesce(p_context_id,0);
    v_new_granted_context = dnm_context_get_granted_context(v_context_id);

    --save old context id
    select pd_context_id, pd_non_effective_context_id 
      into v_old_granted_context_id, v_old_context_id
      from dnm_object_1_granted_context 
      where pd_object_id = p_object_id
      for update
    ;

    if v_old_granted_context_id = p_object_id then
      -- p_object_id has grants on it!

      -- update non effective context_ids 
      update dnm_object_1_granted_context 
        set pd_non_effective_context_id = v_context_id
        where pd_object_id = p_object_id
      ;
      

      delete from dnm_granted_context 
        where pd_object_id in ( select pd_object_id 
		               from dnm_granted_context
			       where pd_context_id = p_object_id)
          and pd_context_id in ( select pd_context_id
                                from dnm_granted_context
			        where pd_object_id = p_object_id 
				  and pd_dummy_flag = 0)
          and pd_dummy_flag = 0
      ;

      -- add new ancestors to p_object_id and its children
      insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select dgc1.pd_object_id, dgc2.pd_context_id, 0
          from dnm_granted_context dgc1, dnm_granted_context dgc2
          where dgc1.pd_context_id = p_object_id
            and dgc2.pd_object_id = v_new_granted_context
      ;
      
    else
      -- ungranted object      

      -- update both granted and non effective context_ids 
      update dnm_object_1_granted_context 
        set pd_non_effective_context_id = v_context_id,
          pd_context_id = v_new_granted_context
        where pd_object_id = p_object_id
      ;
      
      -- delete old ancesctors for p_object_id and its children in 
      -- dnm_ungranted_context
      delete from dnm_ungranted_context
	where object_id in ( select p_object_id
                             union all
                             select object_id 
                               from dnm_ungranted_context
                               where ancestor_id = p_object_id)
          and ancestor_id in ( select ancestor_id 
                                from dnm_ungranted_context
                                where object_id = p_object_id)
      ;

      -- if v_context_id is not granted object_id
      if v_context_id != v_new_granted_context then
        -- add row to dnm_ungranted_context for p_object_id, v_context_id
        insert into dnm_ungranted_context
          (granted_context_id, object_id, ancestor_id, n_generations)
          values (v_new_granted_context, p_object_id, v_context_id, 1)
        ;
        --add ancestors of v_context_id to p_object_id
        insert  into dnm_ungranted_context
          (granted_context_id, object_id, ancestor_id, n_generations)
          select v_new_granted_context, p_object_id, ancestor_id, n_generations +1
            from dnm_ungranted_context
            where object_id = v_context_id
        ;
      
        -- insert new ancestors for pd_object_ids ungranted children
        insert into dnm_ungranted_context 
          (granted_context_id, object_id, ancestor_id, n_generations)
          select v_new_granted_context, duc1.object_id, duc2.ancestor_id, duc1.n_generations + duc2.n_generations
            from dnm_ungranted_context duc1, dnm_ungranted_context duc2
            where duc1.ancestor_id = p_object_id
              and duc2.object_id = p_object_id
        ;
      end if;      

      -- update granted context_id from ungranted children of p_object_id
      update dnm_object_1_granted_context 
        set pd_context_id = v_new_granted_context
        from dnm_ungranted_context dugc
        where dnm_object_1_granted_context.pd_object_id = dugc.object_id 
          and dugc.ancestor_id = p_object_id
      ;
                   
      -- delete from dnm_granted_context ancestors for 
      -- granted children of p_object_id
	delete from dnm_granted_context 
        where pd_object_id in ( select pd_object_id 
                                  from dnm_ungranted_context dugc, dnm_object_1_granted_context dogc
                                  where dugc.ancestor_id = p_object_id 
                                    and dogc.pd_non_effective_context_id = dugc.object_id)
          and pd_context_id in ( select pd_context_id 
                                   from dnm_granted_context 
                                   where pd_object_id = v_old_granted_context_id)
      ;

      delete from dnm_granted_context 
        where pd_object_id in ( select pd_object_id 
                                  from dnm_object_1_granted_context dogc
                                  where dogc.pd_non_effective_context_id  = p_object_id)
          and pd_context_id in ( select pd_context_id 
                                   from dnm_granted_context 
                                   where pd_object_id = v_old_granted_context_id)
      ;


      -- add new ancestors to granted children of p_object_id
      insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select dgc1.pd_object_id, dgc2.pd_context_id, 0
          from dnm_granted_context dgc1, dnm_granted_context dgc2, dnm_object_1_granted_context dogc
          where dgc1.pd_context_id = dogc.pd_object_id 
            and dogc.pd_non_effective_context_id in ( select p_object_id 
                                                      union all
                                                      select object_id 
                                                        from dnm_ungranted_context
                                                        where ancestor_id = p_object_id)
            and dgc2.pd_object_id = v_new_granted_context
      ;

    end if;
    return null;
  end;' language 'plpgsql'
;


create or replace function dnm_context_drop_grant(
  integer)
  returns integer as '
  declare
    p_object_id alias for $1;
    v_n_grants integer;
    v_new_granted_context integer;
    v_non_effective_context integer;
  begin
    -- get the number of grants on object if any, 
    -- and lock row to prevent incosistency in case of concurrent updates
    select pd_n_grants into v_n_grants
      from dnm_object_grants
      where pd_object_id = p_object_id 
      for update 
    ;

    if (v_n_grants > 1) then
      -- row exists, update counter and exit!
      update dnm_object_grants 
	set pd_n_grants = pd_n_grants -1
        where pd_object_id = p_object_id
      ;
      return null;

    else 

      -- remove all corresponding rows from dnm_ungranted_context
      delete from dnm_granted_context 
        where pd_object_id = p_object_id
          or pd_context_id = p_object_id
      ;

      -- add p_object_id to ancestor_id in dnm_ungranted_context
      insert into dnm_ungranted_context
        (granted_context_id, object_id, ancestor_id, n_generations)
        select p_object_id, object_id, p_object_id, 1
          from dnm_ungranted_context 
          where granted_context_id = p_object_id
          group by object_id
      ;

      -- add implied rows from dnm_ungranted_context
      insert into dnm_ungranted_context
        (granted_context_id, object_id, ancestor_id, n_generations)
	select p_object_id, pd_object_id, p_object_id, 1
          from dnm_object_1_granted_context
           where pd_non_effective_context_id = p_object_id
             and pd_context_id <> pd_object_id
      ;

      select pd_non_effective_context_id, 
          dnm_context_get_granted_context(pd_non_effective_context_id)
        into v_non_effective_context, v_new_granted_context
        from dnm_object_1_granted_context 
        where pd_object_id = p_object_id
      ;

      if v_non_effective_context <> v_new_granted_context then
        -- add ancestors of non effective context to p_object_id
        insert into dnm_ungranted_context
          (granted_context_id, object_id, ancestor_id, n_generations)
          select duc.granted_context_id, p_object_id, duc.ancestor_id, 1
            from dnm_ungranted_context duc
            where duc.object_id = v_non_effective_context
        ;
	-- add non effective context as ancestor
        insert into dnm_ungranted_context
          (granted_context_id, object_id, ancestor_id, n_generations)
          values (p_object_id, p_object_id, v_non_effective_context, 1)
        ;

        -- add ancestors to the p_object_id children
        insert into dnm_ungranted_context
          (granted_context_id, object_id, ancestor_id, n_generations)
          select p_object_id, duc1.object_id, duc2.ancestor_id, 1
            from dnm_ungranted_context duc1, dnm_ungranted_context duc2
            where duc1.ancestor_id = p_object_id
              and duc2.object_id = p_object_id
        ;

      end if;

      update dnm_object_1_granted_context 
        set pd_context_id = v_new_granted_context
        where pd_context_id = p_object_id
      ;

      update dnm_ungranted_context
        set granted_context_id = v_new_granted_context
        where granted_context_id = p_object_id
      ;     

      delete from dnm_object_grants where pd_object_id = p_object_id;

    end if;

    return null;

  end; ' language 'plpgsql'
;



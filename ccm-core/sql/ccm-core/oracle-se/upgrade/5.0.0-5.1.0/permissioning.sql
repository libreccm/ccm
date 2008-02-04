--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: permissioning.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace package permission_denormalization
as
  procedure add_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  );
  procedure remove_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  );
  procedure add_grant (
    object_id   in acs_objects.object_id%TYPE
  );
  procedure remove_grant (
    object_id   in acs_objects.object_id%TYPE
  );
end;
/
show errors


create or replace package body permission_denormalization
as

  procedure add_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  )
  as
    child_count integer;
  begin

    insert into object_context_map
    (object_id, context_id)
    values
    (add_context.object_id, add_context.context_id);

--  See comment near table definition.
--    update context_child_counts
--    set n_children = n_children + 1
--    where object_id = add_context.context_id;
    select count(*) into child_count
    from object_context_map
    where context_id = add_context.context_id;

--    if SQL%NOTFOUND then
    if child_count = 1 then
--        insert into context_child_counts
--        (object_id, n_children)
--        values
--        (add_context.context_id, 1);

            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select add_context.context_id, ancestors.implied_context_id, 
                   (ancestors.n_generations + 1) as n_generations
            from object_context_map, granted_context_non_leaf_map ancestors
            where object_context_map.object_id = add_context.context_id
              and ancestors.object_id = object_context_map.context_id
            UNION ALL
            select add_context.context_id, add_context.context_id, 0
            from object_grants
            where object_id = add_context.context_id;

            insert into ungranted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select add_context.context_id, ancestors.implied_context_id, 
                   (ancestors.n_generations + 1) as n_generations
            from object_context_map, 
                 (select object_id, implied_context_id, n_generations
                  from ungranted_context_non_leaf_map
                  UNION ALL
                  select context_id, context_id, 0
                  from object_context_map c
                  where c.object_id = add_context.context_id
                    and not exists (select 1 from object_grants
                                    where object_id=c.context_id
                                    and n_grants>0)) ancestors
            where object_context_map.object_id = add_context.context_id
              and ancestors.object_id = object_context_map.context_id;

    end if;

      insert into granted_context_non_leaf_map
      (object_id, implied_context_id, n_generations)
      select descendants.object_id, ancestors.implied_context_id, 
             (descendants.n_generations + 
                 ancestors.n_generations + 1) as n_generations
      from granted_context_non_leaf_map ancestors,
           (select object_id, implied_context_id, n_generations
            from all_context_non_leaf_map
            UNION
            select add_context.object_id, add_context.object_id, 0
            from dual
            where exists
                (select 1 
                 from object_context_map
                 where context_id =  add_context.object_id)) descendants
      where ancestors.object_id = add_context.context_id
        and descendants.implied_context_id = add_context.object_id;
    
      INSERT into ungranted_context_non_leaf_map
      (object_id, implied_context_id, n_generations)
      select descendants.object_id, ancestors.implied_context_id, 
             (descendants.n_generations + 
                 ancestors.n_generations + 1) as n_generations
      from (select object_id, implied_context_id, n_generations
            from ungranted_context_non_leaf_map
            UNION ALL
            select add_context.context_id, add_context.context_id, 0
            from dual
            where not exists (select 1 from object_grants
                              where object_id=add_context.context_id
                              and n_grants>0)) ancestors,
           (select object_id, implied_context_id, n_generations
            from all_context_non_leaf_map
            UNION
            select add_context.object_id, add_context.object_id, 0
            from dual
            where exists
                (select 1 
                 from object_context_map
                 where context_id =  add_context.object_id)) descendants
      where ancestors.object_id = add_context.context_id
        and descendants.implied_context_id = add_context.object_id;

  end add_context;

  procedure remove_context (
    object_id   in object_context_map.object_id%TYPE,
    context_id  in object_context_map.context_id%TYPE
  )
  as
    v_delete_context integer;
    child_count integer;
  begin

    delete from object_context_map 
    where object_id = remove_context.object_id;

--  See comment near table definition.
--    delete from context_child_counts
--    where object_id = remove_context.context_id
--      and n_children=1;
    select count(*) into child_count
    from object_context_map
    where context_id = remove_context.context_id;

--    if SQL%NOTFOUND then
--        update context_child_counts
--        set n_children = n_children - 1
--        where object_id = remove_context.context_id;
    if child_count > 0 then
        v_delete_context := 0;
    else
        v_delete_context := 1;
    end if;

    delete from granted_context_non_leaf_map
    where (   object_id in (select object_id
                            from all_context_non_leaf_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from all_context_non_leaf_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));

    delete from ungranted_context_non_leaf_map
    where (   object_id in (select object_id
                            from all_context_non_leaf_map
                            where implied_context_id=remove_context.object_id
                            UNION ALL
                            select remove_context.object_id from dual))
      and (   implied_context_id in (select implied_context_id
                                 from all_context_non_leaf_map
                                 where object_id=remove_context.context_id
                                 UNION ALL
                                 select remove_context.context_id from dual));


    if (v_delete_context = 1) then
        -- the context has no more "children", so remove it
        -- from the denormalization.
        delete from granted_context_non_leaf_map
        where object_id = remove_context.context_id;

        delete from ungranted_context_non_leaf_map
        where object_id = remove_context.context_id;
    end if;

  end remove_context;

  procedure add_grant (
    object_id   in acs_objects.object_id%TYPE
  )
  as
      v_has_children integer;
  begin

    update object_grants
    set n_grants = n_grants +1
    where object_id = add_grant.object_id;

    if SQL%NOTFOUND then
        insert into object_grants
        (object_id, n_grants)
        values
        (add_grant.object_id, 1);

        select count(*) into v_has_children
        from object_context_map
        where context_id = add_grant.object_id;

        if (v_has_children=1) then

            -- insert a row stating that this object has itself as an 
            -- implied context
            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            values
            (add_grant.object_id, add_grant.object_id, 0);

            -- insert rows in granted_context_non_leaf_map for this object's
            -- "children" -- i.e., all objects that have add_grant.object_id as
            -- their context.
            insert into granted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select object_id, implied_context_id, n_generations
            from ungranted_context_non_leaf_map utci
            where utci.implied_context_id = add_grant.object_id;

            -- remove the same rows from ungranted_context_non_leaf_map
            delete from ungranted_context_non_leaf_map
            where implied_context_id = add_grant.object_id;

        end if;
    end if;

  end add_grant;

  procedure remove_grant (
    object_id   in acs_objects.object_id%TYPE
  )
  as
     v_n_grants integer;
  begin

    select n_grants into v_n_grants
    from object_grants
    where object_id = remove_grant.object_id;
    -- if the above select fails to return rows, then something is hosed 
    -- because remove_grant should only run when a grant is being removed 
    -- from acs_permisisons in which case add_grant must have been run
    -- when that grant was originally inserted into acs_permissions.

    if (v_n_grants=1) then
        -- remove the row from object_grants because this object has
        -- no grants left.
        delete from object_grants where object_id = remove_grant.object_id;

            -- insert rows in ungranted_context_non_leaf_map for this object's
            -- "children" -- i.e., all objects that have 
            -- remove_grant.object_id as their context.
            -- NOTE: we leave out the mapping between an object and itself,
            -- primarily because it makes the implementation of add_grant()
            -- easier.
            insert into ungranted_context_non_leaf_map
            (object_id, implied_context_id, n_generations)
            select object_id, implied_context_id, n_generations
            from granted_context_non_leaf_map gtcm
            where gtcm.implied_context_id = remove_grant.object_id
              and object_id!=implied_context_id;

            -- remove the same rows from the granted_context_non_leaf_map
            delete from granted_context_non_leaf_map
            where implied_context_id = remove_grant.object_id;
    else
        -- decrement the count of grants for this object
        update object_grants
        set n_grants = n_grants - 1
        where object_id = remove_grant.object_id;

    end if;

  end remove_grant;

end;
/
show errors

drop table context_child_counts;

insert into acs_privileges
(privilege)
values
('edit');


insert into acs_objects (object_id, object_type, display_name) 
values (-204, 'com.arsdigita.kernel.Party', 'ACS System Party');
insert into parties (party_id, primary_email) 
values (-204, 'acs-system-party@acs-system');

-- Create a permission for the ACS system party.

insert into acs_permissions (object_id, grantee_id, privilege, creation_date)
values (0, -204, 'admin', sysdate);

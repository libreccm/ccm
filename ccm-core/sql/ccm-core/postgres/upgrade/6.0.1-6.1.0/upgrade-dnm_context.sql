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
-- $Id: upgrade-dnm_context.sql 1322 2006-09-20 09:37:00Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace function upgrade_dnm_context ()
  returns integer as '
  declare
    c record;
    t integer;
    i integer;
    v_level integer;
    v_level_max integer;
  begin

    delete from dnm_ungranted_context;
    delete from dnm_granted_context;
    delete from dnm_object_grants;
    delete from dnm_object_1_granted_context;

    insert into dnm_object_1_granted_context
        (pd_object_id, pd_context_id, pd_non_effective_context_id)
        select ao.object_id, 0, coalesce(oc.context_id,0)
            from acs_objects ao left join object_context oc
                on ao.object_id = oc.object_id ;
    insert into dnm_object_grants (pd_object_id, pd_n_grants)
        select 0, count(*)+1 from acs_permissions where object_id = 0;
    insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag) values (0,0,1);

    v_level := 1;
    insert into dnm_ungranted_context
        (object_id, ancestor_id, granted_context_id, n_generations)
       select
          object_id, context_id, 0, v_level
          from object_context where context_id is not null;
    get diagnostics t = ROW_COUNT;
    raise notice ''Level % completed, % row(s) affected'', v_level, t;
    loop
        insert into dnm_ungranted_context
            (object_id, ancestor_id, granted_context_id, n_generations)
           select
              child.object_id, parent.ancestor_id, 0, v_level + 1
                from
                    dnm_ungranted_context child, dnm_ungranted_context parent
                where child.ancestor_id = parent.object_id
                    and child.n_generations = v_level
                    and parent.n_generations = 1 ;
        get diagnostics t = ROW_COUNT;
        v_level := v_level + 1;
        raise notice ''Level % completed, % row(s) affected'', v_level, t;
        exit when t = 0;
    end loop;

    -- These objects have grants directly on themselves
    update dnm_object_1_granted_context set
        pd_context_id = pd_object_id
        where pd_object_id in (select object_id from acs_permissions);
    get diagnostics t = ROW_COUNT;
    raise notice ''Direct grants to % row(s)'', t;

    select max(n_generations) into v_level_max from dnm_ungranted_context;
    v_level := 1;
    loop
        update dnm_object_1_granted_context set
            pd_context_id = (select distinct ap.object_id
                             from acs_permissions ap, dnm_ungranted_context duc
                             where ap.object_id = duc.ancestor_id
                               and duc.n_generations = v_level
                               and duc.object_id = pd_object_id)
            where
                pd_context_id = 0
                and pd_object_id in (select duc.object_id
                                     from acs_permissions ap, dnm_ungranted_context duc
                                     where ap.object_id = duc.ancestor_id
                                       and duc.n_generations = v_level) ;
        get diagnostics t = ROW_COUNT;
        raise notice ''Granted context on ancestor level % to % row(s)'', v_level, t;
            v_level := v_level + 1;
        exit when v_level > v_level_max;
    end loop;


    update dnm_ungranted_context set
        granted_context_id = (select duc2.ancestor_id from dnm_ungranted_context duc2
                               where duc2.object_id = dnm_ungranted_context.object_id
                               and duc2.n_generations = (select min(duc3.n_generations)
                                                         from dnm_ungranted_context duc3
                                                         where duc3.object_id = duc2.object_id
                                                         and duc3.ancestor_id in
                                                               (select object_id from acs_permissions))
                               )
        where
        object_id in (select duc4.object_id from dnm_ungranted_context duc4
                      where duc4.ancestor_id in (select object_id from acs_permissions)) ;


    insert into dnm_object_grants (pd_object_id, pd_n_grants)
        select object_id, count(*) from acs_permissions
           where object_id != 0
             group by object_id ;
    insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select distinct object_id, object_id, 1
           from acs_permissions where object_id != 0;
    insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select distinct object_id, 0, 0
           from acs_permissions where object_id != 0;
    insert into dnm_granted_context
        (pd_object_id, pd_context_id, pd_dummy_flag)
        select duc.object_id, duc.ancestor_id, 0
           from dnm_ungranted_context duc
           where duc.object_id in (select object_id from acs_permissions)
              and duc.ancestor_id in (select object_id from acs_permissions) ;

    delete from dnm_ungranted_context where
        object_id in (select duc2.object_id from dnm_ungranted_context duc2
                      where duc2.ancestor_id in
                            (select ap.object_id from acs_permissions ap))
        and n_generations >= (select min(duc3.n_generations) from dnm_ungranted_context duc3
                                  where duc3.object_id = dnm_ungranted_context.object_id
                                  and duc3.ancestor_id in
                                            (select ap.object_id from acs_permissions ap))
        ;

    delete from dnm_ungranted_context where object_id in (
        select object_id from acs_permissions);

    return null;
end; ' language 'plpgsql' ;

select upgrade_dnm_context();


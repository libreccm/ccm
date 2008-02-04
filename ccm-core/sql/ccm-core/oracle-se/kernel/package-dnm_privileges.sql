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
-- $Id: package-dnm_privileges.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace package dnm_priv_utils as 

  procedure add_privilege (privilege varchar2);
  procedure delete_privilege(privilege varchar2);

  procedure add_child_privilege
   (p_privilege varchar2, p_child_privilege varchar2);

  procedure delete_child_privilege(p_privilege varchar2, p_child_privilege varchar2);

  procedure remove_grant 
    (p_object_id integer, p_gratee_id integer, p_privilege varchar2);
  procedure add_grant
    (p_object_id integer, p_gratee_id integer, p_privilege varchar2);


end dnm_priv_utils;
/
show errors


create or replace package body dnm_priv_utils as 

  -----------------------
  -- PRIVATE DECLARATIONS
  -----------------------
  procedure sync_permission_columns 
    (p_object_id integer, p_gratee_id integer, p_priv_list varchar2);
    
  procedure resync_hier_map;

  procedure resync_dnm_privileges;
  
  procedure resync_dnm_permissions;

  ----------------------
  -- GLOBAL DECLARATIONS
  ----------------------  
  procedure add_privilege (privilege varchar2) 
  is 
    i integer := 1; 
    v_column_name varchar2(100) := 'pd_priv_01';
    c integer;
  begin

    -- look for first free column name for new privilege
    loop
      select count(1) into c from dnm_privilege_col_map 
        where column_name = v_column_name;
      if c = 0 then
         exit;
      else 
	 i := i + 1;
	 v_column_name := 'pd_priv_' || trim(to_char(i,'09'));
      end if;
    end loop;

    insert into dnm_privilege_col_map (pd_privilege, column_name)
      values(privilege, v_column_name);
    
    execute immediate 'insert into dnm_privileges (pd_privilege, ' ||
      v_column_name || ') values ('''||privilege||''',1)';
    
    resync_hier_map;
      
  end add_privilege;

  procedure delete_child_privilege(p_privilege varchar2, p_child_privilege varchar2)
  is
  begin
    -- remove rows from acs_privilege_hierarchy clone
    delete dnm_privilege_hierarchy 
      where pd_privilege = p_privilege 
	and pd_child_privilege = p_child_privilege
    ;

    -- rebuild dnm_privilege_hierarchy_map
    resync_hier_map;

    -- rebuild dnm_privileges
    resync_dnm_privileges;
    -- update dnm_permissions
    resync_dnm_permissions;
  end;

  procedure delete_privilege(privilege varchar2)
  is
  begin
    delete dnm_privilege_col_map where pd_privilege = privilege;
    delete dnm_privileges where pd_privilege = privilege;
  end;

  procedure add_child_privilege(p_privilege varchar2, p_child_privilege varchar2)
  is 
  begin
    insert into dnm_privilege_hierarchy (pd_privilege, pd_child_privilege)
      values (p_privilege, p_child_privilege);
    --find affected rows in dnm_permissons table
    update dnm_privileges
      set (pd_priv_01, pd_priv_02, pd_priv_03, pd_priv_04, pd_priv_05, 
           pd_priv_06, pd_priv_07, pd_priv_08, pd_priv_09, pd_priv_10, 
	   pd_priv_11, pd_priv_12, pd_priv_13, pd_priv_14, pd_priv_15, 
           pd_priv_16, pd_priv_17, pd_priv_18, pd_priv_19, pd_priv_20, 
	   pd_priv_21, pd_priv_22, pd_priv_23, pd_priv_24, pd_priv_25, 
           pd_priv_26, pd_priv_27, pd_priv_28, pd_priv_29, pd_priv_30, 
	   pd_priv_31, pd_priv_32, pd_priv_33, pd_priv_34, pd_priv_35, 
           pd_priv_36, pd_priv_37, pd_priv_38, pd_priv_39, pd_priv_40, 
	   pd_priv_41, pd_priv_42, pd_priv_43, pd_priv_44, pd_priv_45, 
           pd_priv_46, pd_priv_47, pd_priv_48, pd_priv_49, pd_priv_50, 
	   pd_priv_51, pd_priv_52, pd_priv_53, pd_priv_54, pd_priv_55, 
           pd_priv_56, pd_priv_57, pd_priv_58, pd_priv_59, pd_priv_60, 
	   pd_priv_61, pd_priv_62, pd_priv_63, pd_priv_64, pd_priv_65, 
           pd_priv_66, pd_priv_67, pd_priv_68, pd_priv_69, pd_priv_70, 
	   pd_priv_71, pd_priv_72, pd_priv_73, pd_priv_74, pd_priv_75, 
           pd_priv_76, pd_priv_77, pd_priv_78, pd_priv_79, pd_priv_80, 
	   pd_priv_81, pd_priv_82, pd_priv_83, pd_priv_84, pd_priv_85, 
           pd_priv_86, pd_priv_87, pd_priv_88, pd_priv_89, pd_priv_90, 
	   pd_priv_91, pd_priv_92, pd_priv_93, pd_priv_94, pd_priv_95, 
           pd_priv_96, pd_priv_97, pd_priv_98, pd_priv_99 ) 
      = (select 
           max(pd_priv_01), max(pd_priv_02), max(pd_priv_03), max(pd_priv_04), max(pd_priv_05), 
           max(pd_priv_06), max(pd_priv_07), max(pd_priv_08), max(pd_priv_09), max(pd_priv_10), 
	   max(pd_priv_11), max(pd_priv_12), max(pd_priv_13), max(pd_priv_14), max(pd_priv_15), 
           max(pd_priv_16), max(pd_priv_17), max(pd_priv_18), max(pd_priv_19), max(pd_priv_20), 
	   max(pd_priv_21), max(pd_priv_22), max(pd_priv_23), max(pd_priv_24), max(pd_priv_25), 
           max(pd_priv_26), max(pd_priv_27), max(pd_priv_28), max(pd_priv_29), max(pd_priv_30), 
	   max(pd_priv_31), max(pd_priv_32), max(pd_priv_33), max(pd_priv_34), max(pd_priv_35), 
           max(pd_priv_36), max(pd_priv_37), max(pd_priv_38), max(pd_priv_39), max(pd_priv_40), 
	   max(pd_priv_41), max(pd_priv_42), max(pd_priv_43), max(pd_priv_44), max(pd_priv_45), 
           max(pd_priv_46), max(pd_priv_47), max(pd_priv_48), max(pd_priv_49), max(pd_priv_50), 
	   max(pd_priv_51), max(pd_priv_52), max(pd_priv_53), max(pd_priv_54), max(pd_priv_55), 
           max(pd_priv_56), max(pd_priv_57), max(pd_priv_58), max(pd_priv_59), max(pd_priv_60), 
	   max(pd_priv_61), max(pd_priv_62), max(pd_priv_63), max(pd_priv_64), max(pd_priv_65), 
           max(pd_priv_66), max(pd_priv_67), max(pd_priv_68), max(pd_priv_69), max(pd_priv_70), 
	   max(pd_priv_71), max(pd_priv_72), max(pd_priv_73), max(pd_priv_74), max(pd_priv_75), 
           max(pd_priv_76), max(pd_priv_77), max(pd_priv_78), max(pd_priv_79), max(pd_priv_80), 
	   max(pd_priv_81), max(pd_priv_82), max(pd_priv_83), max(pd_priv_84), max(pd_priv_85), 
           max(pd_priv_86), max(pd_priv_87), max(pd_priv_88), max(pd_priv_89), max(pd_priv_90), 
	   max(pd_priv_91), max(pd_priv_92), max(pd_priv_93), max(pd_priv_94), max(pd_priv_95), 
           max(pd_priv_96), max(pd_priv_97), max(pd_priv_98), max(pd_priv_99) 
         from dnm_privileges p
	 where pd_privilege in (select pd_child_privilege 
		                 from dnm_privilege_hierarchy ph
				   start with pd_privilege = p_child_privilege
				     connect by prior pd_child_privilege = pd_privilege
		             union all 
			     select p_child_privilege from dual
		             union all
		             select dnm_privileges.pd_privilege from dual))
      where pd_privilege in (select pd_privilege 
			   from dnm_privilege_hierarchy
			     start with pd_child_privilege = p_privilege
			       connect by prior pd_privilege = pd_child_privilege
			 union all
		         select p_privilege from dual)
    ;
    -- updating acs_permissions;
    for c in (select ap.object_id, ap.grantee_id, dp.pd_priv_list 
                from acs_permissions ap, dnm_permissions dp
		where ap.privilege in (select pd_privilege 
			              from dnm_privilege_hierarchy
				      start with pd_child_privilege = p_child_privilege
				        connect by prior pd_privilege = pd_child_privilege)
		  and ap.object_id = dp.pd_object_id 
                  and ap.grantee_id = dp.pd_grantee_id
		for update)
    loop
       sync_permission_columns(c.object_id, c.grantee_id, c.pd_priv_list);
    end loop;
    resync_hier_map;
  end;

  procedure add_grant
   (p_object_id integer, p_gratee_id integer, p_privilege varchar2)
  is
    v_grants integer;
    v_pd_priv_list varchar2(4000);
  begin
    update dnm_permissions 
        set pd_n_grants = pd_n_grants + 1, pd_priv_list = pd_priv_list || p_privilege  || ','
        where pd_object_id = p_object_id 
	   and pd_grantee_id = p_gratee_id
	returning pd_priv_list into v_pd_priv_list;
    v_grants := SQL%ROWCOUNT;  
    if v_grants = 0 then
      insert into dnm_permissions (pd_object_id, pd_grantee_id, pd_n_grants, pd_priv_list) 
        values (p_object_id, p_gratee_id, 1, ',' || p_privilege || ',')
	returning pd_priv_list into v_pd_priv_list;
    end if;
    sync_permission_columns(p_object_id, p_gratee_id, v_pd_priv_list);
  end;

  procedure remove_grant 
    (p_object_id integer, p_gratee_id integer, p_privilege varchar2)
  is 
    v_grants integer;
    v_pd_priv_list varchar2(4000);
  begin
    select pd_n_grants into v_grants
      from dnm_permissions 
      where pd_object_id = p_object_id and pd_grantee_id = p_gratee_id
      for update;
    if v_grants > 1 then
      update dnm_permissions 
        set pd_n_grants = pd_n_grants -1, 
	    pd_priv_list = replace(pd_priv_list,',' || p_privilege || ',', ',')
        where pd_object_id = p_object_id and pd_grantee_id = p_gratee_id
	returning pd_priv_list into v_pd_priv_list;
	sync_permission_columns(p_object_id, p_gratee_id, v_pd_priv_list);
    else 
      delete dnm_permissions 
        where  pd_object_id = p_object_id and pd_grantee_id = p_gratee_id;
    end if;
  end;

  procedure sync_permission_columns 
    (p_object_id integer, p_gratee_id integer, p_priv_list varchar2)
  is 
    sql_stmt varchar2(4000);
  begin
    execute immediate 'update dnm_permissions 
      set (pd_priv_01, pd_priv_02, pd_priv_03, pd_priv_04, pd_priv_05, 
           pd_priv_06, pd_priv_07, pd_priv_08, pd_priv_09, pd_priv_10, 
	   pd_priv_11, pd_priv_12, pd_priv_13, pd_priv_14, pd_priv_15, 
           pd_priv_16, pd_priv_17, pd_priv_18, pd_priv_19, pd_priv_20, 
	   pd_priv_21, pd_priv_22, pd_priv_23, pd_priv_24, pd_priv_25, 
           pd_priv_26, pd_priv_27, pd_priv_28, pd_priv_29, pd_priv_30, 
	   pd_priv_31, pd_priv_32, pd_priv_33, pd_priv_34, pd_priv_35, 
           pd_priv_36, pd_priv_37, pd_priv_38, pd_priv_39, pd_priv_40, 
	   pd_priv_41, pd_priv_42, pd_priv_43, pd_priv_44, pd_priv_45, 
           pd_priv_46, pd_priv_47, pd_priv_48, pd_priv_49, pd_priv_50, 
	   pd_priv_51, pd_priv_52, pd_priv_53, pd_priv_54, pd_priv_55, 
           pd_priv_56, pd_priv_57, pd_priv_58, pd_priv_59, pd_priv_60, 
	   pd_priv_61, pd_priv_62, pd_priv_63, pd_priv_64, pd_priv_65, 
           pd_priv_66, pd_priv_67, pd_priv_68, pd_priv_69, pd_priv_70, 
	   pd_priv_71, pd_priv_72, pd_priv_73, pd_priv_74, pd_priv_75, 
           pd_priv_76, pd_priv_77, pd_priv_78, pd_priv_79, pd_priv_80, 
	   pd_priv_81, pd_priv_82, pd_priv_83, pd_priv_84, pd_priv_85, 
           pd_priv_86, pd_priv_87, pd_priv_88, pd_priv_89, pd_priv_90, 
  	   pd_priv_91, pd_priv_92, pd_priv_93, pd_priv_94, pd_priv_95, 
           pd_priv_96, pd_priv_97, pd_priv_98, pd_priv_99 ) 
      = ( select
           max(pd_priv_01), max(pd_priv_02), max(pd_priv_03), max(pd_priv_04), max(pd_priv_05),
           max(pd_priv_06), max(pd_priv_07), max(pd_priv_08), max(pd_priv_09), max(pd_priv_10),
	   max(pd_priv_11), max(pd_priv_12), max(pd_priv_13), max(pd_priv_14), max(pd_priv_15),
           max(pd_priv_16), max(pd_priv_17), max(pd_priv_18), max(pd_priv_19), max(pd_priv_20),
	   max(pd_priv_21), max(pd_priv_22), max(pd_priv_23), max(pd_priv_24), max(pd_priv_25),
           max(pd_priv_26), max(pd_priv_27), max(pd_priv_28), max(pd_priv_29), max(pd_priv_30),
	   max(pd_priv_31), max(pd_priv_32), max(pd_priv_33), max(pd_priv_34), max(pd_priv_35),
           max(pd_priv_36), max(pd_priv_37), max(pd_priv_38), max(pd_priv_39), max(pd_priv_40),
	   max(pd_priv_41), max(pd_priv_42), max(pd_priv_43), max(pd_priv_44), max(pd_priv_45),
           max(pd_priv_46), max(pd_priv_47), max(pd_priv_48), max(pd_priv_49), max(pd_priv_50),
	   max(pd_priv_51), max(pd_priv_52), max(pd_priv_53), max(pd_priv_54), max(pd_priv_55),
           max(pd_priv_56), max(pd_priv_57), max(pd_priv_58), max(pd_priv_59), max(pd_priv_60),
	   max(pd_priv_61), max(pd_priv_62), max(pd_priv_63), max(pd_priv_64), max(pd_priv_65), 
           max(pd_priv_66), max(pd_priv_67), max(pd_priv_68), max(pd_priv_69), max(pd_priv_70),
	   max(pd_priv_71), max(pd_priv_72), max(pd_priv_73), max(pd_priv_74), max(pd_priv_75),
           max(pd_priv_76), max(pd_priv_77), max(pd_priv_78), max(pd_priv_79), max(pd_priv_80),
	   max(pd_priv_81), max(pd_priv_82), max(pd_priv_83), max(pd_priv_84), max(pd_priv_85),
           max(pd_priv_86), max(pd_priv_87), max(pd_priv_88), max(pd_priv_89), max(pd_priv_90),
	   max(pd_priv_91), max(pd_priv_92), max(pd_priv_93), max(pd_priv_94), max(pd_priv_95),
           max(pd_priv_96), max(pd_priv_97), max(pd_priv_98), max(pd_priv_99) 
        from dnm_privileges p 
        where p.pd_privilege in (''' ||  
	  replace(trim(',' from p_priv_list),',', ''',''') || '''))
      where pd_object_id = ' || to_char(p_object_id) || ' 
        and pd_grantee_id = ' || to_char(p_gratee_id);
  end;

  procedure resync_hier_map 
  as 
  begin
  
    delete dnm_privilege_hierarchy_map;
    -- fill dnm_privilege_hierarchy_map table
    
    for c in (select pd_privilege from dnm_privileges) loop
      -- first add privilege self maps 
      insert into dnm_privilege_hierarchy_map 
        (pd_privilege, pd_child_privilege)
        values (c.pd_privilege, c.pd_privilege)
      ;
      -- insert all child_privileges
      insert into dnm_privilege_hierarchy_map
        (pd_privilege, pd_child_privilege)
        select c.pd_privilege, ph.pd_child_privilege
          from dnm_privilege_hierarchy ph
          connect by ph.pd_privilege = prior ph.pd_child_privilege
              -- exclude cycles!
          	  and ph.pd_child_privilege != c.pd_privilege
            start with ph.pd_privilege = c.pd_privilege 
          group by  c.pd_privilege, ph.pd_child_privilege
      ;
    end loop;
    
  end resync_hier_map;


  procedure resync_dnm_privileges
  is
    v_priv_col_name varchar2(200);
  begin
     -- nullify all pd_privx columns 
     update dnm_privileges
     set pd_priv_01 = null, pd_priv_02 = null, pd_priv_03 = null, pd_priv_04 = null, pd_priv_05 = null, 
           pd_priv_06 = null, pd_priv_07 = null, pd_priv_08 = null, pd_priv_09 = null, pd_priv_10 = null, 
	   pd_priv_11 = null, pd_priv_12 = null, pd_priv_13 = null, pd_priv_14 = null, pd_priv_15 = null, 
           pd_priv_16 = null, pd_priv_17 = null, pd_priv_18 = null, pd_priv_19 = null, pd_priv_20 = null, 
	   pd_priv_21 = null, pd_priv_22 = null, pd_priv_23 = null, pd_priv_24 = null, pd_priv_25 = null, 
           pd_priv_26 = null, pd_priv_27 = null, pd_priv_28 = null, pd_priv_29 = null, pd_priv_30 = null, 
	   pd_priv_31 = null, pd_priv_32 = null, pd_priv_33 = null, pd_priv_34 = null, pd_priv_35 = null, 
           pd_priv_36 = null, pd_priv_37 = null, pd_priv_38 = null, pd_priv_39 = null, pd_priv_40 = null, 
	   pd_priv_41 = null, pd_priv_42 = null, pd_priv_43 = null, pd_priv_44 = null, pd_priv_45 = null, 
           pd_priv_46 = null, pd_priv_47 = null, pd_priv_48 = null, pd_priv_49 = null, pd_priv_50 = null, 
	   pd_priv_51 = null, pd_priv_52 = null, pd_priv_53 = null, pd_priv_54 = null, pd_priv_55 = null, 
           pd_priv_56 = null, pd_priv_57 = null, pd_priv_58 = null, pd_priv_59 = null, pd_priv_60 = null, 
	   pd_priv_61 = null, pd_priv_62 = null, pd_priv_63 = null, pd_priv_64 = null, pd_priv_65 = null, 
           pd_priv_66 = null, pd_priv_67 = null, pd_priv_68 = null, pd_priv_69 = null, pd_priv_70 = null, 
	   pd_priv_71 = null, pd_priv_72 = null, pd_priv_73 = null, pd_priv_74 = null, pd_priv_75 = null, 
           pd_priv_76 = null, pd_priv_77 = null, pd_priv_78 = null, pd_priv_79 = null, pd_priv_80 = null, 
	   pd_priv_81 = null, pd_priv_82 = null, pd_priv_83 = null, pd_priv_84 = null, pd_priv_85 = null, 
           pd_priv_86 = null, pd_priv_87 = null, pd_priv_88 = null, pd_priv_89 = null, pd_priv_90 = null, 
  	   pd_priv_91 = null, pd_priv_92 = null, pd_priv_93 = null, pd_priv_94 = null, pd_priv_95 = null, 
           pd_priv_96 = null, pd_priv_97 = null, pd_priv_98 = null, pd_priv_99 = null 
    ;

    -- start resyncing
    for c in (select pd_privilege from dnm_privilege_col_map for update) loop
      select column_name into v_priv_col_name
        from dnm_privilege_col_map where pd_privilege = c.pd_privilege;
     
      execute immediate 'update dnm_privileges set '|| v_priv_col_name || ' = 1 ' 
                        || ' where pd_privilege = '''||c.pd_privilege||'''';
    end loop;

    update dnm_privileges dnmp
        set (pd_priv_01, pd_priv_02, pd_priv_03, pd_priv_04, pd_priv_05, 
           pd_priv_06, pd_priv_07, pd_priv_08, pd_priv_09, pd_priv_10, 
	   pd_priv_11, pd_priv_12, pd_priv_13, pd_priv_14, pd_priv_15, 
           pd_priv_16, pd_priv_17, pd_priv_18, pd_priv_19, pd_priv_20, 
	   pd_priv_21, pd_priv_22, pd_priv_23, pd_priv_24, pd_priv_25, 
           pd_priv_26, pd_priv_27, pd_priv_28, pd_priv_29, pd_priv_30, 
	   pd_priv_31, pd_priv_32, pd_priv_33, pd_priv_34, pd_priv_35, 
           pd_priv_36, pd_priv_37, pd_priv_38, pd_priv_39, pd_priv_40, 
	   pd_priv_41, pd_priv_42, pd_priv_43, pd_priv_44, pd_priv_45, 
           pd_priv_46, pd_priv_47, pd_priv_48, pd_priv_49, pd_priv_50, 
	   pd_priv_51, pd_priv_52, pd_priv_53, pd_priv_54, pd_priv_55, 
           pd_priv_56, pd_priv_57, pd_priv_58, pd_priv_59, pd_priv_60, 
	   pd_priv_61, pd_priv_62, pd_priv_63, pd_priv_64, pd_priv_65, 
           pd_priv_66, pd_priv_67, pd_priv_68, pd_priv_69, pd_priv_70, 
	   pd_priv_71, pd_priv_72, pd_priv_73, pd_priv_74, pd_priv_75, 
           pd_priv_76, pd_priv_77, pd_priv_78, pd_priv_79, pd_priv_80, 
	   pd_priv_81, pd_priv_82, pd_priv_83, pd_priv_84, pd_priv_85, 
           pd_priv_86, pd_priv_87, pd_priv_88, pd_priv_89, pd_priv_90, 
	   pd_priv_91, pd_priv_92, pd_priv_93, pd_priv_94, pd_priv_95, 
           pd_priv_96, pd_priv_97, pd_priv_98, pd_priv_99 ) 
        = (select 
             max(pd_priv_01), max(pd_priv_02), max(pd_priv_03), max(pd_priv_04), max(pd_priv_05), 
             max(pd_priv_06), max(pd_priv_07), max(pd_priv_08), max(pd_priv_09), max(pd_priv_10), 
	     max(pd_priv_11), max(pd_priv_12), max(pd_priv_13), max(pd_priv_14), max(pd_priv_15), 
             max(pd_priv_16), max(pd_priv_17), max(pd_priv_18), max(pd_priv_19), max(pd_priv_20), 
	     max(pd_priv_21), max(pd_priv_22), max(pd_priv_23), max(pd_priv_24), max(pd_priv_25), 
             max(pd_priv_26), max(pd_priv_27), max(pd_priv_28), max(pd_priv_29), max(pd_priv_30), 
	     max(pd_priv_31), max(pd_priv_32), max(pd_priv_33), max(pd_priv_34), max(pd_priv_35), 
             max(pd_priv_36), max(pd_priv_37), max(pd_priv_38), max(pd_priv_39), max(pd_priv_40), 
	     max(pd_priv_41), max(pd_priv_42), max(pd_priv_43), max(pd_priv_44), max(pd_priv_45), 
             max(pd_priv_46), max(pd_priv_47), max(pd_priv_48), max(pd_priv_49), max(pd_priv_50), 
	     max(pd_priv_51), max(pd_priv_52), max(pd_priv_53), max(pd_priv_54), max(pd_priv_55), 
             max(pd_priv_56), max(pd_priv_57), max(pd_priv_58), max(pd_priv_59), max(pd_priv_60), 
	     max(pd_priv_61), max(pd_priv_62), max(pd_priv_63), max(pd_priv_64), max(pd_priv_65), 
             max(pd_priv_66), max(pd_priv_67), max(pd_priv_68), max(pd_priv_69), max(pd_priv_70), 
	     max(pd_priv_71), max(pd_priv_72), max(pd_priv_73), max(pd_priv_74), max(pd_priv_75), 
             max(pd_priv_76), max(pd_priv_77), max(pd_priv_78), max(pd_priv_79), max(pd_priv_80), 
	     max(pd_priv_81), max(pd_priv_82), max(pd_priv_83), max(pd_priv_84), max(pd_priv_85), 
             max(pd_priv_86), max(pd_priv_87), max(pd_priv_88), max(pd_priv_89), max(pd_priv_90), 
	     max(pd_priv_91), max(pd_priv_92), max(pd_priv_93), max(pd_priv_94), max(pd_priv_95), 
             max(pd_priv_96), max(pd_priv_97), max(pd_priv_98), max(pd_priv_99) 
           from dnm_privileges p
   	   where pd_privilege in (select pd_child_privilege 
		                    from dnm_privilege_hierarchy ph
				    start with pd_privilege = dnmp.pd_privilege
				      connect by prior pd_child_privilege = pd_privilege
                                  union all
		                  select dnmp.pd_privilege from dual))
      ;
  end;

  procedure resync_dnm_permissions
  is
  begin
    for c in (select pd_object_id, pd_priv_list, pd_grantee_id from dnm_permissions) loop
      execute immediate 'update dnm_permissions per
      set (pd_priv_01, pd_priv_02, pd_priv_03, pd_priv_04, pd_priv_05, 
           pd_priv_06, pd_priv_07, pd_priv_08, pd_priv_09, pd_priv_10, 
	   pd_priv_11, pd_priv_12, pd_priv_13, pd_priv_14, pd_priv_15, 
           pd_priv_16, pd_priv_17, pd_priv_18, pd_priv_19, pd_priv_20, 
	   pd_priv_21, pd_priv_22, pd_priv_23, pd_priv_24, pd_priv_25, 
           pd_priv_26, pd_priv_27, pd_priv_28, pd_priv_29, pd_priv_30, 
	   pd_priv_31, pd_priv_32, pd_priv_33, pd_priv_34, pd_priv_35, 
           pd_priv_36, pd_priv_37, pd_priv_38, pd_priv_39, pd_priv_40, 
	   pd_priv_41, pd_priv_42, pd_priv_43, pd_priv_44, pd_priv_45, 
           pd_priv_46, pd_priv_47, pd_priv_48, pd_priv_49, pd_priv_50, 
	   pd_priv_51, pd_priv_52, pd_priv_53, pd_priv_54, pd_priv_55, 
           pd_priv_56, pd_priv_57, pd_priv_58, pd_priv_59, pd_priv_60, 
	   pd_priv_61, pd_priv_62, pd_priv_63, pd_priv_64, pd_priv_65, 
           pd_priv_66, pd_priv_67, pd_priv_68, pd_priv_69, pd_priv_70, 
	   pd_priv_71, pd_priv_72, pd_priv_73, pd_priv_74, pd_priv_75, 
           pd_priv_76, pd_priv_77, pd_priv_78, pd_priv_79, pd_priv_80, 
	   pd_priv_81, pd_priv_82, pd_priv_83, pd_priv_84, pd_priv_85, 
           pd_priv_86, pd_priv_87, pd_priv_88, pd_priv_89, pd_priv_90, 
  	   pd_priv_91, pd_priv_92, pd_priv_93, pd_priv_94, pd_priv_95, 
           pd_priv_96, pd_priv_97, pd_priv_98, pd_priv_99 ) 
      = ( select
           max(pd_priv_01), max(pd_priv_02), max(pd_priv_03), max(pd_priv_04), max(pd_priv_05),
           max(pd_priv_06), max(pd_priv_07), max(pd_priv_08), max(pd_priv_09), max(pd_priv_10),
	   max(pd_priv_11), max(pd_priv_12), max(pd_priv_13), max(pd_priv_14), max(pd_priv_15),
           max(pd_priv_16), max(pd_priv_17), max(pd_priv_18), max(pd_priv_19), max(pd_priv_20),
	   max(pd_priv_21), max(pd_priv_22), max(pd_priv_23), max(pd_priv_24), max(pd_priv_25),
           max(pd_priv_26), max(pd_priv_27), max(pd_priv_28), max(pd_priv_29), max(pd_priv_30),
	   max(pd_priv_31), max(pd_priv_32), max(pd_priv_33), max(pd_priv_34), max(pd_priv_35),
           max(pd_priv_36), max(pd_priv_37), max(pd_priv_38), max(pd_priv_39), max(pd_priv_40),
	   max(pd_priv_41), max(pd_priv_42), max(pd_priv_43), max(pd_priv_44), max(pd_priv_45),
           max(pd_priv_46), max(pd_priv_47), max(pd_priv_48), max(pd_priv_49), max(pd_priv_50),
	   max(pd_priv_51), max(pd_priv_52), max(pd_priv_53), max(pd_priv_54), max(pd_priv_55),
           max(pd_priv_56), max(pd_priv_57), max(pd_priv_58), max(pd_priv_59), max(pd_priv_60),
	   max(pd_priv_61), max(pd_priv_62), max(pd_priv_63), max(pd_priv_64), max(pd_priv_65), 
           max(pd_priv_66), max(pd_priv_67), max(pd_priv_68), max(pd_priv_69), max(pd_priv_70),
	   max(pd_priv_71), max(pd_priv_72), max(pd_priv_73), max(pd_priv_74), max(pd_priv_75),
           max(pd_priv_76), max(pd_priv_77), max(pd_priv_78), max(pd_priv_79), max(pd_priv_80),
	   max(pd_priv_81), max(pd_priv_82), max(pd_priv_83), max(pd_priv_84), max(pd_priv_85),
           max(pd_priv_86), max(pd_priv_87), max(pd_priv_88), max(pd_priv_89), max(pd_priv_90),
	   max(pd_priv_91), max(pd_priv_92), max(pd_priv_93), max(pd_priv_94), max(pd_priv_95),
           max(pd_priv_96), max(pd_priv_97), max(pd_priv_98), max(pd_priv_99) 
         from dnm_privileges p 
         where p.pd_privilege in (''' ||  
	  replace(trim(',' from c.pd_priv_list),',', ''',''') || '''))
      where pd_object_id = ' || to_char(c.pd_object_id) || ' 
        and pd_grantee_id = ' || to_char(c.pd_grantee_id);
    end loop;
  end;
end dnm_priv_utils;
/
show errors

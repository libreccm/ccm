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

create or replace function dnm_privileges_sync_hier_map ()
  returns integer as '
  declare
  begin
  
    delete from dnm_privilege_hierarchy_map;

    -- insert selfmaps       
    insert into dnm_privilege_hierarchy_map
      (pd_privilege, pd_child_privilege)
      select pd_privilege, pd_privilege 
        from dnm_privileges
    ;
    
    insert into dnm_privilege_hierarchy_map
      (pd_privilege, pd_child_privilege)
      select pd_privilege, pd_child_privilege
        from dnm_privilege_hierarchy
    ;
 
    loop
      insert into dnm_privilege_hierarchy_map
        (pd_privilege, pd_child_privilege)
        select distinct p1.pd_privilege, p2.pd_child_privilege
          from dnm_privilege_hierarchy_map p1, dnm_privilege_hierarchy_map p2
          where p1.pd_child_privilege = p2.pd_privilege
            and not exists ( select * 
                               from dnm_privilege_hierarchy_map p3
                               where p3.pd_privilege = p1.pd_privilege
                                 and p3.pd_child_privilege = p2.pd_child_privilege)
      ;
      if not found then
        exit;
      end if;
    end loop;
    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_privileges_sync_dnm_privileges()
  returns integer as '
  declare
    v_priv_col_name varchar(200);
    c record;
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
    for c in select pd_privilege from dnm_privilege_col_map for update loop
      select column_name into v_priv_col_name
        from dnm_privilege_col_map where pd_privilege = c.pd_privilege;
     
      execute ''update dnm_privileges set ''|| v_priv_col_name || '' = 1 '' 
                        || '' where pd_privilege = ''''''||c.pd_privilege||'''''''';
    end loop;


    for c in select pd_privilege from dnm_privileges loop
      update dnm_privileges
        set pd_priv_01 = t1.pd_priv_01,
	pd_priv_02 = t1.pd_priv_02,
	pd_priv_03 = t1.pd_priv_03,
	pd_priv_04 = t1.pd_priv_04,
	pd_priv_05 = t1.pd_priv_05,
        pd_priv_06 = t1.pd_priv_06,
	pd_priv_07 = t1.pd_priv_07,
	pd_priv_08 = t1.pd_priv_08,
	pd_priv_09 = t1.pd_priv_09,
	pd_priv_10 = t1.pd_priv_10,
        pd_priv_11 = t1.pd_priv_11,
	pd_priv_12 = t1.pd_priv_12,
	pd_priv_13 = t1.pd_priv_13,
	pd_priv_14 = t1.pd_priv_14,
	pd_priv_15 = t1.pd_priv_15,
	pd_priv_16 = t1.pd_priv_16,
	pd_priv_17 = t1.pd_priv_17,
	pd_priv_18 = t1.pd_priv_18,
	pd_priv_19 = t1.pd_priv_19,
	pd_priv_20 = t1.pd_priv_20,
	pd_priv_21 = t1.pd_priv_21,
	pd_priv_22 = t1.pd_priv_22,
	pd_priv_23 = t1.pd_priv_23,
	pd_priv_24 = t1.pd_priv_24,
	pd_priv_25 = t1.pd_priv_25,
	pd_priv_26 = t1.pd_priv_26,
	pd_priv_27 = t1.pd_priv_27,
	pd_priv_28 = t1.pd_priv_28,
	pd_priv_29 = t1.pd_priv_29,
	pd_priv_30 = t1.pd_priv_30,
	pd_priv_31 = t1.pd_priv_31,
	pd_priv_32 = t1.pd_priv_32,
	pd_priv_33 = t1.pd_priv_33,
	pd_priv_34 = t1.pd_priv_34,
	pd_priv_35 = t1.pd_priv_35,
	pd_priv_36 = t1.pd_priv_36,
	pd_priv_37 = t1.pd_priv_37,
	pd_priv_38 = t1.pd_priv_38,
	pd_priv_39 = t1.pd_priv_39,
	pd_priv_40 = t1.pd_priv_40,
	pd_priv_41 = t1.pd_priv_41,
	pd_priv_42 = t1.pd_priv_42,
	pd_priv_43 = t1.pd_priv_43,
	pd_priv_44 = t1.pd_priv_44,
	pd_priv_45 = t1.pd_priv_45,
	pd_priv_46 = t1.pd_priv_46,
	pd_priv_47 = t1.pd_priv_47,
	pd_priv_48 = t1.pd_priv_48,
	pd_priv_49 = t1.pd_priv_49,
	pd_priv_50 = t1.pd_priv_50,
	pd_priv_51 = t1.pd_priv_51,
	pd_priv_52 = t1.pd_priv_52,
	pd_priv_53 = t1.pd_priv_53,
	pd_priv_54 = t1.pd_priv_54,
	pd_priv_55 = t1.pd_priv_55,
	pd_priv_56 = t1.pd_priv_56,
	pd_priv_57 = t1.pd_priv_57,
	pd_priv_58 = t1.pd_priv_58,
	pd_priv_59 = t1.pd_priv_59,
	pd_priv_60 = t1.pd_priv_60,
	pd_priv_61 = t1.pd_priv_61,
	pd_priv_62 = t1.pd_priv_62,
	pd_priv_63 = t1.pd_priv_63,
	pd_priv_64 = t1.pd_priv_64,
	pd_priv_65 = t1.pd_priv_65,
	pd_priv_66 = t1.pd_priv_66,
	pd_priv_67 = t1.pd_priv_67,
	pd_priv_68 = t1.pd_priv_68,
	pd_priv_69 = t1.pd_priv_69,
	pd_priv_70 = t1.pd_priv_70,
	pd_priv_71 = t1.pd_priv_71,
	pd_priv_72 = t1.pd_priv_72,
	pd_priv_73 = t1.pd_priv_73,
	pd_priv_74 = t1.pd_priv_74,
	pd_priv_75 = t1.pd_priv_75,
	pd_priv_76 = t1.pd_priv_76,
	pd_priv_77 = t1.pd_priv_77,
	pd_priv_78 = t1.pd_priv_78,
	pd_priv_79 = t1.pd_priv_79,
	pd_priv_80 = t1.pd_priv_80,
	pd_priv_81 = t1.pd_priv_81,
	pd_priv_82 = t1.pd_priv_82,
	pd_priv_83 = t1.pd_priv_83,
	pd_priv_84 = t1.pd_priv_84,
	pd_priv_85 = t1.pd_priv_85,
	pd_priv_86 = t1.pd_priv_86,
	pd_priv_87 = t1.pd_priv_87,
	pd_priv_88 = t1.pd_priv_88,
	pd_priv_89 = t1.pd_priv_89,
	pd_priv_90 = t1.pd_priv_90,
	pd_priv_91 = t1.pd_priv_91,
	pd_priv_92 = t1.pd_priv_92,
	pd_priv_93 = t1.pd_priv_93,
	pd_priv_94 = t1.pd_priv_94,
	pd_priv_95 = t1.pd_priv_95,
	pd_priv_96 = t1.pd_priv_96,
	pd_priv_97 = t1.pd_priv_97,
	pd_priv_98 = t1.pd_priv_98,
	pd_priv_99 = t1.pd_priv_99
      from (select 
           max(pd_priv_01) as pd_priv_01,
	   max(pd_priv_02) as pd_priv_02,
	   max(pd_priv_03) as pd_priv_03,
	   max(pd_priv_04) as pd_priv_04,
	   max(pd_priv_05) as pd_priv_05,
           max(pd_priv_06) as pd_priv_06,
	   max(pd_priv_07) as pd_priv_07,
	   max(pd_priv_08) as pd_priv_08,
	   max(pd_priv_09) as pd_priv_09,
	   max(pd_priv_10) as pd_priv_10,
	   max(pd_priv_11) as pd_priv_11,
	   max(pd_priv_12) as pd_priv_12,
	   max(pd_priv_13) as pd_priv_13,
	   max(pd_priv_14) as pd_priv_14,
	   max(pd_priv_15) as pd_priv_15,
           max(pd_priv_16) as pd_priv_16,
	   max(pd_priv_17) as pd_priv_17,
	   max(pd_priv_18) as pd_priv_18,
	   max(pd_priv_19) as pd_priv_19,
	   max(pd_priv_20) as pd_priv_20,
	   max(pd_priv_21) as pd_priv_21,
	   max(pd_priv_22) as pd_priv_22,
	   max(pd_priv_23) as pd_priv_23,
	   max(pd_priv_24) as pd_priv_24,
	   max(pd_priv_25) as pd_priv_25,
           max(pd_priv_26) as pd_priv_26,
	   max(pd_priv_27) as pd_priv_27,
	   max(pd_priv_28) as pd_priv_28,
	   max(pd_priv_29) as pd_priv_29,
	   max(pd_priv_30) as pd_priv_30,
	   max(pd_priv_31) as pd_priv_31,
	   max(pd_priv_32) as pd_priv_32,
	   max(pd_priv_33) as pd_priv_33,
	   max(pd_priv_34) as pd_priv_34,
	   max(pd_priv_35) as pd_priv_35,
           max(pd_priv_36) as pd_priv_36,
	   max(pd_priv_37) as pd_priv_37,
	   max(pd_priv_38) as pd_priv_38,
	   max(pd_priv_39) as pd_priv_39,
	   max(pd_priv_40) as pd_priv_40,
	   max(pd_priv_41) as pd_priv_41,
	   max(pd_priv_42) as pd_priv_42,
	   max(pd_priv_43) as pd_priv_43,
	   max(pd_priv_44) as pd_priv_44,
	   max(pd_priv_45) as pd_priv_45,
           max(pd_priv_46) as pd_priv_46,
	   max(pd_priv_47) as pd_priv_47,
	   max(pd_priv_48) as pd_priv_48,
	   max(pd_priv_49) as pd_priv_49,
	   max(pd_priv_50) as pd_priv_50,
	   max(pd_priv_51) as pd_priv_51,
	   max(pd_priv_52) as pd_priv_52,
	   max(pd_priv_53) as pd_priv_53,
	   max(pd_priv_54) as pd_priv_54,
	   max(pd_priv_55) as pd_priv_55,
           max(pd_priv_56) as pd_priv_56,
	   max(pd_priv_57) as pd_priv_57,
	   max(pd_priv_58) as pd_priv_58,
	   max(pd_priv_59) as pd_priv_59,
	   max(pd_priv_60) as pd_priv_60,
	   max(pd_priv_61) as pd_priv_61,
	   max(pd_priv_62) as pd_priv_62,
	   max(pd_priv_63) as pd_priv_63,
	   max(pd_priv_64) as pd_priv_64,
	   max(pd_priv_65) as pd_priv_65,
           max(pd_priv_66) as pd_priv_66,
	   max(pd_priv_67) as pd_priv_67,
	   max(pd_priv_68) as pd_priv_68,
	   max(pd_priv_69) as pd_priv_69,
	   max(pd_priv_70) as pd_priv_70,
	   max(pd_priv_71) as pd_priv_71,
	   max(pd_priv_72) as pd_priv_72,
	   max(pd_priv_73) as pd_priv_73,
	   max(pd_priv_74) as pd_priv_74,
	   max(pd_priv_75) as pd_priv_75,
           max(pd_priv_76) as pd_priv_76,
	   max(pd_priv_77) as pd_priv_77,
	   max(pd_priv_78) as pd_priv_78,
	   max(pd_priv_79) as pd_priv_79,
	   max(pd_priv_80) as pd_priv_80,
	   max(pd_priv_81) as pd_priv_81,
	   max(pd_priv_82) as pd_priv_82,
	   max(pd_priv_83) as pd_priv_83,
	   max(pd_priv_84) as pd_priv_84,
	   max(pd_priv_85) as pd_priv_85,
           max(pd_priv_86) as pd_priv_86,
	   max(pd_priv_87) as pd_priv_87,
	   max(pd_priv_88) as pd_priv_88,
	   max(pd_priv_89) as pd_priv_89,
	   max(pd_priv_90) as pd_priv_90,
	   max(pd_priv_91) as pd_priv_91,
	   max(pd_priv_92) as pd_priv_92,
	   max(pd_priv_93) as pd_priv_93,
	   max(pd_priv_94) as pd_priv_94,
	   max(pd_priv_95) as pd_priv_95,
           max(pd_priv_96) as pd_priv_96,
	   max(pd_priv_97) as pd_priv_97,
	   max(pd_priv_98) as pd_priv_98,
	   max(pd_priv_99) as pd_priv_99 
           from dnm_privileges p
   	   where pd_privilege in (select pd_child_privilege 
		                    from dnm_privilege_hierarchy_map ph
				    where pd_privilege = c.pd_privilege
                                  )) t1
        where pd_privilege = c.pd_privilege
      ;

    end loop;
    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_privileges_sync_dnm_permissions()
  returns integer as '
  declare
    c record;
  begin

      execute ''update dnm_permissions 
      set pd_priv_01 = null, pd_priv_02 = null, pd_priv_03 = null,
          pd_priv_04 = null, pd_priv_05 = null, pd_priv_06 = null,
          pd_priv_07 = null, pd_priv_08 = null, pd_priv_09 = null,
          pd_priv_10 = null, pd_priv_11 = null, pd_priv_12 = null,
          pd_priv_13 = null, pd_priv_14 = null, pd_priv_15 = null,
          pd_priv_16 = null, pd_priv_17 = null, pd_priv_18 = null,
          pd_priv_19 = null, pd_priv_20 = null, pd_priv_21 = null,
          pd_priv_22 = null, pd_priv_23 = null, pd_priv_24 = null,
          pd_priv_25 = null, pd_priv_26 = null, pd_priv_27 = null,
          pd_priv_28 = null, pd_priv_29 = null, pd_priv_30 = null,
          pd_priv_31 = null, pd_priv_32 = null, pd_priv_33 = null,
          pd_priv_34 = null, pd_priv_35 = null, pd_priv_36 = null,
          pd_priv_37 = null, pd_priv_38 = null, pd_priv_39 = null,
          pd_priv_40 = null, pd_priv_41 = null, pd_priv_42 = null,
          pd_priv_43 = null, pd_priv_44 = null, pd_priv_45 = null,
          pd_priv_46 = null, pd_priv_47 = null, pd_priv_48 = null,
          pd_priv_49 = null, pd_priv_50 = null, pd_priv_51 = null,
          pd_priv_52 = null, pd_priv_53 = null, pd_priv_54 = null,
          pd_priv_55 = null, pd_priv_56 = null, pd_priv_57 = null,
          pd_priv_58 = null, pd_priv_59 = null, pd_priv_60 = null,
          pd_priv_61 = null, pd_priv_62 = null, pd_priv_63 = null,
          pd_priv_64 = null, pd_priv_65 = null, pd_priv_66 = null,
          pd_priv_67 = null, pd_priv_68 = null, pd_priv_69 = null,
          pd_priv_70 = null, pd_priv_71 = null, pd_priv_72 = null,
          pd_priv_73 = null, pd_priv_74 = null, pd_priv_75 = null,
          pd_priv_76 = null, pd_priv_77 = null, pd_priv_78 = null,
          pd_priv_79 = null, pd_priv_80 = null, pd_priv_81 = null,
          pd_priv_82 = null, pd_priv_83 = null, pd_priv_84 = null,
          pd_priv_85 = null, pd_priv_86 = null, pd_priv_87 = null,
          pd_priv_88 = null, pd_priv_89 = null, pd_priv_90 = null,
          pd_priv_91 = null, pd_priv_92 = null, pd_priv_93 = null,
          pd_priv_94 = null, pd_priv_95 = null, pd_priv_96 = null,
          pd_priv_97 = null, pd_priv_98 = null, pd_priv_99 = null ''
    ;


    for c in select pd_object_id, pd_priv_list, pd_grantee_id from dnm_permissions loop

      execute ''update dnm_permissions
        set pd_priv_01 = t1.pd_priv_01,
	pd_priv_02 = t1.pd_priv_02,
	pd_priv_03 = t1.pd_priv_03,
	pd_priv_04 = t1.pd_priv_04,
	pd_priv_05 = t1.pd_priv_05,
        pd_priv_06 = t1.pd_priv_06,
	pd_priv_07 = t1.pd_priv_07,
	pd_priv_08 = t1.pd_priv_08,
	pd_priv_09 = t1.pd_priv_09,
	pd_priv_10 = t1.pd_priv_10,
        pd_priv_11 = t1.pd_priv_11,
	pd_priv_12 = t1.pd_priv_12,
	pd_priv_13 = t1.pd_priv_13,
	pd_priv_14 = t1.pd_priv_14,
	pd_priv_15 = t1.pd_priv_15,
	pd_priv_16 = t1.pd_priv_16,
	pd_priv_17 = t1.pd_priv_17,
	pd_priv_18 = t1.pd_priv_18,
	pd_priv_19 = t1.pd_priv_19,
	pd_priv_20 = t1.pd_priv_20,
	pd_priv_21 = t1.pd_priv_21,
	pd_priv_22 = t1.pd_priv_22,
	pd_priv_23 = t1.pd_priv_23,
	pd_priv_24 = t1.pd_priv_24,
	pd_priv_25 = t1.pd_priv_25,
	pd_priv_26 = t1.pd_priv_26,
	pd_priv_27 = t1.pd_priv_27,
	pd_priv_28 = t1.pd_priv_28,
	pd_priv_29 = t1.pd_priv_29,
	pd_priv_30 = t1.pd_priv_30,
	pd_priv_31 = t1.pd_priv_31,
	pd_priv_32 = t1.pd_priv_32,
	pd_priv_33 = t1.pd_priv_33,
	pd_priv_34 = t1.pd_priv_34,
	pd_priv_35 = t1.pd_priv_35,
	pd_priv_36 = t1.pd_priv_36,
	pd_priv_37 = t1.pd_priv_37,
	pd_priv_38 = t1.pd_priv_38,
	pd_priv_39 = t1.pd_priv_39,
	pd_priv_40 = t1.pd_priv_40,
	pd_priv_41 = t1.pd_priv_41,
	pd_priv_42 = t1.pd_priv_42,
	pd_priv_43 = t1.pd_priv_43,
	pd_priv_44 = t1.pd_priv_44,
	pd_priv_45 = t1.pd_priv_45,
	pd_priv_46 = t1.pd_priv_46,
	pd_priv_47 = t1.pd_priv_47,
	pd_priv_48 = t1.pd_priv_48,
	pd_priv_49 = t1.pd_priv_49,
	pd_priv_50 = t1.pd_priv_50,
	pd_priv_51 = t1.pd_priv_51,
	pd_priv_52 = t1.pd_priv_52,
	pd_priv_53 = t1.pd_priv_53,
	pd_priv_54 = t1.pd_priv_54,
	pd_priv_55 = t1.pd_priv_55,
	pd_priv_56 = t1.pd_priv_56,
	pd_priv_57 = t1.pd_priv_57,
	pd_priv_58 = t1.pd_priv_58,
	pd_priv_59 = t1.pd_priv_59,
	pd_priv_60 = t1.pd_priv_60,
	pd_priv_61 = t1.pd_priv_61,
	pd_priv_62 = t1.pd_priv_62,
	pd_priv_63 = t1.pd_priv_63,
	pd_priv_64 = t1.pd_priv_64,
	pd_priv_65 = t1.pd_priv_65,
	pd_priv_66 = t1.pd_priv_66,
	pd_priv_67 = t1.pd_priv_67,
	pd_priv_68 = t1.pd_priv_68,
	pd_priv_69 = t1.pd_priv_69,
	pd_priv_70 = t1.pd_priv_70,
	pd_priv_71 = t1.pd_priv_71,
	pd_priv_72 = t1.pd_priv_72,
	pd_priv_73 = t1.pd_priv_73,
	pd_priv_74 = t1.pd_priv_74,
	pd_priv_75 = t1.pd_priv_75,
	pd_priv_76 = t1.pd_priv_76,
	pd_priv_77 = t1.pd_priv_77,
	pd_priv_78 = t1.pd_priv_78,
	pd_priv_79 = t1.pd_priv_79,
	pd_priv_80 = t1.pd_priv_80,
	pd_priv_81 = t1.pd_priv_81,
	pd_priv_82 = t1.pd_priv_82,
	pd_priv_83 = t1.pd_priv_83,
	pd_priv_84 = t1.pd_priv_84,
	pd_priv_85 = t1.pd_priv_85,
	pd_priv_86 = t1.pd_priv_86,
	pd_priv_87 = t1.pd_priv_87,
	pd_priv_88 = t1.pd_priv_88,
	pd_priv_89 = t1.pd_priv_89,
	pd_priv_90 = t1.pd_priv_90,
	pd_priv_91 = t1.pd_priv_91,
	pd_priv_92 = t1.pd_priv_92,
	pd_priv_93 = t1.pd_priv_93,
	pd_priv_94 = t1.pd_priv_94,
	pd_priv_95 = t1.pd_priv_95,
	pd_priv_96 = t1.pd_priv_96,
	pd_priv_97 = t1.pd_priv_97,
	pd_priv_98 = t1.pd_priv_98,
	pd_priv_99 = t1.pd_priv_99
      from ( select
           max(pd_priv_01) as pd_priv_01,
	   max(pd_priv_02) as pd_priv_02,
	   max(pd_priv_03) as pd_priv_03,
	   max(pd_priv_04) as pd_priv_04,
	   max(pd_priv_05) as pd_priv_05,
           max(pd_priv_06) as pd_priv_06,
	   max(pd_priv_07) as pd_priv_07,
	   max(pd_priv_08) as pd_priv_08,
	   max(pd_priv_09) as pd_priv_09,
	   max(pd_priv_10) as pd_priv_10,
	   max(pd_priv_11) as pd_priv_11,
	   max(pd_priv_12) as pd_priv_12,
	   max(pd_priv_13) as pd_priv_13,
	   max(pd_priv_14) as pd_priv_14,
	   max(pd_priv_15) as pd_priv_15,
           max(pd_priv_16) as pd_priv_16,
	   max(pd_priv_17) as pd_priv_17,
	   max(pd_priv_18) as pd_priv_18,
	   max(pd_priv_19) as pd_priv_19,
	   max(pd_priv_20) as pd_priv_20,
	   max(pd_priv_21) as pd_priv_21,
	   max(pd_priv_22) as pd_priv_22,
	   max(pd_priv_23) as pd_priv_23,
	   max(pd_priv_24) as pd_priv_24,
	   max(pd_priv_25) as pd_priv_25,
           max(pd_priv_26) as pd_priv_26,
	   max(pd_priv_27) as pd_priv_27,
	   max(pd_priv_28) as pd_priv_28,
	   max(pd_priv_29) as pd_priv_29,
	   max(pd_priv_30) as pd_priv_30,
	   max(pd_priv_31) as pd_priv_31,
	   max(pd_priv_32) as pd_priv_32,
	   max(pd_priv_33) as pd_priv_33,
	   max(pd_priv_34) as pd_priv_34,
	   max(pd_priv_35) as pd_priv_35,
           max(pd_priv_36) as pd_priv_36,
	   max(pd_priv_37) as pd_priv_37,
	   max(pd_priv_38) as pd_priv_38,
	   max(pd_priv_39) as pd_priv_39,
	   max(pd_priv_40) as pd_priv_40,
	   max(pd_priv_41) as pd_priv_41,
	   max(pd_priv_42) as pd_priv_42,
	   max(pd_priv_43) as pd_priv_43,
	   max(pd_priv_44) as pd_priv_44,
	   max(pd_priv_45) as pd_priv_45,
           max(pd_priv_46) as pd_priv_46,
	   max(pd_priv_47) as pd_priv_47,
	   max(pd_priv_48) as pd_priv_48,
	   max(pd_priv_49) as pd_priv_49,
	   max(pd_priv_50) as pd_priv_50,
	   max(pd_priv_51) as pd_priv_51,
	   max(pd_priv_52) as pd_priv_52,
	   max(pd_priv_53) as pd_priv_53,
	   max(pd_priv_54) as pd_priv_54,
	   max(pd_priv_55) as pd_priv_55,
           max(pd_priv_56) as pd_priv_56,
	   max(pd_priv_57) as pd_priv_57,
	   max(pd_priv_58) as pd_priv_58,
	   max(pd_priv_59) as pd_priv_59,
	   max(pd_priv_60) as pd_priv_60,
	   max(pd_priv_61) as pd_priv_61,
	   max(pd_priv_62) as pd_priv_62,
	   max(pd_priv_63) as pd_priv_63,
	   max(pd_priv_64) as pd_priv_64,
	   max(pd_priv_65) as pd_priv_65,
           max(pd_priv_66) as pd_priv_66,
	   max(pd_priv_67) as pd_priv_67,
	   max(pd_priv_68) as pd_priv_68,
	   max(pd_priv_69) as pd_priv_69,
	   max(pd_priv_70) as pd_priv_70,
	   max(pd_priv_71) as pd_priv_71,
	   max(pd_priv_72) as pd_priv_72,
	   max(pd_priv_73) as pd_priv_73,
	   max(pd_priv_74) as pd_priv_74,
	   max(pd_priv_75) as pd_priv_75,
           max(pd_priv_76) as pd_priv_76,
	   max(pd_priv_77) as pd_priv_77,
	   max(pd_priv_78) as pd_priv_78,
	   max(pd_priv_79) as pd_priv_79,
	   max(pd_priv_80) as pd_priv_80,
	   max(pd_priv_81) as pd_priv_81,
	   max(pd_priv_82) as pd_priv_82,
	   max(pd_priv_83) as pd_priv_83,
	   max(pd_priv_84) as pd_priv_84,
	   max(pd_priv_85) as pd_priv_85,
           max(pd_priv_86) as pd_priv_86,
	   max(pd_priv_87) as pd_priv_87,
	   max(pd_priv_88) as pd_priv_88,
	   max(pd_priv_89) as pd_priv_89,
	   max(pd_priv_90) as pd_priv_90,
	   max(pd_priv_91) as pd_priv_91,
	   max(pd_priv_92) as pd_priv_92,
	   max(pd_priv_93) as pd_priv_93,
	   max(pd_priv_94) as pd_priv_94,
	   max(pd_priv_95) as pd_priv_95,
           max(pd_priv_96) as pd_priv_96,
	   max(pd_priv_97) as pd_priv_97,
	   max(pd_priv_98) as pd_priv_98,
	   max(pd_priv_99) as pd_priv_99
         from dnm_privileges p 
         where p.pd_privilege in ('''''' ||  
	  replace(trim('','' from c.pd_priv_list),'','', '''''','''''') || '''''')) t1
      where pd_object_id = '' || c.pd_object_id || ''
        and pd_grantee_id = '' || c.pd_grantee_id;
    end loop;

    return null;
  end; ' language 'plpgsql'
;


create or replace function dnm_privileges_add_privilege (
  varchar)
  returns integer as '
  declare 
    privilege alias for $1;
    i integer; 
    v_column_name varchar(100);
    c integer;
  begin
    i := 1; 
    v_column_name := ''pd_priv_01'';

    -- look for first free column name for new privilege
    loop
      select count(1) into c from dnm_privilege_col_map 
        where column_name = v_column_name;
      if c = 0 then
         exit;
      else 
	 i := i + 1;
	 v_column_name := ''pd_priv_'' || trim(to_char(i,''09''));
      end if;
    end loop;

    insert into dnm_privilege_col_map (pd_privilege, column_name)
      values(privilege, v_column_name);
    
    execute ''insert into dnm_privileges (pd_privilege, '' ||
      v_column_name || '') values (''''''||privilege||'''''',1)'';

    insert into dnm_privilege_hierarchy_map 
      (pd_privilege, pd_child_privilege)
      values (privilege, privilege)
    ;

    return null;
  end ; ' language 'plpgsql'
;

create or replace function dnm_privileges_sync_permission_columns(
  integer, integer, varchar) 
  returns integer as '
  declare
    p_object_id alias for $1;
    p_grantee_id alias for $2;
    p_priv_list alias for $3;
    sql_stmt varchar(10000);
  begin
    sql_stmt :=  ''update dnm_permissions 
      set pd_priv_01 = t1.pd_priv_01,
	pd_priv_02 = t1.pd_priv_02,
	pd_priv_03 = t1.pd_priv_03,
	pd_priv_04 = t1.pd_priv_04,
	pd_priv_05 = t1.pd_priv_05,
        pd_priv_06 = t1.pd_priv_06,
	pd_priv_07 = t1.pd_priv_07,
	pd_priv_08 = t1.pd_priv_08,
	pd_priv_09 = t1.pd_priv_09,
	pd_priv_10 = t1.pd_priv_10,
        pd_priv_11 = t1.pd_priv_11,
	pd_priv_12 = t1.pd_priv_12,
	pd_priv_13 = t1.pd_priv_13,
	pd_priv_14 = t1.pd_priv_14,
	pd_priv_15 = t1.pd_priv_15,
	pd_priv_16 = t1.pd_priv_16,
	pd_priv_17 = t1.pd_priv_17,
	pd_priv_18 = t1.pd_priv_18,
	pd_priv_19 = t1.pd_priv_19,
	pd_priv_20 = t1.pd_priv_20,
	pd_priv_21 = t1.pd_priv_21,
	pd_priv_22 = t1.pd_priv_22,
	pd_priv_23 = t1.pd_priv_23,
	pd_priv_24 = t1.pd_priv_24,
	pd_priv_25 = t1.pd_priv_25,
	pd_priv_26 = t1.pd_priv_26,
	pd_priv_27 = t1.pd_priv_27,
	pd_priv_28 = t1.pd_priv_28,
	pd_priv_29 = t1.pd_priv_29,
	pd_priv_30 = t1.pd_priv_30,
	pd_priv_31 = t1.pd_priv_31,
	pd_priv_32 = t1.pd_priv_32,
	pd_priv_33 = t1.pd_priv_33,
	pd_priv_34 = t1.pd_priv_34,
	pd_priv_35 = t1.pd_priv_35,
	pd_priv_36 = t1.pd_priv_36,
	pd_priv_37 = t1.pd_priv_37,
	pd_priv_38 = t1.pd_priv_38,
	pd_priv_39 = t1.pd_priv_39,
	pd_priv_40 = t1.pd_priv_40,
	pd_priv_41 = t1.pd_priv_41,
	pd_priv_42 = t1.pd_priv_42,
	pd_priv_43 = t1.pd_priv_43,
	pd_priv_44 = t1.pd_priv_44,
	pd_priv_45 = t1.pd_priv_45,
	pd_priv_46 = t1.pd_priv_46,
	pd_priv_47 = t1.pd_priv_47,
	pd_priv_48 = t1.pd_priv_48,
	pd_priv_49 = t1.pd_priv_49,
	pd_priv_50 = t1.pd_priv_50,
	pd_priv_51 = t1.pd_priv_51,
	pd_priv_52 = t1.pd_priv_52,
	pd_priv_53 = t1.pd_priv_53,
	pd_priv_54 = t1.pd_priv_54,
	pd_priv_55 = t1.pd_priv_55,
	pd_priv_56 = t1.pd_priv_56,
	pd_priv_57 = t1.pd_priv_57,
	pd_priv_58 = t1.pd_priv_58,
	pd_priv_59 = t1.pd_priv_59,
	pd_priv_60 = t1.pd_priv_60,
	pd_priv_61 = t1.pd_priv_61,
	pd_priv_62 = t1.pd_priv_62,
	pd_priv_63 = t1.pd_priv_63,
	pd_priv_64 = t1.pd_priv_64,
	pd_priv_65 = t1.pd_priv_65,
	pd_priv_66 = t1.pd_priv_66,
	pd_priv_67 = t1.pd_priv_67,
	pd_priv_68 = t1.pd_priv_68,
	pd_priv_69 = t1.pd_priv_69,
	pd_priv_70 = t1.pd_priv_70,
	pd_priv_71 = t1.pd_priv_71,
	pd_priv_72 = t1.pd_priv_72,
	pd_priv_73 = t1.pd_priv_73,
	pd_priv_74 = t1.pd_priv_74,
	pd_priv_75 = t1.pd_priv_75,
	pd_priv_76 = t1.pd_priv_76,
	pd_priv_77 = t1.pd_priv_77,
	pd_priv_78 = t1.pd_priv_78,
	pd_priv_79 = t1.pd_priv_79,
	pd_priv_80 = t1.pd_priv_80,
	pd_priv_81 = t1.pd_priv_81,
	pd_priv_82 = t1.pd_priv_82,
	pd_priv_83 = t1.pd_priv_83,
	pd_priv_84 = t1.pd_priv_84,
	pd_priv_85 = t1.pd_priv_85,
	pd_priv_86 = t1.pd_priv_86,
	pd_priv_87 = t1.pd_priv_87,
	pd_priv_88 = t1.pd_priv_88,
	pd_priv_89 = t1.pd_priv_89,
	pd_priv_90 = t1.pd_priv_90,
	pd_priv_91 = t1.pd_priv_91,
	pd_priv_92 = t1.pd_priv_92,
	pd_priv_93 = t1.pd_priv_93,
	pd_priv_94 = t1.pd_priv_94,
	pd_priv_95 = t1.pd_priv_95,
	pd_priv_96 = t1.pd_priv_96,
	pd_priv_97 = t1.pd_priv_97,
	pd_priv_98 = t1.pd_priv_98,
	pd_priv_99 = t1.pd_priv_99
      from (select 
	 max(pd_priv_01) as pd_priv_01,
	 max(pd_priv_02) as pd_priv_02,
	 max(pd_priv_03) as pd_priv_03,
	 max(pd_priv_04) as pd_priv_04,
	 max(pd_priv_05) as pd_priv_05,
	 max(pd_priv_06) as pd_priv_06,
	 max(pd_priv_07) as pd_priv_07,
	 max(pd_priv_08) as pd_priv_08,
	 max(pd_priv_09) as pd_priv_09,
	 max(pd_priv_10) as pd_priv_10,
	 max(pd_priv_11) as pd_priv_11,
	 max(pd_priv_12) as pd_priv_12,
	 max(pd_priv_13) as pd_priv_13,
	 max(pd_priv_14) as pd_priv_14,
	 max(pd_priv_15) as pd_priv_15,
	 max(pd_priv_16) as pd_priv_16,
	 max(pd_priv_17) as pd_priv_17,
	 max(pd_priv_18) as pd_priv_18,
	 max(pd_priv_19) as pd_priv_19,
	 max(pd_priv_20) as pd_priv_20,
	 max(pd_priv_21) as pd_priv_21,
	 max(pd_priv_22) as pd_priv_22,
	 max(pd_priv_23) as pd_priv_23,
	 max(pd_priv_24) as pd_priv_24,
	 max(pd_priv_25) as pd_priv_25,
	 max(pd_priv_26) as pd_priv_26,
	 max(pd_priv_27) as pd_priv_27,
	 max(pd_priv_28) as pd_priv_28,
	 max(pd_priv_29) as pd_priv_29,
	 max(pd_priv_30) as pd_priv_30,
	 max(pd_priv_31) as pd_priv_31,
	 max(pd_priv_32) as pd_priv_32,
	 max(pd_priv_33) as pd_priv_33,
	 max(pd_priv_34) as pd_priv_34,
	 max(pd_priv_35) as pd_priv_35,
	 max(pd_priv_36) as pd_priv_36,
	 max(pd_priv_37) as pd_priv_37,
	 max(pd_priv_38) as pd_priv_38,
	 max(pd_priv_39) as pd_priv_39,
	 max(pd_priv_40) as pd_priv_40,
	 max(pd_priv_41) as pd_priv_41,
	 max(pd_priv_42) as pd_priv_42,
	 max(pd_priv_43) as pd_priv_43,
	 max(pd_priv_44) as pd_priv_44,
	 max(pd_priv_45) as pd_priv_45,
	 max(pd_priv_46) as pd_priv_46,
	 max(pd_priv_47) as pd_priv_47,
	 max(pd_priv_48) as pd_priv_48,
	 max(pd_priv_49) as pd_priv_49,
	 max(pd_priv_50) as pd_priv_50,
	 max(pd_priv_51) as pd_priv_51,
	 max(pd_priv_52) as pd_priv_52,
	 max(pd_priv_53) as pd_priv_53,
	 max(pd_priv_54) as pd_priv_54,
	 max(pd_priv_55) as pd_priv_55,
	 max(pd_priv_56) as pd_priv_56,
	 max(pd_priv_57) as pd_priv_57,
	 max(pd_priv_58) as pd_priv_58,
	 max(pd_priv_59) as pd_priv_59,
	 max(pd_priv_60) as pd_priv_60,
	 max(pd_priv_61) as pd_priv_61,
	 max(pd_priv_62) as pd_priv_62,
	 max(pd_priv_63) as pd_priv_63,
	 max(pd_priv_64) as pd_priv_64,
	 max(pd_priv_65) as pd_priv_65,
	 max(pd_priv_66) as pd_priv_66,
	 max(pd_priv_67) as pd_priv_67,
	 max(pd_priv_68) as pd_priv_68,
	 max(pd_priv_69) as pd_priv_69,
	 max(pd_priv_70) as pd_priv_70,
	 max(pd_priv_71) as pd_priv_71,
	 max(pd_priv_72) as pd_priv_72,
	 max(pd_priv_73) as pd_priv_73,
	 max(pd_priv_74) as pd_priv_74,
	 max(pd_priv_75) as pd_priv_75,
	 max(pd_priv_76) as pd_priv_76,
	 max(pd_priv_77) as pd_priv_77,
	 max(pd_priv_78) as pd_priv_78,
	 max(pd_priv_79) as pd_priv_79,
	 max(pd_priv_80) as pd_priv_80,
	 max(pd_priv_81) as pd_priv_81,
	 max(pd_priv_82) as pd_priv_82,
	 max(pd_priv_83) as pd_priv_83,
	 max(pd_priv_84) as pd_priv_84,
	 max(pd_priv_85) as pd_priv_85,
	 max(pd_priv_86) as pd_priv_86,
	 max(pd_priv_87) as pd_priv_87,
	 max(pd_priv_88) as pd_priv_88,
	 max(pd_priv_89) as pd_priv_89,
	 max(pd_priv_90) as pd_priv_90,
	 max(pd_priv_91) as pd_priv_91,
	 max(pd_priv_92) as pd_priv_92,
	 max(pd_priv_93) as pd_priv_93,
	 max(pd_priv_94) as pd_priv_94,
	 max(pd_priv_95) as pd_priv_95,
	 max(pd_priv_96) as pd_priv_96,
	 max(pd_priv_97) as pd_priv_97,
	 max(pd_priv_98) as pd_priv_98,
	 max(pd_priv_99) as pd_priv_99 
       from dnm_privileges p 
        where p.pd_privilege in ('''''' ||   
	  replace(trim('','' from p_priv_list),'','', '''''','''''') || '''''')) t1
      where pd_object_id = '' || p_object_id || ''  
        and pd_grantee_id = '' || p_grantee_id; 

    execute sql_stmt;

    return null;
  end; ' language 'plpgsql'
;


create or replace function dnm_privileges_map_add_child_priv( 
  varchar, varchar ) 
  returns integer as '
  declare 
    p_privilege alias for $1;
    p_child_privilege alias for $2;
    c record;
  begin
    insert into dnm_privilege_hierarchy (pd_privilege, pd_child_privilege)
      values (p_privilege, p_child_privilege);

    -- sync dnm_privilege_hierarchy_map
    perform dnm_privileges_sync_hier_map();

    -- sync dnm_privileges table
    perform dnm_privileges_sync_dnm_privileges();
   
    -- updating dnm_permissions;
    for c in select ap.object_id, ap.grantee_id, dp.pd_priv_list 
                from acs_permissions ap, dnm_permissions dp
		where ap.privilege in (select pd_privilege 
			                from dnm_privilege_hierarchy_map 
				        where pd_child_privilege = p_child_privilege)
		  and ap.object_id = dp.pd_object_id 
                  and ap.grantee_id = dp.pd_grantee_id
		for update
    loop
       perform dnm_privileges_sync_permission_columns(c.object_id, c.grantee_id, c.pd_priv_list);
    end loop;

    return null;
  end; ' language 'plpgsql'
;





create or replace function dnm_privileges_delete_child_privilege(
  varchar, varchar)
  returns integer as '
  declare 
    p_privilege alias for $1;
    p_child_privilege alias for $2;
  begin
    -- remove rows from acs_privilege_hierarchy clone
    delete from dnm_privilege_hierarchy 
      where pd_privilege = p_privilege 
	and pd_child_privilege = p_child_privilege
    ;
    -- rebuild dnm_privilege_hierarchy_map
    perform dnm_privileges_sync_hier_map();

    -- rebuild dnm_privileges
    perform dnm_privileges_sync_dnm_privileges();
    -- update dnm_permissions
    perform dnm_privileges_sync_dnm_permissions();
    return null;
  end; ' language 'plpgsql'
;

create or replace function dnm_privileges_add_grant (
  integer, integer, varchar)
  returns integer as '
  declare 
    p_object_id alias for $1;
    p_grantee_id alias for $2;
    p_privilege alias for $3;
    v_pd_priv_list varchar(4000);
  begin

    update dnm_permissions 
      set pd_n_grants = pd_n_grants + 1, pd_priv_list = pd_priv_list || p_privilege  || '',''
      where pd_object_id = p_object_id 
	and pd_grantee_id = p_grantee_id
    ;

    if not FOUND then
      insert into dnm_permissions (pd_object_id, pd_grantee_id, pd_n_grants, pd_priv_list) 
        values (p_object_id, p_grantee_id, 1, '','' || p_privilege || '','')
      ;
    end if;

    select pd_priv_list into v_pd_priv_list 
      from dnm_permissions 
      where pd_object_id = p_object_id and pd_grantee_id = p_grantee_id
    ;

    perform dnm_privileges_sync_permission_columns(p_object_id, p_grantee_id, v_pd_priv_list);
    return null;
  end; ' language 'plpgsql'
;


create or replace function dnm_privileges_remove_grant (
  integer, integer, varchar) 
  returns integer as '
  declare
    p_object_id alias for $1;
    p_gratee_id alias for $2;
    p_privilege alias for $3;
    v_grants integer;
    v_pd_priv_list varchar(4000);
  begin
    select pd_n_grants into v_grants
      from dnm_permissions 
      where pd_object_id = p_object_id and pd_grantee_id = p_gratee_id
      for update;
    if v_grants > 1 then
      update dnm_permissions 
        set pd_n_grants = pd_n_grants -1, 
	    pd_priv_list = replace(pd_priv_list,'','' || p_privilege || '','', '','')
        where pd_object_id = p_object_id and pd_grantee_id = p_gratee_id
        ;
        select pd_priv_list into v_pd_priv_list
          from dnm_permissions 
          where pd_object_id = p_object_id
            and pd_grantee_id = p_gratee_id
        ;
	perform dnm_privileges_sync_permission_columns(p_object_id, p_gratee_id, v_pd_priv_list);
    else 
      delete from dnm_permissions 
        where  pd_object_id = p_object_id and pd_grantee_id = p_gratee_id;
    end if;
    return null;
  end; ' language 'plpgsql'
;


create or replace function dnm_privileges_delete_privilege(
  varchar)
  returns integer as '
  declare  
    privilege alias for $1;
  begin
    delete from dnm_privilege_hierarchy_map where pd_privilege = privilege;
    delete from dnm_privilege_col_map where pd_privilege = privilege;
    delete from dnm_privileges where pd_privilege = privilege;
    return null;
  end; ' language 'plpgsql'
;

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
-- $Id: package-hierarchy_denormalization.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace procedure hierarchy_add_item (v_item_id in INTEGER, 
                                                v_table in VARCHAR, 
                                                v_itemColumn in VARCHAR, 
                                                v_subitemColumn in VARCHAR)
is 
begin
    execute immediate 'insert into ' || v_table || ' ( ' || v_itemColumn || 
                      ', ' || v_subitemColumn || ', n_paths) values 
                      (' || v_item_id || ', ' || v_item_id || ', 0)';
end;
/



create or replace procedure hierarchy_add_subitem (v_item_id in INTEGER,
                                                   v_subitem_id in INTEGER,
                                                   v_table in VARCHAR,
                                                   v_itemColumn in VARCHAR, 
                                                   v_subitemColumn in VARCHAR)
  is 
    TYPE itemCursor IS REF CURSOR;
    v_path_increment integer;
    newEntry itemCursor;
    sql_stmt VARCHAR(4000);
    v_select_item_id integer;
    v_select_subitem_id integer;
    v_select_ancestor_n_paths integer;
    v_select_descendant_n_paths integer;
  begin
      sql_stmt := 'select ancestors.' || v_itemColumn || ' as item_id, 
                   descendants.' || v_subitemColumn || ' as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from ' || v_table || ' ancestors,
               ' || v_table || ' descendants
          where ancestors.' || v_subitemColumn || ' = ' || v_item_id || '
            and descendants.'|| v_itemColumn || ' = ' || v_subitem_id;

      OPEN newEntry FOR sql_stmt;
      LOOP
        FETCH newEntry INTO v_select_item_id, v_select_subitem_id, v_select_ancestor_n_paths, v_select_descendant_n_paths;
        EXIT WHEN newEntry%NOTFOUND;

          v_path_increment :=
              v_select_ancestor_n_paths * v_select_descendant_n_paths;

          execute immediate 'update ' || v_table ||  '
          set n_paths = n_paths + ' || v_path_increment || '
          where ' || v_itemColumn || ' = ' || v_select_item_id || '
            and ' || v_subitemColumn || ' = ' || v_select_subitem_id;

          if (SQL%NOTFOUND) then
              execute immediate 'insert into ' || v_table || '
              (' || v_itemColumn || ',' || v_subitemColumn || ', n_paths)
              values
              (' || v_select_item_id || ',' || v_select_subitem_id || ', ' ||
              v_path_increment || ')';
          end if;

      END LOOP;
      CLOSE newEntry;
 end;
/




create or replace procedure hierarchy_remove_subitem (v_item_id in INTEGER,
                                                   v_subitem_id in INTEGER,
                                                   v_table in VARCHAR,
                                                   v_itemColumn in VARCHAR, 
                                                   v_subitemColumn in VARCHAR)
  is 
    TYPE itemCursor IS REF CURSOR;
    v_path_decrement integer;
    newEntry itemCursor;
    sql_stmt VARCHAR(4000);
    v_select_item_id integer;
    v_select_subitem_id integer;
    v_select_ancestor_n_paths integer;
    v_select_descendant_n_paths integer;
  begin
      sql_stmt := 'select ancestors.' || v_itemColumn || ' as item_id, 
                   descendants.' || v_subitemColumn || ' as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from ' || v_table || ' ancestors,
               ' || v_table || ' descendants
          where ancestors.' || v_subitemColumn || ' = ' || v_item_id || '
            and descendants.'|| v_itemColumn || ' = ' || v_subitem_id;

      OPEN newEntry FOR sql_stmt;
      LOOP
        FETCH newEntry INTO v_select_item_id, v_select_subitem_id, v_select_ancestor_n_paths, v_select_descendant_n_paths;
        EXIT WHEN newEntry%NOTFOUND;

        v_path_decrement :=
            v_select_ancestor_n_paths * v_select_descendant_n_paths;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        execute immediate 'delete from ' || v_table || '
        where ' || v_itemColumn || ' = ' || v_select_item_id || '
          and ' || v_subitemColumn || ' = ' || v_select_subitem_id || '
          and n_paths <= ' || v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then
           execute immediate 'update ' || v_table || '
              set n_paths = n_paths - ' || v_path_decrement || '
            where ' || v_itemColumn || ' = ' || v_select_item_id || '
              and ' || v_subitemColumn || ' = ' || v_select_subitem_id;
        end if;
      END LOOP;
      CLOSE newEntry;
 end;
/

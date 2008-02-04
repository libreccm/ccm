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

create or replace function hierarchy_add_item (
    integer, varchar, varchar, varchar
  )
  returns integer as '
  declare
    v_item_id alias for $1;
    v_hierarchy_index alias for $2;
    v_itemColumn alias for $3;
    v_subitemColumn alias for $4;
  begin
      -- every item is a subitem of itself.
    execute ''insert into '' || quote_ident(v_hierarchy_index) || 
            '' ( '' || quote_ident(v_itemColumn) || '','' ||
            quote_ident(v_subitemColumn) || '','' ||
            '' n_paths) values ( '' || quote_literal(v_item_id) || '','' ||
            quote_literal(v_item_id) || '', 0)'';

  return null;
  end' language 'plpgsql';


create or replace function hierarchy_add_subitem (
     integer, integer, varchar, varchar, varchar
  )
  returns integer as '
  declare
    v_item_id alias for $1;
    v_subitem_id alias for $2;
    v_hierarchy_index alias for $3;
    v_itemColumn alias for $4;
    v_subitemColumn alias for $5;
    v_path_increment integer;
    new_entry record;
  begin

      for new_entry in 
          EXECUTE ''select ancestors.'' || quote_ident(v_itemColumn) || '' 
                  as item_id, 
                  descendants.'' || quote_ident(v_subitemColumn) || '' 
                  as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from '' || quote_ident(v_hierarchy_index) || '' ancestors,
               '' || quote_ident(v_hierarchy_index) || '' descendants
          where ancestors.'' || quote_ident(v_subitemColumn) || '' = '' || 
                quote_literal(v_item_id) || ''
            and descendants.'' || quote_ident(v_itemColumn) || '' = '' || 
                quote_literal(v_subitem_id) || ''''
      loop
          v_path_increment :=
              new_entry.ancestor_n_paths * new_entry.descendant_n_paths;

          -- This does both an update and an insert because the postegres
          -- execute statement in 7.2.1 is not smart enough to recognize
          -- an if statement.  By placing the update first, the path is
          -- set correctly.  If it is placed second then the path will
          -- be incorrect
          EXECUTE ''
             update '' || quote_ident(v_hierarchy_index) || ''
              set n_paths = n_paths + '' || quote_literal(v_path_increment) || ''
              where '' || quote_ident(v_itemColumn) || '' = '' || 
                          quote_literal(new_entry.item_id) || '' 
               and '' || quote_ident(v_subitemColumn) || ''  = '' || 
                          quote_literal(new_entry.subitem_id) || '';

              -- we do the insert with a select to make sure that
              -- we do not insert something that was just updated.
              -- it would be nice if we could use "if FOUND then" here
              -- instead of the subselect but EXECUTE does not like that
              insert into '' || quote_ident(v_hierarchy_index) || ''
              ('' || quote_ident(v_itemColumn) || '', 
              '' || quote_ident(v_subitemColumn) || '', n_paths)
              select
              '' || quote_literal(new_entry.item_id) || '', '' || 
                quote_literal(new_entry.subitem_id) || '', '' || 
                quote_literal(v_path_increment) || '' from dual 
              where not exists (select 1 from '' || 
                         quote_ident(v_hierarchy_index) || ''
                     where '' || quote_ident(v_itemColumn) || '' = '' || 
                                 quote_literal(new_entry.item_id) || ''
                       and '' || quote_ident(v_subitemColumn) || '' = '' || 
                                 quote_literal(new_entry.subitem_id) || '');
          '';
      end loop;

  return null;
  end;' language 'plpgsql';



create or replace function hierarchy_remove_subitem (
        integer, integer, varchar, varchar, varchar
  )
  returns integer as '
  declare
    v_item_id alias for $1;
    v_subitem_id alias for $2;
    v_hierarchy_index alias for $3;
    v_itemColumn alias for $4;
    v_subitemColumn alias for $5;
    v_path_decrement integer;
    remove_entry record;
  begin

      for remove_entry in 
          EXECUTE ''select ancestors.'' || quote_ident(v_itemColumn) || '' 
                  as item_id, 
                  descendants.'' || quote_ident(v_subitemColumn) || '' 
                  as subitem_id,
                  (case when ancestors.n_paths = 0
                        then 1
                        else ancestors.n_paths end) as ancestor_n_paths,
                  (case when descendants.n_paths = 0
                        then 1
                        else descendants.n_paths end) as descendant_n_paths
          from '' || quote_ident(v_hierarchy_index) || '' ancestors,
               '' || quote_ident(v_hierarchy_index) || '' descendants
          where ancestors.'' || quote_ident(v_subitemColumn) || '' = '' || 
                quote_literal(v_item_id) || ''
            and descendants.'' || quote_ident(v_itemColumn) || '' = '' || 
                quote_literal(v_subitem_id) || ''''
      loop
          v_path_decrement :=
              remove_entry.ancestor_n_paths * remove_entry.descendant_n_paths;

          -- This does both an update and an insert because the postegres
          -- execute statement in 7.2.1 is not smart enough to recognize
          -- an if statement.  By placing the update first, the path is
          -- set correctly.  If it is placed second then the path will
          -- be incorrect

           -- delete this entry if n_path would become 0 if we were
           -- to decrement n_paths
           EXECUTE ''
           delete from '' || quote_ident(v_hierarchy_index) || ''
           where '' || quote_ident(v_itemColumn) || '' = '' || 
                       quote_literal(remove_entry.item_id) || ''
             and '' || quote_ident(v_subitemColumn) || '' = '' || 
                       quote_literal(remove_entry.subitem_id) || ''
             and n_paths <= '' || quote_literal(v_path_decrement) || '';

             -- if the item was not deleted then we update.  Otherwise,
             -- this is essentially a no-op
             update '' || quote_ident(v_hierarchy_index) || ''
              set n_paths = n_paths - '' || quote_literal(v_path_decrement) || ''
              where '' || quote_ident(v_itemColumn) || '' = '' || 
                          quote_literal(remove_entry.item_id) || '' 
               and '' || quote_ident(v_subitemColumn) || ''  = '' || 
                          quote_literal(remove_entry.subitem_id) || '';
           '';
      end loop;

  return null;
  end;' language 'plpgsql';

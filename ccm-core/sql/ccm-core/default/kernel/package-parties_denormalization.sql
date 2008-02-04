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
-- $Id: package-parties_denormalization.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/16 18:10:38 $

create or replace package parties_denormalization
as
  procedure add_group (
      group_id    in groups.group_id%TYPE
  );
  procedure add_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure remove_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure add_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  );
  procedure remove_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  );
  procedure add_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure remove_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
end parties_denormalization;
/
show errors

create or replace package body parties_denormalization
as
  procedure add_group (
      group_id    in groups.group_id%TYPE
  )
  as begin
      hierarchy_add_item(add_group.group_id, 'group_subgroup_trans_index', 
                         'group_id', 'subgroup_id');
  end add_group;

  procedure add_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      add_subgroup_members(group_id, subgroup_id);

      hierarchy_add_subitem(add_subgroup.group_id, 
                            add_subgroup.subgroup_id,
                           'group_subgroup_trans_index',
                           'group_id', 'subgroup_id');
  end add_subgroup;

  procedure remove_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_decrement integer;
  begin
      hierarchy_remove_subitem(group_id, subgroup_id,
                              'group_subgroup_trans_index',      
                              'group_id', 'subgroup_id');

      remove_subgroup_members(group_id, subgroup_id);

  end remove_subgroup;

  procedure add_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      for new_entry in (
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = add_member.group_id
      ) loop

          if (add_member.group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          update group_member_trans_index
          set n_paths = n_paths + v_path_increment
          where group_id = new_entry.group_id
            and member_id = add_member.member_id;

          if (SQL%NOTFOUND) then

              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, add_member.member_id, v_path_increment);
          end if;
      end loop;

  end add_member;

  procedure remove_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  )
  as
    v_path_decrement integer;
  begin

      for remove_entry in (
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = remove_member.group_id
      ) loop

        if (remove_entry.group_id = remove_member.group_id) then
            v_path_decrement := 1;
        else
            v_path_decrement := remove_entry.n_paths;
        end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_member_trans_index
        where group_id = remove_entry.group_id
          and member_id = remove_member.member_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = remove_member.member_id;

        end if;

      end loop;

  end remove_member;

  procedure add_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      for new_entry in (
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = add_subgroup_members.group_id
            and members.group_id = add_subgroup_members.subgroup_id
      ) loop

          if (add_subgroup_members.group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          update group_member_trans_index
          set n_paths = n_paths + v_path_increment
          where group_id = new_entry.group_id
            and member_id = new_entry.member_id;

          if (SQL%NOTFOUND) then

              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, new_entry.member_id, v_path_increment);
          end if;
      end loop;

  end add_subgroup_members;

  procedure remove_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_decrement integer;
  begin

      for remove_entry in (
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = remove_subgroup_members.group_id
            and members.group_id = remove_subgroup_members.subgroup_id
      ) loop

          if (remove_subgroup_members.group_id = remove_entry.group_id) then
            v_path_decrement := 1;
          else 
            v_path_decrement := remove_entry.n_paths;
          end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_member_trans_index
        where group_id = remove_entry.group_id
          and member_id = remove_entry.member_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = remove_entry.member_id;

        end if;

      end loop;

  end remove_subgroup_members;

end parties_denormalization;
/
show errors

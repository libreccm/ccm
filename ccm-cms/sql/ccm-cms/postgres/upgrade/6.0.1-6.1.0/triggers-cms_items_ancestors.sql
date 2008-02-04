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
-- $Id: triggers-cms_items_ancestors.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $

-- Author: Daniel Berrange (berrange@redhat.com)
-- Since:  2004-01-15
-- See:    https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=109718#c14
--         https://bugzilla.redhat.com/bugzilla/attachment.cgi?id=97029&action=view

create or replace function cms_items_in_fn() returns trigger as '
  declare
    parents_ancestors varchar;
  begin
    if new.parent_id isnull then
      new.ancestors := new.item_id || ''/'';
    else
      select ancestors into parents_ancestors
      from cms_items
      where item_id = new.parent_id;

      new.ancestors := parents_ancestors || new.item_id || ''/'';
    end if;
    return new;
  end;
' language 'plpgsql';


create trigger cms_items_in before insert on cms_items
  for each row execute procedure cms_items_in_fn();


create or replace function cms_items_up_fn() returns trigger as '
  declare
    parents_ancestors varchar;
  begin
    if new.parent_id isnull and old.parent_id isnull then
      return new;
    end if;
    if new.parent_id = old.parent_id then
      return new;
    end if;

    if new.parent_id isnull then
      new.ancestors := new.item_id || ''/'';
    else
      select ancestors into parents_ancestors
      from cms_items
      where item_id = new.parent_id;

      new.ancestors := parents_ancestors  || new.item_id  || ''/'';
    end if;

    -- Although this update does recursively trigger calls to
    -- cms_items_up_fn, the recursion is nipped in the bud, because we
    -- are not updating the parent_id column.  See above.

    -- use dynamic sql to force parser to evaluate stmt each time and 
    -- avoid seq_scan on cms_items
    EXECUTE
    ''update cms_items '' ||
    ''set ancestors = '''''' || new.ancestors || '''''' || substr(ancestors, char_length('''''' || old.ancestors || '''''')+1 ) '' ||
    ''where ancestors like '''''' || old.ancestors || ''%'''''' || '' and item_id <> '' || new.item_id || '''';

    return new;
  end;
' language 'plpgsql';



create trigger cms_items_up before update on cms_items
  for each row execute procedure cms_items_up_fn();

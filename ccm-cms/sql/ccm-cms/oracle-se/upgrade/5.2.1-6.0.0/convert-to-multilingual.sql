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
-- $Id: convert-to-multilingual.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


-- data model changes
alter table cms_items drop column locale_id;
alter table cms_items add (language char(2));
drop table cms_variants;
drop table cms_variant_tags;

-- cms_bundles
-- from ddl/oracle-se/table-cms_bundles-auto.sql
create table cms_bundles (
    bundle_id INTEGER not null
        constraint cms_bundles_bundle_id_p_hm39d
          primary key,
        -- referential constraint for bundle_id deferred due to circular dependencies
    default_language CHAR(2) not null
);
-- from ddl/oracle-se/deferred.sql
alter table cms_bundles add
    constraint cms_bundles_bundle_id_f_bbqro foreign key (bundle_id)
      references cms_items(item_id);

-- upgrade procedure
declare

  type ItemRec is Record(
    item_id cms_items.item_id%type,
    parent_id cms_items.parent_id%type,
    section_id cms_items.section_id%type,
    version cms_items.version%type,
    name cms_items.name%type,
    ancestors cms_items.ancestors%type
  );

  v_draftBundle integer;
  v_liveBundle  integer;


  function create_bundle(item ItemRec) return integer is
    v_bundle_id integer;
    v_lang char(2);
  begin
    select acs_object_id_seq.nextval into v_bundle_id from dual;
    v_lang := 'en';

    -- create ACSObject
    insert into acs_objects
      (object_id, object_type, display_name, default_domain_class)
    values
      (v_bundle_id, 'com.arsdigita.cms.ContentBundle', item.name, 'com.arsdigita.cms.ContentBundle');

    -- create VersionedACSObject with master_id null
    insert into vc_objects
      (object_id, master_id, is_deleted)
    values
      (v_bundle_id, null, '0');

    -- create ContentItem Bundle Object
   insert into cms_items
     (item_id, parent_id, name, version, section_id, ancestors)
   values
     (v_bundle_id, item.parent_id, item.name, item.version, item.section_id,
        substr(item.ancestors,1,length(item.ancestors)-length(item.item_id||'')-1)||v_bundle_id||'/');

   -- create Bundle object
   insert into cms_bundles
     (bundle_id, default_language)
   values
     (v_bundle_id, v_lang);

   return v_bundle_id;
  end;

  -- Set the Bundle as a parent of the Item
  -- Set the Folder as a parent of the Bundle
  -- Set the Bundle as an index item in cms_folders
  -- Update the context of the Items and Bundle
  procedure update_hierarchy(item ItemRec, p_bundle integer) IS
    v_oldParent cms_items.item_id%type;
    v_context cms_items.item_id%type;
    v_isIndex number := 0;
    v_lang char(2);
    v_bundlePos number;
  begin
    v_oldParent := item.parent_id;
    v_lang := 'en';

    update cms_items
       set parent_id = p_bundle,
           language = v_lang
     where item_id = item.item_id;

    update cms_items
       set parent_id = v_oldParent
     where item_id = p_bundle;

    -- update ancestors denormalization
    v_bundlePos := length(item.ancestors) - length(item.item_id||'');

    update cms_items
       set ancestors = substr(ancestors,1,v_bundlePos-1) || p_bundle || '/' || substr(ancestors,v_bundlePos)
     where ancestors like item.ancestors || '%';

    update object_context
       set context_id = v_oldParent
     where object_id = p_bundle;

    update object_context
       set context_id = p_bundle
     where object_id = item.item_id;

    select count(*) into v_isIndex
      from cms_folders
     where folder_id = v_oldParent
           and index_id = item.item_id;

    if (v_isIndex > 0) then
        -- the Item was an index item, replace it with the Bundle
        update cms_folders
           set index_id = p_bundle
         where folder_id = v_oldParent;
    end if;
  end;

-- For each draft top-level item in the system:
-- 1. create the Bundle object
-- 2. get live version, create live Bundle if it exists
-- 3. get the pending versions and assign them to the same live Bundle
begin
  for item in (select ci.item_id, parent_id, section_id, version, name, ancestors
                from cms_items ci, cms_pages cp, cms_folders f
                where ci.item_id = cp.item_id
                    and f.folder_id = ci.parent_id
                    and type_id is not null
                    and version = 'draft')
  loop
    v_draftBundle := create_bundle(item);
    update_hierarchy(item, v_draftBundle);
    -- get all the "pending" and "live" versions and create and
    -- set the same live bundle for all of them
    v_liveBundle := null;
    for itemVersion in (select ci.item_id, parent_id, section_id, version, name, ancestors
                    from cms_items ci, cms_version_map m
                    where m.item_id = item.item_id
                        and m.version_id = ci.item_id)
    loop
        if (v_liveBundle is null) then
            itemVersion.version := 'live';
            v_liveBundle := create_bundle(itemVersion);
            --- insert mapping for live and draft Bundle
            insert into cms_version_map (item_id, version_id, timestamp) values (
                v_draftBundle, v_liveBundle, sysdate);
        end if;
        update_hierarchy(itemVersion, v_liveBundle);
    end loop;
  end loop;
end;
/
show errors

PROMPT Changing ItemResolver used in existing ContentSections
PROMPT ** NOTE: Sections using a custom ItemResolver, i.e. something other than
PROMPT          SimpleItemResolver will NOT be changed, you have to do it manually! **
update content_sections
set item_resolver_class='com.arsdigita.cms.dispatcher.MultilingualItemResolver'
where item_resolver_class='com.arsdigita.cms.dispatcher.SimpleItemResolver';

commit;

-- upgrade statistics
PROMPT Number of top-level items inside bundles:
select count(*) from cms_items ci, cms_pages cp, cms_bundles b
    where ci.item_id = cp.item_id and type_id is not null and version='draft' and b.bundle_id=ci.parent_id;

PROMPT Number of top-level items outside bundles:
select count(*) from cms_items ci, cms_pages cp, cms_folders f
    where ci.item_id = cp.item_id and type_id is not null and version='draft' and f.folder_id=ci.parent_id;

PROMPT Total number of bundles:
select count(*) from cms_bundles;

PROMPT - number of draft bundles:
select count(*)
    from acs_objects o, cms_items i, cms_bundles b, vc_objects vo
    where o.object_id = vo.object_id
        and vo.object_id = i.item_id
        and i.item_id = b.bundle_id
        and i.version='draft';

PROMPT - number of live bundles:
select count(*)
    from acs_objects o, cms_items i, cms_bundles b, vc_objects vo
    where o.object_id = vo.object_id
        and vo.object_id = i.item_id
        and i.item_id = b.bundle_id
        and i.version='live';

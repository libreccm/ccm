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
alter table cms_items drop column locale_id restrict;
alter table cms_items add column language char(2);

drop table cms_variants;
drop table cms_variant_tags;

-- cms_bundles
-- from ddl/postgres/table-cms_bundles-auto.sql
create table cms_bundles (
    bundle_id INTEGER not null
        constraint cms_bundles_bundle_id_p_hm39d
          primary key,
        -- referential constraint for bundle_id deferred due to circular dependencies
    default_language CHAR(2) not null
);

-- from ddl/postgres/deferred.sql
alter table cms_bundles add
    constraint cms_bundles_bundle_id_f_bbqro foreign key (bundle_id)
      references cms_items(item_id);

-- upgrade procedure
-- cannot use record as a function parameter, see PostgreSQL Bug #709: PL/pgSQL Parameter Of Composite Type
create or replace function ml_create_bundle(cms_items.item_id%TYPE, cms_items.parent_id%TYPE, cms_items.name%TYPE,
    cms_items.version%TYPE, cms_items.section_id%TYPE, cms_items.ancestors%TYPE)
returns integer as '
declare
    p_item_id alias for $1;
    p_parent_id alias for $2;
    p_name alias for $3;
    p_version alias for $4;
    p_section_id alias for $5;
    p_ancestors alias for $6;
    v_bundle_id integer;
    v_lang char(2);
begin
   select nextval(''acs_object_id_seq'') into v_bundle_id from dual;
    v_lang := ''en'';

    -- create ACSObject
    insert into acs_objects (object_id, object_type, display_name, default_domain_class) values (
        v_bundle_id, ''com.arsdigita.cms.ContentBundle'', p_name, ''com.arsdigita.cms.ContentBundle'');
    -- create VersionedACSObject with master_id null
    insert into vc_objects (object_id, master_id, is_deleted) values (
        v_bundle_id, null, ''0'');
    -- create ContentItem Bundle Object
   insert into cms_items (item_id, parent_id, name, version, section_id, ancestors) values (
        v_bundle_id, p_parent_id, p_name, p_version, p_section_id,
        substr(p_ancestors,1,length(p_ancestors)-length(p_item_id||'''')-1)||v_bundle_id||''/'');
   -- create Bundle object
   insert into cms_bundles (bundle_id, default_language) values (
        v_bundle_id,v_lang);
   return v_bundle_id;
end;
' language 'plpgsql';

-- Set the Bundle as a parent of the Item
-- Set the Folder as a parent of the Bundle
-- Set the Bundle as an index item in cms_folders
-- Update the context of the Items and Bundle

create or replace function ml_update_hierarchy(cms_items.item_id%TYPE, cms_items.item_id%TYPE, cms_items.parent_id%TYPE, object_context.context_id%TYPE, cms_items.ancestors%TYPE)
returns integer as '
declare
    p_item_id alias for $1;
    p_parent_id alias for $2;
    p_new_parent_id alias for $3;
    p_new_context_id alias for $4;
    p_ancestors alias for $5;

    v_lang varchar(2) := ''en'';
    v_bundlePos integer;
begin

    -- update parent contexts
    update cms_items set parent_id = p_new_parent_id, language = v_lang
        where item_id = p_item_id;
    update cms_items set parent_id = p_parent_id
        where item_id = p_new_parent_id;

    -- update ancestors denormalization
    v_bundlePos := length(p_ancestors) - length(p_item_id||'''');
    update cms_items set ancestors = substr(ancestors,1,v_bundlePos-1) || p_new_parent_id || ''/'' || substr(ancestors,v_bundlePos)
       where  ancestors like p_ancestors || ''%'';

    update object_context set context_id = p_new_context_id
     where object_id = p_item_id;

    -- if the Item was an index item, replace it with the Bundle
    update cms_folders
       set index_id = p_new_parent_id
     where folder_id = p_parent_id
       and index_id = p_item_id;

    return 1;
end;
' language 'plpgsql';


-- For each draft top-level item in the system:
-- 1. Create the Bundle object
-- 2. Get live version, create live Bundle if it exists
-- 3. Get the pending versions and assign them to the same live Bundle
create or replace function ml_upgrade()
returns integer as '
declare
  v_ok          integer;
  v_draftBundle integer;
  v_liveBundle  integer;
  item          record;
  itemVer       record;
begin
  for item in select ci.item_id, ci.parent_id, ci.name, ci.version, ci.section_id, ci.ancestors
                from cms_items ci, cms_pages cp, cms_folders f
               where ci.item_id = cp.item_id
                 and f.folder_id = ci.parent_id
                 and type_id is not null
                 and version = ''draft''
  loop
    v_draftBundle := ml_create_bundle(item.item_id,
                                      item.parent_id,
                                      item.name,
                                      item.version,
                                      item.section_id,
                                      item.ancestors);

    v_ok := ml_update_hierarchy(item.item_id,
                                item.parent_id,
                                v_draftBundle,
                                v_draftBundle,
                                item.ancestors);
    v_liveBundle := null;
    for itemVer in select ci.item_id, ci.parent_id, ci.name, ci.version, ci.section_id, ci.ancestors
                     from cms_items ci, cms_version_map m
                    where m.item_id = item.item_id
                      and m.version_id = ci.item_id
    loop
        if (v_liveBundle is null) then
            itemVer.version := ''live'';
            v_liveBundle := ml_create_bundle(itemVer.item_id,
                                             itemVer.parent_id,
                                             itemVer.name,
                                             itemVer.version,
                                             itemVer.section_id,
                                             itemVer.ancestors);
            --- insert mapping for live and draft Bundle
            insert into cms_version_map
              (item_id, version_id, timestamp)
            values
              (v_draftBundle, v_liveBundle, current_timestamp);

        end if;

        v_ok := ml_update_hierarchy(itemVer.item_id,
                                    itemVer.parent_id,
                                    v_liveBundle,
                                    v_draftBundle,
                                    itemVer.ancestors);
    end loop;
  end loop;
  return 1;
end;
' language 'plpgsql';

select ml_upgrade();

update object_context
   set context_id = (select parent_id
                       from cms_items ci
                      where object_id = ci.item_id)
 where object_id in (select bundle_id
                       from cms_bundles cb,
                            cms_items ci
                      where ci.item_id = cb.bundle_id
                        and ci.version = 'draft');

update object_context
   set context_id = (select context_id
                       from object_context oc,
                            cms_version_map cvm
                      where oc.object_id = cvm.item_id
                        and cvm.version_id = object_context.object_id)
 where context_id is null
   and object_id in (select version_id
                       from cms_version_map);

\qecho Changing ItemResolver used in existing ContentSections
\qecho ** NOTE: Sections using a custom ItemResolver, i.e. something other than
\qecho          SimpleItemResolver will NOT be changed, you have to do it manually! **
update content_sections
set item_resolver_class='com.arsdigita.cms.dispatcher.MultilingualItemResolver'
where item_resolver_class='com.arsdigita.cms.dispatcher.SimpleItemResolver';

-- upgrade statistics
\qecho Number of top-level items inside bundles:
select count(*) from cms_items ci, cms_pages cp, cms_bundles b
    where ci.item_id = cp.item_id and type_id is not null and version='draft' and b.bundle_id=ci.parent_id;

\qecho Number of top-level items outside bundles:
select count(*) from cms_items ci, cms_pages cp, cms_folders f
    where ci.item_id = cp.item_id and type_id is not null and version='draft' and f.folder_id=ci.parent_id;

\qecho Total number of bundles:
select count(*) from cms_bundles;

\qecho - number of draft bundles:
select count(*)
    from acs_objects o, cms_items i, cms_bundles b, vc_objects vo
    where o.object_id = vo.object_id
        and vo.object_id = i.item_id
        and i.item_id = b.bundle_id
        and i.version='draft';

\qecho - number of live bundles:
select count(*)
    from acs_objects o, cms_items i, cms_bundles b, vc_objects vo
    where o.object_id = vo.object_id
        and vo.object_id = i.item_id
        and i.item_id = b.bundle_id
        and i.version='live';

drop function ml_upgrade();
drop function ml_update_hierarchy(cms_items.item_id%TYPE,
                                  cms_items.item_id%TYPE,
                                  cms_items.parent_id%TYPE,
                                  object_context.context_id%TYPE,
                                  cms_items.ancestors%TYPE);
drop function ml_create_bundle(cms_items.item_id%TYPE,
                               cms_items.parent_id%TYPE,
                               cms_items.name%TYPE,
                               cms_items.version%TYPE,
                               cms_items.section_id%TYPE,
                               cms_items.ancestors%TYPE);

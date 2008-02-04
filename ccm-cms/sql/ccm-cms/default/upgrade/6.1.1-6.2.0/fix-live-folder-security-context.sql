
update object_context set
      context_id = (select master_id from cms_items where item_id = object_context.object_id )
  where object_id in (
      select f.folder_id from cms_folders f, cms_items ci
      where f.folder_id = ci.item_id  and ci.version = 'live'
  ) ;

delete from acs_permissions
  where object_id in (
      select f.folder_id from cms_folders f, cms_items ci
      where f.folder_id = ci.item_id  and ci.version = 'live'
  ) ;


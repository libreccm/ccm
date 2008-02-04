
update object_context set
    context_id = (select ci.parent_id from cms_items ci
                  where ci.item_id = object_context.object_id)
    where
      object_id in (select i2.item_id from cms_items i2
                    where i2.parent_id in (select bundle_id from cms_bundles))
      and context_id <> (select i3.parent_id from cms_items i3
                         where i3.item_id = object_context.object_id) ;

insert into object_context (object_id, context_id)
        select ci.item_id, ci.parent_id from cms_items ci
            where ci.parent_id in (select bundle_id from cms_bundles)
              and ci.item_id not in (select object_id from object_context) ;


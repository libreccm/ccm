create table ff_content_item_asset_map (
  item_id     integer 
              constraint ff_content_item_asset_item_fk
              references ff_freeform_content_items(item_id),
  asset_id    integer 
              constraint ff_content_item_asset_asset_fk
              references cms_assets(asset_id),
  rank        integer
);

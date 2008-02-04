create table ff_binary_assets (
  asset_id        integer
                  constraint ff_binary_asset_fk references
                  acs_objects(object_id) on delete cascade
                  constraint ff_binary_asset_pk primary key
);

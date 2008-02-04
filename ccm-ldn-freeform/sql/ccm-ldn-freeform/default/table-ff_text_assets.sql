create table ff_text_assets (
  asset_id        integer
                  constraint ff_text_asset_fk references
                  acs_objects(object_id) on delete cascade
                  constraint ff_text_asset_pk primary key
);

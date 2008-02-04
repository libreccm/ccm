create table ff_freeform_binary_assets (
    asset_id    integer
                constraint ff_binary_assets_fk references
                cms_assets(asset_id) on delete cascade
                constraint ff_binary_assets_pk primary key,
    content     blob
);

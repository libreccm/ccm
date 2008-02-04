create table ff_freeform_text_assets (
    asset_id    integer
                constraint ff_text_assets_fk references
                cms_assets(asset_id) on delete cascade
                constraint ff_text_assets_pk primary key
);

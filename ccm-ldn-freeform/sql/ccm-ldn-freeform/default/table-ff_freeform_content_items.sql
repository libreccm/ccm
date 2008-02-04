create table ff_freeform_content_items (
  item_id        integer
                  constraint ff_content_items_fk references
                  cms_items(item_id) on delete cascade
                  constraint ff_content_items_pk primary key
);

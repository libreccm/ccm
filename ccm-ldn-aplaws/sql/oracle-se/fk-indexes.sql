create index cms_filestorage_fbits_idx on cms_filestorage (filebits);

create index ct_mp_articles_map_art_idx on ct_mp_articles_map (article);

create index ct_mp_articles_map_sec_idx  on ct_mp_articles_map (section);

create index ct_mp_sections_image_idx on ct_mp_sections (image);

create index ct_mp_sections_text_idx on ct_mp_sections (text);

-- this index is need to skip full table scan if 
-- where condition based on name columns
create index cat_categories_name_idx on cat_categories(name);

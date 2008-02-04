alter table nav_quick_links add cascade_link BOOLEAN;

update nav_quick_links set cascade_link = true;

alter table nav_quick_links alter column cascade_link set not null;

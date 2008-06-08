alter table forum_forums add no_category_posts_allowed BOOLEAN NOT NULL;

update forum_forums set no_category_posts_allowed = TRUE;

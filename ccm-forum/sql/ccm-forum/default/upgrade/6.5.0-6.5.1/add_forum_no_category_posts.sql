alter table forum_forums add (no_category_posts_allowed CHAR(1));

update forum_forums set no_category_posts_allowed = 1;

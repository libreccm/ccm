alter table forum_forums add (anonymous_posts_allowed CHAR(1));

update forum_forums set anonymous_posts_allowed = 0;
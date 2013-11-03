alter table forum_forums add anonymous_posts_allowed BOOLEAN NOT NULL DEFAULT FALSE;

update forum_forums set anonymous_posts_allowed = FALSE;

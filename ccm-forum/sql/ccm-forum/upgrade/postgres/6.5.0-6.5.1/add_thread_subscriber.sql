alter table forum_forums add subscribe_thread_starter BOOLEAN NOT NULL DEFAULT FALSE;

update forum_forums set subscribe_thread_starter = FALSE;



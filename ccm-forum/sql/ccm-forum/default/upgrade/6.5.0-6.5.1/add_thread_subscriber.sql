alter table forum_forums add (subscribe_thread_starter CHAR(1));

update forum_forums set subscribe_thread_starter = 0;



insert into acs_privileges (privilege)
values ('forum_read');

insert into acs_privileges (privilege)
values ('forum_create_thread');

insert into acs_privileges (privilege)
values ('forum_respond');

insert into acs_privilege_hierarchy (privilege, child_privilege)
values ('forum_moderation', 'forum_create_thread');

insert into acs_privilege_hierarchy (privilege, child_privilege)
values ('forum_create_thread', 'forum_respond');

insert into acs_privilege_hierarchy (privilege, child_privilege)
values ('forum_respond', 'forum_read');


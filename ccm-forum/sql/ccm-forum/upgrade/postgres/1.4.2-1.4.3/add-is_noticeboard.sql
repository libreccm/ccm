begin;
  alter table forum_forums add is_noticeboard boolean;
  update forum_forums set is_noticeboard = false;
  alter table forum_forums alter is_noticeboard set not null;
commit;

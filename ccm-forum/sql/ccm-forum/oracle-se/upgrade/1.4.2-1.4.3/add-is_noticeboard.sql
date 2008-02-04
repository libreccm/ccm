alter table forum_forums add is_noticeboard char(1);
update forum_forums set is_noticeboard = '0';
alter table forum_forums modify is_noticeboard not null;
alter table forum_forums add constraint foru_for_is_noticeboar_c_1deu6
          check(is_noticeboard in ('0', '1'));

alter table forum_forums add 
    admin_group_id   INTEGER;
alter table forum_forums add
    constraint foru_foru_admi_grou_id_f_k0nw6 foreign key (admin_group_id)
      references groups(group_id);

alter table forum_forums add 
    create_group_id  INTEGER;
alter table forum_forums add
    constraint foru_foru_crea_grou_id_f_f7x57 foreign key (create_group_id)
      references groups(group_id);

alter table forum_forums add 
    respond_group_id INTEGER;
alter table forum_forums add
    constraint foru_foru_respo_gro_id_f_rnofz foreign key (respond_group_id)
      references groups(group_id);

alter table forum_forums add 
    read_group_id    INTEGER;
alter table forum_forums add
    constraint foru_forum_rea_grou_id_f_itati foreign key (read_group_id)
      references groups(group_id);


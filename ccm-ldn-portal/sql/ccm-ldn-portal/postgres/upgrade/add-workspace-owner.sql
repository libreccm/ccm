
alter table workspaces add column owner_id INTEGER;

alter table workspaces add
    constraint workspaces_owner_id_f_mbbra foreign key (owner_id)
      references users(user_id);



alter table workspaces add owner_id INTEGER;

alter table workspaces add
    constraint workspaces_owner_id_f_tpdju foreign key (owner_id)
      references users(user_id);
      

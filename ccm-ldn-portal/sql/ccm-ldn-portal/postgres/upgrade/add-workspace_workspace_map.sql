
create table workspace_workspace_map (
    referenced_workspace_id INTEGER not null,
        -- referential constraint for referenced_workspace_id deferred due to circular dependencies
    referencing_workspace_id INTEGER not null,
        -- referential constraint for referencing_workspace_id deferred due to circular dependencies
    constraint work_wor_map_ref_wor_i_p_bidbq
      primary key(referencing_workspace_id, referenced_workspace_id)
);

alter table workspace_workspace_map add
    constraint work_wor_map_ref_wor_i_f_md9_z foreign key (referenced_workspace_id)
      references workspaces(workspace_id);
alter table workspace_workspace_map add
    constraint work_wor_map_ref_wor_i_f_nyhzz foreign key (referencing_workspace_id)
      references workspaces(workspace_id);

create index wrkspc_wrkspc_mp_rfrn_wrks_idx on workspace_workspace_map(referenced_workspace_id);

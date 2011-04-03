insert into object_context_map (object_id, context_id)
  select portal_id, workspace_id 
    from workspace_portal_map;

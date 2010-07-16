create table ct_projects (
    project_id INTEGER not null
        constraint ct_projects_project_id_p_temz1
          primary key
        -- referential constraint for project_id deferred due to circular dependencies
);

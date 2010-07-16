alter table ct_projects add 
    constraint ct_projects_project_id_f_5k900 foreign key (project_id)
      references cms_organizationalunit(organizationalunit_id);

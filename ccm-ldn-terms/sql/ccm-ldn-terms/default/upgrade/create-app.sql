create table trm_app (
    application_id INTEGER not null
        constraint trm_app_application_id_p_eup00
          primary key
        -- referential constraint for application_id deferred due to circular dependencies
);
alter table trm_app add 
    constraint trm_app_application_id_f_qlphe foreign key (application_id)
      references applications(application_id);

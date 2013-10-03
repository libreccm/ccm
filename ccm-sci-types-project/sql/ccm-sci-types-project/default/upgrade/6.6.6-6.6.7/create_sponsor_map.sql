create table ct_sci_project_sponsor_map (
    sponsor_id INTEGER not null,    
    project_id INTEGER not null,    
    sponsor_order INTEGER,

    constraint ct_sci_pro_spo_map_pro_p_y6bbk
      primary key(project_id, sponsor_id)
);

alter table ct_sci_project_sponsor_map add 
    constraint ct_sci_pro_spo_map_pro_f_8a7hv foreign key (project_id)
      references ct_sci_project_bundles(bundle_id);
alter table ct_sci_project_sponsor_map add 
    constraint ct_sci_pro_spo_map_spo_f_7x6td foreign key (sponsor_id)
      references cms_orgaunit_bundles(bundle_id);

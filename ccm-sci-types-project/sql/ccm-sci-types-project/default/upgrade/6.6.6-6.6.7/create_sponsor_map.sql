CREATE TABLE ct_sci_project_sponsor_map (
    sponsor_id INTEGER not null,    
    project_id INTEGER not null,    
    sponsor_order INTEGER,

     CONSTRAINT ct_sci_pro_spo_map_pro_p_y6bbk
    PRIMARY KEY(project_id, sponsor_id)
);

   ALTER TABLE ct_sci_project_sponsor_map 
ADD CONSTRAINT ct_sci_pro_spo_map_pro_f_8a7hv 
   FOREIGN KEY (project_id)
    REFERENCES ct_sci_project_bundles(bundle_id);
   ALTER TABLE ct_sci_project_sponsor_map 
ADD CONSTRAINT ct_sci_pro_spo_map_spo_f_7x6td 
   FOREIGN KEY (sponsor_id)
    REFERENCES cms_orgaunit_bundles(bundle_id);

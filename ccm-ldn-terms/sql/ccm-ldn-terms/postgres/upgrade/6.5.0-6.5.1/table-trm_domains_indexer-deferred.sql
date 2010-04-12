alter table trm_domains_indexer add 
    constraint trm_dom_ind_las_mod_us_f_1k1i3 foreign key (last_modified_user)
      references parties(party_id);
alter table trm_domains_indexer add 
    constraint trm_domain_indexer_key_f_lghsq foreign key (key)
      references trm_domains(key);

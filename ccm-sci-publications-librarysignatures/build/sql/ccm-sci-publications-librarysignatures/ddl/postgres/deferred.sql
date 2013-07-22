alter table ca_publications_librarysignatures add 
    constraint ca_pub_librarys_pub_id_f_be8sv foreign key (publication_id)
      references ct_publications(publication_id);
alter table ca_publications_librarysignatures add 
    constraint ca_pub_librarys_sig_id_f_gk1cu foreign key (signature_id)
      references acs_objects(object_id);

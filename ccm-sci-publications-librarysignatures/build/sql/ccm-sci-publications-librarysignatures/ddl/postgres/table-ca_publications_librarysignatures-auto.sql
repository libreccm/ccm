create table ca_publications_librarysignatures (
    signature_id INTEGER not null
        constraint ca_pub_librarys_sig_id_p_7qh_1
          primary key,
        -- referential constraint for signature_id deferred due to circular dependencies
    library VARCHAR(512) not null,
    signature VARCHAR(512) not null,
    librarylink VARCHAR(2048),
    publication_id INTEGER not null
        -- referential constraint for publication_id deferred due to circular dependencies
);

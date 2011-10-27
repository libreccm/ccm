create table cms_organizationalunits_publications_map (
    orgaunit_id INTEGER not null,
        -- referential constraint for orgaunit_id deferred due to circular dependencies
    publication_id INTEGER not null,
        -- referential constraint for publication_id deferred due to circular dependencies
    publication_order INTEGER,
    constraint cms_org_pub_map_org_id_p__dore
      primary key(publication_id, orgaunit_id)
);

alter table cms_organizationalunits_publications_map add 
    constraint cms_org_pub_map_org_id_f_pe406 foreign key (orgaunit_id)
      references cms_organizationalunits(organizationalunit_id);
alter table cms_organizationalunits_publications_map add 
    constraint cms_org_pub_map_pub_id_f_6udi3 foreign key (publication_id)
      references ct_publications(publication_id);

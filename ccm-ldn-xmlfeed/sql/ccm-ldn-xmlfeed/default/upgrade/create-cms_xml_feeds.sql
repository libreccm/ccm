create table cms_xml_feeds (
    item_id INTEGER not null
        constraint cms_xml_feeds_item_id_p_rr1ph
          primary key,
        -- referential constraint for item_id deferred due to circular dependencies
    url VARCHAR(4000),
    xsl_file_id INTEGER
        -- referential constraint for xsl_file_id deferred due to circular dependencies
);
alter table cms_xml_feeds add 
    constraint cms_xml_fee_xsl_fil_id_f_gdjgh foreign key (xsl_file_id)
      references cms_files(file_id);
alter table cms_xml_feeds add 
    constraint cms_xml_feeds_item_id_f_s5m_n foreign key (item_id)
      references cms_form_item(item_id) on delete cascade;

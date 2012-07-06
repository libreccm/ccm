package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationWithPublisherPublisherAssocUpgrade extends AbstractAssocUpgrade {
   
    @Override
    protected String getTableName() {
        return "ct_publication_with_publisher_publisher_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "publication_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "publisher_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("publisher_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_wit_pub_pub_map_p_5zccy";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_pub_wit_pub_pub_map_f_rcgl4";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_pub_wit_pub_pub_map_f_4nuuz";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_publication_with_publisher_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_publisher_bundles";
    }

}

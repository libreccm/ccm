package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationOrgaUnitAssocUpgrade extends AbstractAssocUpgrade {
   
    @Override
    protected String getTableName() {
        return "cms_organizationalunits_publications_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "publication_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "orgaunit_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("authorship_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_org_pub_map_org_id_p__dore";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "cms_org_pub_map_pub_id_f_6udi3";
    }

    @Override
    protected String getMemberConstraintName() {
        return "cms_org_pub_map_org_id_f_pe406";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_publication_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }

}

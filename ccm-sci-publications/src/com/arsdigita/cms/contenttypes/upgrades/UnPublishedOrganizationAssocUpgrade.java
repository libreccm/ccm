package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class UnPublishedOrganizationAssocUpgrade extends AbstractAssocUpgrade {
       
    @Override
    protected String getTableName() {
        return "ct_unpublished_organization_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "unpublished_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "organization_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("orga_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_unp_org_map_org_id__p__r4u5";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_unp_orga_map_unp_id_f_uflh7";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_unp_org_map_orga_id_f_01qho";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_unpublished_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }
    
    
    
}

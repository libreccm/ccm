package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ExpertiseOrganizationAssocUpgrade extends AbstractAssocUpgrade {
   
    @Override
    protected String getTableName() {
        return "ct_expertise_organization_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "experise_id";
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
        return "ct_exp_org_map_exp_id__p_4q5um";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_exp_orga_map_exp_id_f_9uksn";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_exp_org_map_orga_id_f_d3msf";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_expertise_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }
    
    
    
}

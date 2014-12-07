package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrgaUnitGenericContactAssocUpgrade extends AbstractAssocUpgrade {
       
    @Override
    protected String getTableName() {
        return "cms_orgaunits_contact_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "orgaunit_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "contact_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("contact_type", "character varying(100)");
        attributes.put("map_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_org_con_map_con_id_p_1rc4y";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "cms_org_con_map_org_id_f_vdrnx";
    }

    @Override
    protected String getMemberConstraintName() {
        return "cms_org_con_map_con_id_f_9tm3c";
    }

    @Override
    protected String getOwnerTableName() {
        return "cms_orgaunit_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_contact_bundles";
    }
    
    
    
}

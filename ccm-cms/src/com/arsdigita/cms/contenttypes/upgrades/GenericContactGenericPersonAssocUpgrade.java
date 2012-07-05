package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericContactGenericPersonAssocUpgrade extends AbstractAssocUpgrade {
   
    @Override
    protected String getTableName() {
        return "cms_person_contact_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "person_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "contact_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("link_order", "integer");
        attributes.put("link_key", "character varying(100)");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_per_con_map_con_id_p_g1cii";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "cms_per_con_map_per_id_f_g82jn";
    }

    @Override
    protected String getMemberConstraintName() {
        return "cms_per_con_map_con_id_f_peoc2";
    }

    @Override
    protected String getOwnerTableName() {
        return "cms_person_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_contact_bundles";
    }
    
    
    
}

package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileOwnerAssocUpgrade extends AbstractAssocUpgrade {
    
    public PublicPersonalProfileOwnerAssocUpgrade() {
        super("PublicPersonalProfileOwnerAssocUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new PublicPersonalProfileOwnerAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "ct_public_personal_profile_owner_map.owner_id";
    }

    @Override
    protected String getOwnerIdCol() {
        return "profile_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "owner_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();      
        attributes.put("owner_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_per_pro_own_map_p_rr7ie";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_pub_per_pro_own_map_f_ugs15";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_pub_per_pro_own_map_f_cd7_1";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_public_personal_profile_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_person_bundles";
    }
    
    
    
}

package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ExpertiseOrdererAssocUpgrade extends AbstractAssocUpgrade {
  
    @Override
    protected String getTableName() {
        return "ct_expertise_orderer";
    }

    @Override
    protected String getOwnerIdCol() {
        return "experise_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "orderer_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("orderer_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_exp_ord_map_exp_id__p_dn4qh";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_expe_ord_map_exp_id_f_e_q5f";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_expe_ord_map_ord_id_f_q51ie";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_expertise_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_orgaunit_bundles";
    }

}

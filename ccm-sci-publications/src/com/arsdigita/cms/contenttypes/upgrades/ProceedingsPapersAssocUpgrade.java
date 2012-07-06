package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ProceedingsPapersAssocUpgrade extends AbstractAssocUpgrade {
 
    @Override
    protected String getTableName() {
        return "ct_proceedings_papers_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "proceedings_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "paper_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("paper_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pro_pap_map_pap_id__p_o1ws7";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_pro_pap_map_proc_id_f_4jgfl";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_proc_pap_map_pap_id_f_k6cly";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_proceedings_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_inproceedings_bundles";
    }

}

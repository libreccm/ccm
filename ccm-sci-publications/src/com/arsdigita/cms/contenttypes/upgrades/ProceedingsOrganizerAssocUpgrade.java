package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ProceedingsOrganizerAssocUpgrade extends AbstractAssocUpgrade {
   
    @Override
    protected String getTableName() {
        return "ct_proceedings_organizer_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "proceeding_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "organizer_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("organizer_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pro_org_map_org_id__p_uhoc1";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_proc_org_map_pro_id_f_jkgwf";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_proc_org_map_org_id_f_60x9n";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_proceedings_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }

}

package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrgaUnitGenericOrgaUnitAssocUpgrade extends AbstractAssocUpgrade {

    public GenericOrgaUnitGenericOrgaUnitAssocUpgrade() {
        super("GenericOrgaUnitGenericOrgaUnitAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String args[]) {
        new GenericOrgaUnitGenericOrgaUnitAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "cms_organizationalunits_hierarchy_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "superior_orgaunit_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "subordinate_orgaunit_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("assoc_type", "character varying(128)");
        attributes.put("superior_orgaunit_order", "integer");
        attributes.put("subordinate_orgaunit_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_org_hie_map_sub_or_p_nykpq";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "cms_org_hie_map_sup_or_f_qchkn";
    }

    @Override
    protected String getMemberConstraintName() {
        return "cms_org_hie_map_sub_or_f_xq5is";
    }

    @Override
    protected String getOwnerTableName() {
        return "cms_orgaunit_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }

}

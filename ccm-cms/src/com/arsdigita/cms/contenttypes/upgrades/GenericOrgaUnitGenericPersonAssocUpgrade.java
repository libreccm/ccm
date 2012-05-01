package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrgaUnitGenericPersonAssocUpgrade extends AbstractAssocUpgrade {

    public GenericOrgaUnitGenericPersonAssocUpgrade() {
        super("GenericOrgaUnitGenericPersonAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String args[]) {
        new GenericContactGenericPersonAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "cms_organizationalunits_person_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "organizationalunit_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "person_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("role_name", "character varying(100)");
        attributes.put("status", "character varying(100)");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_org_per_map_org_id_p_km6_m";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "cms_org_per_map_org_id_f_ducb2";
    }

    @Override
    protected String getMemberConstraintName() {
        return "cms_org_per_map_per_id_f_hrpzh";
    }

    @Override
    protected String getOwnerTableName() {
        return "cms_orgaunit_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_person_bundles";
    }

}

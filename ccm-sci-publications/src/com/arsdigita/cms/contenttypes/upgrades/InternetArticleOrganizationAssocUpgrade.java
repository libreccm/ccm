package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class InternetArticleOrganizationAssocUpgrade extends AbstractAssocUpgrade {
    
    public InternetArticleOrganizationAssocUpgrade() {
        super("InternetArticleOrganizationAssocUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new InternetArticleOrganizationAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "ct_internet_article_organization_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "internet_article_id";
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
        return "ct_int_art_org_map_int_p_vkmee";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_int_art_org_map_int_f_5xso5";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_int_art_org_map_org_f_yp23x";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_internet_article_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_orgaunit_bundles";
    }
}

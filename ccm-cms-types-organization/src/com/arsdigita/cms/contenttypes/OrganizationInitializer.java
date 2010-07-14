/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class OrganizationInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(OrganizationInitializer.class);

    public OrganizationInitializer() {
        super("ccm-cms-types-organization.pdl.mf",
                Organization.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/Organization.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Organization.xml";
    }
}

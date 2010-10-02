package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(
            SciOrganizationInitializer.class);

    public SciOrganizationInitializer() {
        super("ccm-sci-types-organization.pdl.mf",
              SciOrganization.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciOrganization.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciOrganization.xml";

    }
}

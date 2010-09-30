package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(
            SciProjectInitializer.class);

    public SciProjectInitializer() {
        super("empty.pdl.mf", SciProject.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciProject.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciProject.xml";
    }
}

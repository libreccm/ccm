package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(
            SciDepartmentInitializer.class);

    public SciDepartmentInitializer() {
        super("empty.pdl.mf", SciDepartment.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/SciDepartment.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciDepartment.xml";
    }
}

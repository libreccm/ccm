package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class DepartmentInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(DepartmentInitializer.class);

    public DepartmentInitializer() {
        super("ccm-cms-types-department.pdl.mf", Department.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[] {
          "/static/content-types/com/arsdigita/cms/contenttypes/Department.xsl"
        };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Department.xml";
    }

}


package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein;
 */
public class BaseContactInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(BaseContactInitializer.class);
    
    public BaseContactInitializer() {
        super("ccm-cms-types-baseContact.pdl.mf",
                BaseContact.BASE_DATA_OBJECT_TYPE);
    }
    
    public String[] getStylesheets() {
        return new String[] {
            "/static/content-types/com/arsdigita/cms/contenttypes/BaseContact.xsl"
        };
    }
    
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/BaseContact.xml";
    }
    
}

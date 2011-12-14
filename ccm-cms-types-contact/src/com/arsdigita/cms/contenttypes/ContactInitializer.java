
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein;
 */
public class ContactInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(ContactInitializer.class);
    
    public ContactInitializer() {
        super("ccm-cms-types-contact.pdl.mf",
                Contact.BASE_DATA_OBJECT_TYPE);
    }
    
    @Override
    public String[] getStylesheets() {
        return new String[] {
            "/static/content-types/com/arsdigita/cms/contenttypes/Contact.xsl"
        };
    }
    
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Contact.xml";
    }
    
}

package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SimpleOrganizationLoader extends AbstractContentTypeLoader {
    
    public SimpleOrganizationLoader() {
        super();
    }
    
      private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SimpleOrganization.xml"
    };
    
    @Override
    public String[] getTypes() {
        return TYPES;
    }
    
}

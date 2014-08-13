package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class OrganizationLoader extends AbstractContentTypeLoader {
    
    public OrganizationLoader() {
        super();
    }
    
      private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Organization.xml"
    };
    
    @Override
    public String[] getTypes() {
        return TYPES;
    }
    
}

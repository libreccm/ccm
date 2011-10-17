package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectLoader extends AbstractContentTypeLoader {
    
    public SciProjectLoader() {
        super();
    }
    
     private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciProject.xml"
    };
    
    @Override
    public String[] getTypes() {
        return TYPES;
    }
    
}

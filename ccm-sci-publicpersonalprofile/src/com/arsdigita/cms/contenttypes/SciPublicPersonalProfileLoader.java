package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfileLoader extends AbstractContentTypeLoader {
            
    public SciPublicPersonalProfileLoader() {
        super();                
    }
    
     private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/"
        + "SciPublicPersonalProfile.xml"
    };
          
    @Override
    public String[] getTypes() {
        return TYPES;
    }        
}

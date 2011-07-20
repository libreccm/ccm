package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileLoader extends AbstractContentTypeLoader {
            
    public PublicPersonalProfileLoader() {
        super();                
    }
    
     private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/"
        + "PublicPersonalProfile.xml"
    };
          
    @Override
    public String[] getTypes() {
        return TYPES;
    }        
}

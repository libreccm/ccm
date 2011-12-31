package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.xml.Element;

/**
 * Implementations of this interface are used to render automatic content
 * (for example a publication list).
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface ContentGenerator {
    
    /**
     * Generates the content
     * 
     * @param parent XML element to attach the content to
     * @param person The person to be used as data source
     * @param state The current page state.
     * @param profileLanguage  
     */
    void generateContent(Element parent, 
                         GenericPerson person, 
                         PageState state, 
                         String profileLanguage);
    
}

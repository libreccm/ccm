package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface ContentGenerator {
    
    void generateContent(Element parent, GenericPerson person, PageState state);
    
}

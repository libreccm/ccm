package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileXmlGenerator extends SimpleXMLGenerator {
    
    private ContentItem item;
    
    public PublicPersonalProfileXmlGenerator(final ContentItem item) {
        this.item = item;
    }
    
    @Override
    public ContentItem getContentItem(final PageState state) {
        return item;
    }
    
}

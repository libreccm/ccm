package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.CMS;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

/**
 * Trivial extension of Bebop page.
 * Can be used, for example, to deliver a part of the page to AJAX clients.  
 *  
 * @author Alan Pevec
 */
public class EmptyPage extends Page {

    public EmptyPage() {
        super("", new SimpleContainer());
    }

    @Override
    public void generateXML(PageState state, Document parent) {
        Element page = parent.createRootElement("cms:emptyPage", CMS.CMS_XML_NS);
        page.addAttribute("title", getTitle().getGlobalizedMessage().getKey());
        m_panel.generateXML(state, page);
    }
    
}

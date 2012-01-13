/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.rssfeed.ui;

import com.arsdigita.rssfeed.RSSChannel;
import com.arsdigita.rssfeed.RSSRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.xml.Element;


public abstract class RSSComponent extends SimpleComponent {

    private String m_element;
    private String m_xmlns;

    public RSSComponent(String element,
                        String xmlns) {
        m_element = element;
        m_xmlns = xmlns;
    }
    
    protected abstract RSSChannel getChannel(PageState state);
    
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement(m_element,
                                                 m_xmlns);

        RSSChannel channel = getChannel(state);
        content.addContent(RSSRenderer.generateXML(channel));
    }
}

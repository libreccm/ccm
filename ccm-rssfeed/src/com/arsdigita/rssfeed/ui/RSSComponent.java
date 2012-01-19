/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
    
    @Override
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement(m_element,
                                                 m_xmlns);

        RSSChannel channel = getChannel(state);
        content.addContent(RSSRenderer.generateXML(channel));
    }
}

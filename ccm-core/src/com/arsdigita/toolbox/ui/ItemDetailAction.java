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
 *
 */
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Link;
import com.arsdigita.xml.Element;

/**
 * 
 *
 * @author Justin Ross
 * @version $Id: ItemDetailAction.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemDetailAction {
    String m_name;
    String m_url;

    public ItemDetailAction(String name, String url) {
        m_name = name;
        m_url = url;
    }

    public void generateXML(PageState pageState, Element parent) {
        Element action = new Element("bebop:action", Component.BEBOP_XML_NS);
        parent.addContent(action);

        Link link = new Link(m_name, m_url);
        link.generateXML(pageState, action);
    }
}

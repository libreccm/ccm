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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;

/**
 * 
 *
 * @author Justin Ross
 * @version $Id: ItemDetailProperty.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemDetailProperty {
    // These are protected so that subclasses can access them in
    // the generate*XML methods
    protected String m_name;
    protected String m_value;

    public ItemDetailProperty() {
        this(null, null);
    }

    public ItemDetailProperty(String name, String value) {
        m_name = name;
        m_value = value;
    }

    public void generateXML(PageState pageState, Element parent) {
        Element property = new Element("bebop:property", Component.BEBOP_XML_NS);
        parent.addContent(property);

        Element name = new Element("bebop:name", Component.BEBOP_XML_NS);
        property.addContent(name);
        generateNameXML(pageState, name);

        Element value = new Element("bebop:value", Component.BEBOP_XML_NS);
        property.addContent(value);
        generateValueXML(pageState, value);
    }


    /**
     *  This generates the XML for the Name property.  By default,
     *  it takes the passed in name and uses it as the name of the property.
     */
    public void generateNameXML(PageState pageState, Element parent) {
        parent.setText(m_name);
    }

    /**
     *  This generate the XML for the Value property.  By default
     *  it uses the passed in value.
     */
    public void generateValueXML(PageState pageState, Element parent) {
        Label l = new Label(m_value);
        l.setOutputEscaping(false);
        l.generateXML(pageState, parent);
    }
}

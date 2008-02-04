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
package com.arsdigita.bebop.demo;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * This is a common page for a fictitious MySite.
 * It includes a common header, a footer, and a main "content"
 * area.  We override the .generateXML method to add stuff to the
 * main content area.
 */
public class MySitePage extends Page {
    public static final String versionId =
        "$Id: MySitePage.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(MySitePage.class);

    private Component m_top;
    private Component m_bottom;
    private Component m_side;

    public MySitePage() {
        this("");
    }

    public MySitePage(String s) {
        super("MySite: " + s);

        m_top = new MySiteHeader();
        m_bottom = new MySiteFooter();
        m_side = new MySiteSide();
    }

    public void generateXML(PageState ps, Document doc) {
        Element page = generateXMLHelper(ps, doc);
        Element layout = new Element("bebop:fourPanelLayout", BEBOP_XML_NS);
        page.addContent(layout);

        addContents(layout, ps);
    }

    protected void addContents(Element layout, PageState ps) {
        Element topPanel = new Element("bebop:top", BEBOP_XML_NS);
        layout.addContent(topPanel);
        m_top.generateXML(ps, topPanel);

        Element sidePanel = new Element("bebop:side", BEBOP_XML_NS);
        layout.addContent(sidePanel);
        m_side.generateXML(ps, sidePanel);

        Element bottomPanel = new Element("bebop:bottom", BEBOP_XML_NS);
        layout.addContent(bottomPanel);
        m_bottom.generateXML(ps, bottomPanel);

        Element mainPanel = new Element("bebop:main", BEBOP_XML_NS);
        layout.addContent(mainPanel);
        m_panel.generateXML(ps, mainPanel);
    }

    /**
     * Header component.  Demonstrates dynamic content.
     */
    private class MySiteHeader extends SimpleComponent {
        public void generateXML(PageState ps, Element parent) {
            Element elt = new Element("bebop:message", BEBOP_XML_NS);

            elt.setText("MySite: dynamic page header.  You requested: " +
                        Web.getContext().getRequestURL());

            parent.addContent(elt);
        }
    }

    /**
     * Footer component.  All static.
     */
    private class MySiteFooter extends SimpleComponent {
        public void generateXML(PageState ps, Element parent) {
            Element elt = new Element("bebop:message", BEBOP_XML_NS);
            elt.setText("MySite: static footer.");
            parent.addContent(elt);
        }
    }

    /**
     * Footer component.  All static.
     */
    private class MySiteSide extends SimpleComponent {
        public void generateXML(PageState ps, Element parent) {
            Element elt = new Element("bebop:message", BEBOP_XML_NS);
            elt.setText("MySite: static side panel.");
            parent.addContent(elt);
        }
    }
}

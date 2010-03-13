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
package com.arsdigita.bebop;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.Document;
import com.arsdigita.util.UncheckedWrapperException;
import javax.xml.parsers.ParserConfigurationException;
import javax.servlet.ServletException;

/**
 *  Demarcates the
 * position of a slave page subtree inside another page's component
 * tree.  Its generateXML method does nothing but call generateXML on
 * the input page.
 *
 * @version $Id: SlaveComponent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SlaveComponent extends SimpleComponent {

    private final static String SLAVE_ATTRIBUTE =
        "com.arsdigita.bebop.SlavePage";

    private Page m_slavePage;

    /**
     * Constructor.
     */
    public SlaveComponent() {
        super();
    }

    public SlaveComponent(Page p) {
        this();
        m_slavePage = p;
    }

    public void generateXML(PageState ps, Element parent) {
        if (m_slavePage == null) {
            m_slavePage =
                (Page)ps.getRequest().getAttribute(SLAVE_ATTRIBUTE);
        }

        // document parameter will be ignored
        // except we'll waste some effort calling setRootNode and
        // then re-importing the created notes into a new document
        try {
            Document doc = new Document();
            // prepare is deprecated
            // PageState slaveState =
            //     m_slavePage.prepare(ps.getRequest(), ps.getResponse());
            PageState slaveState =
                m_slavePage.process(ps.getRequest(), ps.getResponse());
            m_slavePage.generateXML(slaveState, doc);
            parent.addContent(doc.getRootElement());
        } catch (ParserConfigurationException pce) {
            // TODO: log error
            parent.addContent(new Element("b").setText(pce.toString()));
        } catch (ServletException se) {
            throw new UncheckedWrapperException(se);
        }
    }
}

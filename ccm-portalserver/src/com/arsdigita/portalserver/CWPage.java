/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.util.Assert;

import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

/**
 * CWPage class
 *
 * This is a common page to be used by all CW pages for consistent
 * styling.
 *
 * @author <a href="mailto:elorenzo@arsdigita.com">Eric Lorenzo</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 */

/* XXX Have to control links with permissions and
 *      add access control
 */
public class CWPage extends Page {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/CWPage.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log = Logger.getLogger(CWPage.class);

    private final Container m_global;
    private final Container m_header;
    private final Container m_body;
    private final Container m_footer;

    private IntegerParameter m_selected = new IntegerParameter("m");

    public static final String PORTAL_GLOBAL_ELEMENT = "portalserver:global";
    public static final String PORTAL_HEADER_ELEMENT = "portalserver:header";
    public static final String PORTAL_BODY_ELEMENT = "portalserver:body";
    public static final String PORTAL_FOOTER_ELEMENT = "portalserver:footer";
    public static final String PORTAL_XML_NS =
        "http://www.redhat.com/portalserver/1.0";

    protected CWPage() {
        super(new Label(), new SimpleContainer());

        setClassAttr("portalserver");

        m_panel = new Panel();

        addGlobalStateParam(m_selected);

        m_global = new SimpleContainer
            (PORTAL_GLOBAL_ELEMENT, PORTAL_XML_NS);
        m_header = new SimpleContainer
            (PORTAL_HEADER_ELEMENT, PORTAL_XML_NS);
        m_body = new SimpleContainer
            (PORTAL_BODY_ELEMENT, PORTAL_XML_NS);
        m_footer = new SimpleContainer
            (PORTAL_FOOTER_ELEMENT, PORTAL_XML_NS);

        super.add(m_global);
        super.add(m_header);
        super.add(m_body);
        super.add(m_footer);
    }

    /**
     * Makes the given component the only visible component between
     * the header and footer of this page.
     */
    public void goModal(PageState ps, Component c) {
        Component old = getSelected(ps);
        if (old != null) {
            old.setVisible(ps, false);
        }
        c.setVisible(ps, true);
        setSelected(ps, c);
    }

    private Component getSelected(PageState ps) {
        Integer stateIndex = (Integer) ps.getValue(m_selected);
        Component c = null;
        if (stateIndex != null) {
            c = getComponent(stateIndex.intValue());
        }

        return c;
    }

    private void setSelected(PageState ps, Component c) {
        if (c == null) {
            ps.setValue(m_selected, null);
        } else {
            ps.setValue(m_selected, new Integer(stateIndex(c)));
        }
    }

    /**
     * Clears the currently selected modal component if it has been set.
     */
    public void goUnmodal(PageState ps) {
        Component old = getSelected(ps);
        if (old != null) {
            old.setVisible(ps, false);
        }
        setSelected(ps, null);
    }

    private class Panel extends SimpleContainer {
        public void generateXML(PageState ps, Element p) {
            Component selected = getSelected(ps);
            if (selected == null) {
                super.generateXML(ps, p);
            } else {
                SimpleContainer fakeBody =
                    new SimpleContainer(PORTAL_BODY_ELEMENT,
                                        PORTAL_XML_NS);
                fakeBody.add(selected);

                Element parent = generateParent(p);

                m_header.generateXML(ps, parent);
                fakeBody.generateXML(ps, parent);
                m_footer.generateXML(ps, parent);
            }
        }
    }

    /**
     * Adds a component to the body.
     *
     * @param pc the component to be added
     */
    public void add(Component pc) {
        // Assert.assertNotLocked(this);
        Assert.isUnlocked(this);
        m_body.add(pc);
    }

    public Container getGlobal() {
        return m_global;
    }

    public Container getHeader() {
        return m_header;
    }

    public Container getBody() {
        return m_body;
    }

    public Container getFooter() {
        return m_footer;
    }

}

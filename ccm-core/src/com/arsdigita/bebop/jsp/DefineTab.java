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
package com.arsdigita.bebop.jsp;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.TabbedPane;
import javax.servlet.jsp.JspException;

/**
 * Class for defining a single component tab within a define:tabbedPane
 * JSP tag.  This is an odd tag becuase it doesn't really define a
 * real Bebop container; rather it's a "virtual" container that exists only
 * inside the tag handler class as a way of associating a label with
 * the tag.  The define:tab tag is a special case because we have to
 * call <code>addTab(String label, Component)</code> to add tabs to the
 * tabbed pane, not just<code>add(Component)</code>.
 */
public class DefineTab extends DefineContainer {

    public static final String versionId = "$Id: DefineTab.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Component m_child;
    private String m_label;

    private final static String ERROR_MESSAGE =
        "define:tab must be contained within define:tabbedPane.";

    /**
     * doStartTag() is a special case that does nothing.
     */
    public int doStartTag() throws JspException {
        // don't add anything to parent just yet
        return EVAL_BODY_TAG;
    }

    /**
     * At the close of the tag, add the component <em>within</em> this
     * tag to the tabbed pane with the specified label.
     */
    public int doEndTag() throws JspException {
        // now add to parent
        try {
            TabbedPane parent = (TabbedPane)getParentTag().getComponent();
            parent.addTab(m_label, m_child);
        } catch (ClassCastException cce) {
            throw new JspException(ERROR_MESSAGE);
        }
        return super.doEndTag();
    }

    /**
     * overrides the default container method.  This tag is a placeholder,
     * so we just keep track of what the "real" component contained within
     * this tab is so we can add it to the tabbed pane later on.
     */
    public final void addComponent(Component c) {
        m_child = c;
    }

    protected final Component getComponent() {
        return m_child;
    }

    /**
     * sets the label for the tab contained within this tag.
     */
    public final void setLabel(String l) {
        m_label = l;
    }
}

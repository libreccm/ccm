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

import javax.servlet.jsp.JspException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * 
 * $Id: ShowContainer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowContainer extends ShowComponent {

    private Node m_inputContext;
    private Node m_outputContext;
    private boolean m_empty = true;

    Node getInputContext() {
        if (m_inputContext == null) {
            return getContainerTag().getInputContext();
        } else {
            return m_inputContext;
        }
    }

    Node getOutputContext() {
        // unless otherwise specified, our output context is unchanged
        // from our parent tag's.  So walk up the tree of show:...
        // until we find some base case like page or form.
        if (m_outputContext == null) {
            ShowContainer parentTag = getContainerTag();
            return parentTag.getOutputContext();
        } else {
            return m_outputContext;
        }
    }

    void setOutputContext(Node n) {
        m_outputContext = n;
    }

    void setInputContext(Node n) {
        if (n != null) {
            m_inputContext = n;
        }
    }

    /**
     * When we open a container tag, by default we introduce a new
     * scoping level in our input context.  Any tree walkers we build
     * within this container will start their traversal rooted at the
     * element that corresponds with this container itself.  (Note that
     * page is a special case.)
     */
    public int doStartTag() throws JspException {
        m_inputContext = findFirstMatch(getName());
        setOutputContext(getContainerTag().getOutputContext());
        return super.doStartTag();
    }

    public int doEndTag() throws JspException {
        if (isEmpty()) {
            // do deep copy and add to parent output context
            return super.doEndTag();
        } else {
            // nested tags have already added stuff we want,
            // so do nothing.
            return EVAL_PAGE;
        }
    }

    /**
     * Literal text is turned into &lt;bebop:label> nodes with
     * output escaping disabled.
     */
    void handleText() throws JspException {
        if (bodyContent == null) {
            return;
        }

        String text = bodyContent.getString();
        if (text != null && text.length() > 0) {
            m_empty = false;
            Element label =
                getResultDocument().createElementNS(BEBOP_XMLNS,
                                                    "bebop:label");
            label.setAttribute("escape", "yes");
            Text textNode = getResultDocument().createTextNode(text);
            label.appendChild(textNode);
            getOutputContext().appendChild(label);
            try {
                bodyContent.clear();
            } catch (java.io.IOException ioe) {
                throw new JspException(ioe.toString());
            }
        }
    }

    public int doAfterBody() throws JspException {
        handleText();
        return SKIP_BODY;
    }

    boolean isEmpty() {
        return m_empty;
    }
}

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
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * tag for displaying a Bebop form and widgets within it.
 * A Bebop form is a special case of a container because we need to
 * preserve the pageState children which will later turn into
 * &lt;input type=hidden>.
 * 
 * @version $Id: ShowForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowForm extends ShowContainer {

    private Node m_oldOutputContext;
    private Node m_formResult;

    public int doStartTag() throws JspException {
        super.doStartTag();
        // shallow-copy bebop:form into new document
        m_formResult =
            getResultDocument().importNode(getInputContext(), false);
        // now copy all bebop:pageState children into formResult
        TreeWalker walker = createTreeWalker(new NodeFilter() {
                public short acceptNode(Node n) {
                    String nodeName = n.getNodeName();
                    if (nodeName.equals("bebop:pageState")) {
                        return FILTER_ACCEPT;
                    }
                    if (nodeName.equals("bebop:formWidget")) {
                        if (((Element)n).getAttribute("type").equals("hidden")) {
                            return FILTER_ACCEPT;
                        }
                    }
                    return FILTER_SKIP;
                }
            });
        Node n;
        while ((n = walker.nextNode()) != null) {
            Node resultNode = getResultDocument().importNode(n, true);
            m_formResult.appendChild(resultNode);
        }
        m_oldOutputContext = getOutputContext();
        setOutputContext(m_formResult);
        return EVAL_BODY_TAG;
    }

    public int doEndTag() throws JspException {
        setOutputContext(m_oldOutputContext);
        if (!isEmpty()) {
            getOutputContext().appendChild(m_formResult);
            return EVAL_PAGE;
        } else {
            return super.doEndTag();
        }
    }
}

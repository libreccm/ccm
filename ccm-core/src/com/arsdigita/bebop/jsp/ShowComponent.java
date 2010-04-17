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
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * Base class for JSP tag library to manipulate and display XML
 * generated from Bebop. This allows JSP authors to show Bebop pages
 * or components from within them within relatively "normal" JSP pages.
 * <p>
 * show:component may also be used directly in a JSP page:
 * <pre>
 * &lt;show:component name="bebopComponent"/>
 * </pre>
 * This will have the effect of finding an element in the XML
 * document whose name attribute is "bebopComponent" and copying
 * the subtree rooted at this element into the result document.
 * 
 * @version $Id: ShowComponent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ShowComponent extends BodyTagSupport implements JSPConstants {

    private String m_name;

    /**
     * Gets the result Document created by the show:page tag that the
     * current tag is contained within.
     */
    Document getResultDocument() {
        ShowPage pageTag = getPageTag();
        return pageTag.getResultDocument();
    }

    /**
     * Creates a TreeWalker from the input page object, given a NodeFilter
     * and a node to start the traversal from.
     *
     * @param filter the node filter to use for accepting/skipping/rejecting
     * nodes in the traversal.
     * @return a TreeWalker.
     */
    TreeWalker createTreeWalker(Node startFrom, NodeFilter filter) {
        ShowPage pageTag = getPageTag();
        DocumentTraversal dt = (DocumentTraversal)pageTag.getInputDocument();
        TreeWalker tw =
            dt.createTreeWalker(startFrom, NodeFilter.SHOW_ALL,
                                filter, false);
        return tw;
    }

    /**
     * Creates a TreeWalker from the input page object, given a NodeFilter.
     * The traversal starts from the input context set up by whatever
     * tag we're contained within.
     *
     * @param filter the node filter to use for accepting/skipping/rejecting
     * nodes in the traversal.
     * @return a TreeWalker.
     */
    TreeWalker createTreeWalker(NodeFilter filter) {
        ShowContainer parent = getContainerTag();
        return createTreeWalker(parent.getInputContext(), filter);
    }

    ShowPage getPageTag() {
        return (ShowPage)findAncestorWithClass(this, ShowPage.class);
    }

    ShowContainer getContainerTag() {
        return
            (ShowContainer)findAncestorWithClass(this, ShowContainer.class);
    }

    public int doStartTag() throws JspException {
        ShowContainer parent = getContainerTag();
        // handle static JSP/HTML text
        parent.handleText();
        return EVAL_BODY_TAG;
    }

    public int doEndTag() throws JspException {
        Node n = findFirstMatch(getName());
        if (n != null) {
            // we have a match.  do a deep copy
            // this is equivalent to XSLT xsl:copy-of
            Node toAdd = getResultDocument().importNode(n, true);
            ShowContainer parent = getContainerTag();
            parent.getOutputContext().appendChild(toAdd);
        }
        return EVAL_PAGE;
    }

    Node findFirstMatch(final String name) {
        // traverse from the input context node down until we find
        // the first node such that @name=$name or @id=$name
        NodeFilter nf = new NodeFilter() {
                public short acceptNode(Node n) {
                    if (n instanceof Element) {
                        Element elt = (Element)n;
                        if (elt.getAttribute("name").equals(name)
                            || elt.getAttribute("id").equals(name)) {
                            return FILTER_ACCEPT;
                        }
                    }
                    return FILTER_SKIP;
                }
            };
        TreeWalker walker = createTreeWalker(nf);
        Node n = walker.nextNode();
        if (n == null) {
            // no node available.
            // try to look up same in slave document.
            Node slaveNode = (Node)pageContext.getRequest()
                .getAttribute(SLAVE_INPUT_DOC);
            if (slaveNode != null) {
                walker = createTreeWalker(slaveNode, nf);
                n = walker.nextNode();
            }
        }
        return n;
    }

    // ---------- attributes ------------

    public String getName() {
        return m_name;
    }

    public void setName(String s) {
        m_name = s;
    }

}

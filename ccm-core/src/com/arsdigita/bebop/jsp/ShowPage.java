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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.templating.Templating;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tag for showing components from a Bebop page within JSP.  This
 * tag takes a com.arsdigita.xml.Document from the request attribute
 * of the same name.
 * <p>
 * This is the top-level show:... tag.  You specify the page to use
 * either with a fully-qualified class name (e.g.,
 * "com.arsdigita.ui.login.UserRegistrationPage") or the path to a JSP
 * that builds a page and puts its XML output document in the
 * "com.arsdigita.xml.Document" request attribute.
 * <p>
 * The closing tag
 * will pass off the result XML document to another presentation manager
 * for rendering with XSLT.
 *<p>
 * Usage is:
 * <pre>
 * &lt;show:page [pmClass=...] [pageClass="pageClass"]>
 * Hi, I'm writing JSP and &lt;b>HTML&lt;/b> code.  &lt;%= java_expression %>
 * &lt;p> This doesn't even have to be well-formed HTML.
 *   &lt;show:component name="myBebopComponent"/>
 * &lt;/show:page>
 * </pre>
 * Resulting XML is: <pre>
 * &lt;bebop:page>
 *   ... contents from nested JSP content + tags ...
 * &lt;/bebop:page>
 * 
 * @version $Id: ShowPage.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class ShowPage extends ShowContainer {

    private com.arsdigita.xml.Document m_resultDoc;
    private com.arsdigita.xml.Document m_inputDoc;
    private Document m_resultDom;
    private Document m_inputDom;
    private Node m_inputContext;
    private Node m_outputContext;
    private String m_master;

    /**
     * Creates a new result document and sets up the context
     * within the document for nested tags to add to.  Also
     * sets up the context within the input document for nested
     * tags to retrieve relative to.  <p>
     * The show:page tag itself is a special case.  It creates
     * a top-level &lt;bebop:page> element and nothing else.
     */
    public int doStartTag() throws JspException {
        HttpServletResponse resp =
            (HttpServletResponse)pageContext.getResponse();
        if (resp.isCommitted()) {
            // we already committed the response (redirect).  Don't bother
            // trying to do anything with output
            return SKIP_BODY;
        }

        m_resultDoc = null;
        try {
            m_resultDoc = new com.arsdigita.xml.Document();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new JspWrapperException("cannot create XML document", e);
        }
        m_resultDom = m_resultDoc.getInternalDocument();

        if (m_inputDoc == null) {
            // fetch doc from JSP
            m_inputDoc = (com.arsdigita.xml.Document)pageContext.getRequest()
                .getAttribute(INPUT_DOC_ATTRIBUTE);
        }

        m_inputDom = m_inputDoc.getInternalDocument();
        Element pageElement = m_inputDom.getDocumentElement();
        Node resultPageNode = m_resultDom.importNode(pageElement, false);
        Element titleElement = m_resultDom.createElementNS(BEBOP_XMLNS,
                                                           "bebop:title");
        resultPageNode.appendChild(titleElement);
        m_resultDom.appendChild(resultPageNode);
        m_outputContext = resultPageNode;
        m_inputContext = pageElement;
        return EVAL_BODY_TAG;
    }

    /**
     * Serves the result document with a presentation manager.
     * If this page has a master page, then pass control to
     * the master with the result document as a request parameter.
     */
    public int doEndTag() throws JspException {
        try {
            if (m_master == null) {
                servePageWithPresentationManager();
            } else {
                // stuff result *and input* page in request attribute
                // the input page is necessary in case you need
                // to fish something out in the master page that we didn't
                // show:... here in the slave
                pageContext.getRequest().setAttribute(SLAVE_DOC,
                                                      m_resultDoc);

                // tacit contract with ShowComponent
                pageContext.getRequest().setAttribute(SLAVE_INPUT_DOC,
                                                      m_inputContext);
                DispatcherHelper.forwardRequestByPath(m_master, pageContext);
            }
        } catch (java.io.IOException ioe) {
            throw new JspWrapperException("cannot serve page", ioe);
        } catch (ServletException se) {
            if (se.getRootCause() != null) {
                throw new JspWrapperException("cannot serve page", se.getRootCause());
            } else {
                throw new JspException("cannot serve page: " + se.toString());
            }
        }
        return EVAL_PAGE;
    }

    Node getInputContext() {
        // package-protected visibility is intentional
        return m_inputContext;
    }

    Node getOutputContext() {
        // package-protected visibility is intentional
        return m_outputContext;
    }

    /**
     * returns the result document that JSP tags are building.  Necessary
     * for calling DOM methods createElement, createTextNode, etc.
     */
    Document getResultDocument() {
        // package-protected visibility is intentional.
        return m_resultDom;
    }

    Document getInputDocument() {
        // package-protected visibility is intentional.
        return m_inputDom;
    }

    // helper method.
    private void servePageWithPresentationManager()
    {
        HttpServletRequest req =
            (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse resp =
            (HttpServletResponse)pageContext.getResponse();
        
        Templating.getPresentationManager()
            .servePage(m_resultDoc, req, resp);
    }

    /**
     * When a page class is specified as an attribute, we build
     * the page object and generate its XML document for use in
     * this JSP page.
     */
    public void setPageClass(String s) throws JspException {
        try {
            HttpServletRequest req =
                (HttpServletRequest)pageContext.getRequest();
            HttpServletResponse resp =
                (HttpServletResponse)pageContext.getResponse();
            Class pageClass = Class.forName(s);
            Page p = (Page)pageClass.newInstance();
            com.arsdigita.xml.Document doc = new com.arsdigita.xml.Document();
            PageState state = p.process(req, resp);
            p.generateXML(state, doc);

            m_inputDoc = doc;
            pageContext.getRequest().setAttribute(INPUT_DOC_ATTRIBUTE, doc);
            pageContext.setAttribute(INPUT_PAGE_STATE_ATTRIBUTE, state);
        } catch (ClassNotFoundException e) {
            throw new JspWrapperException("cannot find presentation manager class: " + s, e);
        } catch (InstantiationException e) {
            throw new JspWrapperException("class is abstract in: " + s, e);
        } catch (IllegalAccessException e) {
            throw new JspWrapperException("constructor is not public in: " + s, e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new JspWrapperException("cannot create xml document ", e);
        } catch (ServletException e) {
            if (e.getRootCause() != null) {
                throw new JspWrapperException(e);
            } else {
                throw new JspException(e.toString());
            }
        }
    }

    public void setMaster(String s) {
        m_master = s;
    }
}

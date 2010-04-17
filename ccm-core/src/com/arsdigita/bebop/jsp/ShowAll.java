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
package com.arsdigita.bebop.jsp;

import com.arsdigita.templating.Templating;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;

/**
 * A simple replacement for ShowPage which simply
 * passes the input document from DefinePage straight
 * through to the presentation manager. So rather than
 * having to put a &lt;show:component&gt; tag for every
 * &lt;define:component&gt; tag, you simply need a single
 * &lt;show:all&gt; tag.
 * 
 * @version $Id: ShowAll.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class ShowAll extends ShowContainer {

    public static final Logger s_log = Logger.getLogger(ShowAll.class);

    private com.arsdigita.xml.Document m_resultDoc;
    private com.arsdigita.xml.Document m_inputDoc;
    private Document m_resultDom;
    private Document m_inputDom;
    private Node m_inputContext;
    private Node m_outputContext;

    /**
     * Creates a new result document
     */
    public int doStartTag() throws JspException {
        HttpServletResponse resp =
            (HttpServletResponse)pageContext.getResponse();
        if (resp.isCommitted()) {
            s_log.debug("Already committed, so skiping");
            // we already committed the response (redirect).  Don't bother
            // trying to do anything with output
            return SKIP_BODY;
        }

        m_inputDoc = (com.arsdigita.xml.Document)pageContext.getRequest()
            .getAttribute(INPUT_DOC_ATTRIBUTE);
        m_resultDoc = m_inputDoc;
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Outputing doc " + pageContext.getRequest() + 
                        " " + m_resultDoc.toString(true));
        }

        m_inputDom = m_inputDoc.getInternalDocument();
        m_resultDom = m_inputDom;

        Element pageElement = m_inputDom.getDocumentElement();
        Node resultPageNode = m_resultDom.getDocumentElement();

        m_outputContext = resultPageNode;
        m_inputContext = pageElement;
        return EVAL_BODY_TAG;
    }

    /**
     * Serves the result document with a presentation manager.
     */
    public int doEndTag() throws JspException {
      servePageWithPresentationManager();
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
    private void servePageWithPresentationManager() {
      HttpServletRequest req =
        (HttpServletRequest)pageContext.getRequest();
      HttpServletResponse resp =
        (HttpServletResponse)pageContext.getResponse();

      Templating.getPresentationManager()
          .servePage(m_resultDoc, req, resp);
    }
}

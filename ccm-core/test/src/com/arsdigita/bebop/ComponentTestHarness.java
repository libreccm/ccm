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
package com.arsdigita.bebop;

import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.util.RequestEnvironment;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import java.io.File;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;

/**
 * This is a test harness for Bebop components. Allows generation of XML, HTML, manipulation of HTTP Request parameters.
 * The stylesheet used for HTML generation can be altered. By default, it is bebop.xsl.
 *
 */
public class ComponentTestHarness {

    private static final Logger s_log = Logger.getLogger(ComponentTestHarness.class);
    // Page wrapper for component, or component itself if it is a page.
    private Page m_page;
    // Component under test
    private Component m_component;

    private RequestEnvironment m_environment = new RequestEnvironment();
    // Page state for component
    private PageState m_ps;

    // Transformer used for HTML generation
    private Transformer m_transformer;

    /**
     * Constructs a new test harness for the component.
     *
     * @param c The component to test.
     */
    public ComponentTestHarness(Component c) {

        m_page = getPageForComponent(c);
        if (!m_page.isLocked()) {
            m_page.lock();
        }

        m_component = c;

        setDefaultStylesheet();
        setupPageState();

    }

    private Page getPageForComponent(Component c) {
        if (c instanceof Page) {
            return (Page) c;
        }
        final Page page = new Page();
        page.add(c);
        return page;
    }


    /**
     * @return The component under test
     */
    public Component getComponent() {
        return m_component;
    }

    public PageState getPageState() {
        return m_ps;
    }
    /**
     * Call generateXML on the containing page of the component, return the result.
     *
     * @return The new XML Document
     */
    public Document generateXML() {
        try {

            Document result = new Document();
            m_page.generateXML(m_ps, result);
            return result;

        } catch(ParserConfigurationException pe ) {
            throw new UncheckedWrapperException("Parser error", pe);
        }

    }

    /**
     * Generates HTML using the stylesheet passed to setPrimaryStylesheet.
     * By default, this is bebop.xsl
     *
     * @return The generated HTML in a String
     */
    public String generateHTML() {
        Document doc = generateXML();
        return generateHTML(doc);
    }

    /**
     * Generates HTML using the stylesheet passed to setPrimaryStylesheet.
     * By default, this is bebop.xsl. Code taken from BasePresentationManager.
     *
     * @param doc The document to be transformed
     *
     * @return The generated HTML in a String
     */
    private String generateHTML(Document doc) {

        String defaultCharset = Globalization.getDefaultCharset(m_environment.getRequest().getLocale());
        m_transformer.setParameter("contextPath", m_environment.getRequest().getContextPath());
        m_transformer.setOutputProperty("encoding", defaultCharset);
        StringWriter result = new StringWriter();
        try {
            m_transformer.transform(new DOMSource(doc.getInternalDocument()),
                         new StreamResult(result));

        } catch( TransformerException e ) {
            throw new UncheckedWrapperException(e);
        }

        return result.toString();
    }

    /**
     * Gets the dummy ServletRequest. Allows manipulation of the underlying Component.
     *
     * @return HttpServletDummyRequest
     */
    public HttpServletDummyRequest getServletRequest() {
        return m_environment.getRequest();
    }

    /**
     * Get the dummy ServletResposne.
     *
     * @return HttpServletDummyResponse
     */
    public HttpServletDummyResponse getServletResponse() {
        return m_environment.getResponse();
    }

    private boolean usingSaxon() {
        final String trans = System.getProperty
            ("javax.xml.transform.TransformerFactory");

        if (trans == null) {
            return false;
        } else {
            return trans.indexOf("saxon") != -1;
        }
    }

    private boolean usingXSLTC() {
        final String trans = System.getProperty
            ("javax.xml.transform.TransformerFactory");

        if (trans == null) {
            return false;
        } else {
            return trans.indexOf("xsltc") != -1;
        }
    }

    /**
     * Sets the primary stylesheet for generating HTML from the component under test.
     * Currently accepts relative paths, i.e. web/packages/bebop/xsl/bebop.xsl
     *
     * Code nicked from Stylesheet. Should be changed if that code is ever refactored out
     * into a more public and useful place.
     *
     * @param filename Relative path to stylesheet.
     */
    public void setPrimaryStylesheet(String filename) {
        File stylesheet = new File(filename);
        StreamSource ssSource = new StreamSource(stylesheet);
        final String trnsformerLib = System.getProperty("PreferredXSLTTransformer");
        if (usingSaxon()) {
            String pathn = stylesheet.getAbsolutePath();
            ssSource.setSystemId(pathn);
        }

        TransformerFactory fact = TransformerFactory.newInstance();
        if (usingXSLTC()) {
            //disable template inlining, otherwise xsltc might generate methods
            //that are too long, or contain jump offsets that are too large for
            //the JVM to handle for more details see "Known problems for XSLTC
            //Translets - http://xml.apache.org/xalan-j/xsltc_constraints.html#xsltcknownproblems
            fact.setAttribute("disable-inlining", Boolean.TRUE);
        }
        try {
            Templates template = fact.newTemplates(ssSource);
            m_transformer = template.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new UncheckedWrapperException(e);
        }

    }

    /**
     * Setup the default transformer, which uses the bebop stylesheet.
     */
    public void setDefaultStylesheet() {
        setPrimaryStylesheet(System.getProperty("j2ee.webapp.dir") + "/webapps/ROOT/packages/bebop/xsl/bebop.xsl");
    }

    private void setupPageState() {
        try {
            m_ps = new PageState(m_page, m_environment.getRequest(), m_environment.getResponse());
        } catch(ServletException e) {
            throw new UncheckedWrapperException(e);
        }

    }



}

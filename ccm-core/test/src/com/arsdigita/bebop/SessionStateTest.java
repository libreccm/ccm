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

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.RequestEnvironment;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * Tests saving the page state to the HttpSession.
 */
public class SessionStateTest extends TestCase {

    private Page m_testPage;
    private Component c1;
    private Component c2;
    private Component link1;
    private Component link2;
    private RequestEnvironment m_env;

    private static Logger s_log =
        Logger.getLogger(SessionStateTest.class);

    public SessionStateTest(String name) {
        super(name);
        m_testPage = new Page("new page title");
        m_testPage.setUsingHttpSession(true);

        // add two components that change their state in response
        // to a control event
        // we should be able to change them independently.
        c1 = new StatefulText("param1");
        m_testPage.add(c1);

        c2 = new StatefulText("param2");
        m_testPage.add(c2);

        m_testPage.add(link1 = new ControlLink("click for param1") {
                public void setControlEvent(PageState s) {
                    s.setControlEvent(c1, "set", "p1-value");
                }
            });

        m_testPage.add(link2 = new ControlLink("click for param1") {
                public void setControlEvent(PageState s) {
                    s.setControlEvent(c2, "set", "p2-value");
                }
            });
        m_testPage.lock();
    }


    protected void setUp() throws Exception {
        super.setUp();
         m_env = new RequestEnvironment();
    }

    /**
     * tests a page with two stateful components and two control
     * links.  makes sure control links are good.
     * <p>
     * then follows a control link which changes state on component 1.
     * verifies that new page has right state, and that control links
     * don't exportUsers state directly.
     * <p>
     * follows the second control link.  verifies that the state on
     * component 1 is preserved, after already checking that the state
     * was not exported directly in the URL.
     */
    public void testSessionState() throws Exception {
        // look at the control links for c1 and c2
        // validate that there are no URL variables in any
        // of the links except the control event (there should be four
        // at most: bbp.e, bbp.v, bbp.s, and bbp.session)
        Document doc = m_testPage.buildDocument(m_env.getRequest(), m_env.getResponse())
            .getInternalDocument();

        // doc should have two control links with "href" sections
        DocumentTraversal dt = (DocumentTraversal)doc;
        TreeWalker tw =
            dt.createTreeWalker(doc, NodeFilter.SHOW_ALL,
                                new NamedNodeFilter("bebop:link"), false);
        Node n = tw.nextNode();
        assertTrue(n != null);
        String href = ((Element)n).getAttribute("href");
        assertTrue (href != null);
        String queryString = href.substring(href.indexOf('?') + 1);
        verifyQueryString(queryString);

        // SECOND REQUEST -- verify that the first bebop:label is
        // "p1-value" and that the control events still look good
        // (no state in them)
        HttpSession sess = m_env.getRequest().getSession();

        m_env.getRequest().setSession(sess);
        s_log.info("parsing query string: " + queryString);
        exportQueryString(m_env.getRequest(), queryString);

        doc = m_testPage.buildDocument(m_env.getRequest(), m_env.getResponse())
            .getInternalDocument();
        tw = dt.createTreeWalker(doc, NodeFilter.SHOW_ALL,
                                 new NamedNodeFilter("bebop:label"), false);
        n = tw.nextNode();
        assertTrue(n != null);
        String str = ((CharacterData)n.getFirstChild()).getData();
        s_log.info(str);
        assertTrue(str.equals("p1-value"));

        // follow the second control link
        tw = dt.createTreeWalker(doc, NodeFilter.SHOW_ALL,
                                 new NamedNodeFilter("bebop:link"), false);
        n = tw.nextNode();
        assertTrue(n != null);
        n = tw.nextNode();
        assertTrue(n != null);
        href = ((Element)n).getAttribute("href");
        assertTrue(href != null);
        // make sure it looks good
        queryString = href.substring(href.indexOf('?') + 1);
        verifyQueryString(queryString);

        // THIRD REQUEST -- follow the other control link to set
        // p2-value.  Make sure p1-value is still preserved without
        // the benefit of passing the state directly through the URL.
        sess = m_env.getRequest().getSession();

        m_env.getRequest().setSession(sess);
        s_log.info("parsing query string: " + queryString);
        exportQueryString(m_env.getRequest(), queryString);

        doc = m_testPage.buildDocument(m_env.getRequest(), m_env.getResponse())
            .getInternalDocument();
        tw = dt.createTreeWalker(doc, NodeFilter.SHOW_ALL,
                                 new NamedNodeFilter("bebop:label"), false);
        n = tw.nextNode();
        assertTrue(n != null);
        str = ((CharacterData)n.getFirstChild()).getData();
        s_log.info(str);
        assertTrue(str.equals("p1-value"));

        n = tw.nextNode();
        assertTrue(n != null);
        str = ((CharacterData)n.getFirstChild()).getData();
        s_log.info(str);
        assertTrue(str.equals("p2-value"));

        tw = dt.createTreeWalker(doc, NodeFilter.SHOW_ALL,
                                 new NamedNodeFilter("bebop:link"), false);
        n = tw.nextNode();
        assertTrue(n != null);
        assertTrue(n instanceof Element);
        href = ((Element)n).getAttribute("href");
        verifyQueryString(href);
        n = tw.nextNode();
        assertTrue(n != null);
        assertTrue(n instanceof Element);
        href = ((Element)n).getAttribute("href");
        verifyQueryString(href);
    }

    /**
     * Tests what happens if we make a request without a valid pagestate
     * in the HttpSession.
     */
    public void testNoSession() throws Exception {
        exportQueryString(m_env.getRequest(), "bbp.session=29");
        try {
            m_testPage.buildDocument(m_env.getRequest(), m_env.getResponse());
            fail();
        } catch (SessionExpiredException see) {
            // this is success
        }
    }

    private void verifyQueryString(String queryString) {
        assertTrue(queryString.indexOf("bbp.e=") != -1);
        assertTrue(queryString.indexOf("bbp.v=") != -1);
        assertTrue(queryString.indexOf("bbp.s=") != -1);
        assertTrue(queryString.indexOf("bbp.session=") != -1);
        assertTrue(queryString.indexOf("param1") == -1);
        assertTrue(queryString.indexOf("param2") == -1);
    }

    private static void exportQueryString(HttpServletDummyRequest req,
                                          String queryString) {
        Hashtable map = HttpUtils.parseQueryString(queryString);
        for (Enumeration e = map.keys(); e.hasMoreElements(); ) {
            String name = (String)e.nextElement();
            String[] values = (String[])map.get(name);
            req.setParameterValues(name, values);
        }
    }

    private class StatefulText extends SimpleComponent {
        private ParameterModel m_param;

        public StatefulText(String name) {
            m_param = new StringParameter(name);
        }

        public void register(Page p) {
            p.addComponentStateParam(this, m_param);
        }

        public void respond(PageState ps) {
            s_log.debug("responding to control event: " + m_param.getName());
            String value = ps.getControlEventValue();
            ps.setValue(m_param, value);
        }

        public void generateXML(PageState ps, com.arsdigita.xml.Element parent) {
            com.arsdigita.xml.Element elt = new com.arsdigita.xml.Element("bebop:label", BEBOP_XML_NS);
            String param = (String)ps.getValue(m_param);
            if (param == null) {
                param = "(no value)";
            }
            elt.setText(param.toString());
            parent.addContent(elt);
        }
    }

    private class NamedNodeFilter implements NodeFilter {
        private String m_name;
        public NamedNodeFilter(String name) {
            m_name = name;
        }

        public short acceptNode(Node n) {
            if (n instanceof Element) {
                Element elt = (Element)n;
                if (elt.getNodeName().equals(m_name)) {
                    return FILTER_ACCEPT;
                }
            }
            return FILTER_SKIP;
        }
    }
}

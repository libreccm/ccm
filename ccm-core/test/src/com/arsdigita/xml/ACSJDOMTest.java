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
package com.arsdigita.xml;

import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** 
 * 
 * @version $Id: ACSJDOMTest.java 750 2005-09-02 12:38:44Z sskracic $
 */
public class ACSJDOMTest extends TestCase {

    public static final String BEBOP_XML_NS =
        "http://www.arsdigita.com/bebop/1.0";

    public ACSJDOMTest ( String name ) {
        super (name);
    }

    public static void main ( String args[] ) {
        junit.textui.TestRunner.run(suite());
    }

    protected void setUp () {
    }

    public static Test suite () {
        TestSuite suite = new TestSuite();
        suite.addTest(new ACSJDOMTest("testJDOMOutput"));
        suite.addTest(new ACSJDOMTest("testJDOMConcurrency"));
        return suite;
    }

    public void testJDOMOutput () throws Exception {
        Transformer xformer =
            TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream os;

        /*
         * Construct a DOM document...
         */
        org.w3c.dom.Document domDoc = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .newDocument();

        org.w3c.dom.Element domPage =
            domDoc.createElementNS(BEBOP_XML_NS, "page");
        domDoc.appendChild(domPage);

        org.w3c.dom.Element domTitle =
            domDoc.createElementNS(BEBOP_XML_NS, "title");
        domPage.appendChild(domTitle);
        domTitle.setAttribute("fontweight", "bold");
        domTitle.appendChild(domDoc.createTextNode("Title goes here"));

        org.w3c.dom.Element domBoxPanel =
            domDoc.createElementNS(BEBOP_XML_NS, "boxPanel");
        domPage.appendChild(domBoxPanel);
        domBoxPanel.setAttribute("bgcolor", "ffffff");

        org.w3c.dom.Element domCell =
            domDoc.createElementNS(BEBOP_XML_NS, "cell");
        domBoxPanel.appendChild(domCell);
        domCell.appendChild(domDoc.createTextNode("Name?"));

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(domDoc), new StreamResult(os));
        String domString = os.toString();

        /*
         * Now make a JDOM document
         */
        Document jdomDoc = new Document();

        Element jdomPage = new Element("page", BEBOP_XML_NS);
        jdomDoc.setRootElement(jdomPage);

        Element jdomTitle = new Element("title", BEBOP_XML_NS);
        jdomPage.addContent(jdomTitle);
        jdomTitle.addAttribute("fontweight", "bold");
        jdomTitle.setText("Title goes here");

        Element jdomBoxPanel = new Element("boxPanel", BEBOP_XML_NS);
        jdomPage.addContent(jdomBoxPanel);
        jdomBoxPanel.addAttribute("bgcolor", "ffffff");

        Element jdomCell = new Element("cell", BEBOP_XML_NS);
        jdomBoxPanel.addContent(jdomCell);
        jdomCell.setText("Name?");

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(jdomDoc.getInternalDocument()),
                           new StreamResult(os));
        String jdomString = os.toString();

        assertEquals("DOMs do not match.\n\n" +
                     "DOM version: " + domString + "\n" +
                     "JDOM version: " + jdomString,
                     domString,
                     jdomString);
    }

    public void testJDOMConcurrency () throws Exception {
        Transformer xformer =
            TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream os;

        Document dom1 = new Document();

        Element dom2root = new Element("rootNode");
        Document dom2 = new Document(dom2root);

        Element dom1root = new Element("rootNode");
        dom1.setRootElement(dom1root);

        Element dom2branch = new Element("branch");
        Element dom1branch = new Element("branch");

        dom1root.addContent(dom1branch);
        dom2root.addContent(dom2branch);

        Element dom2leaf = new Element("leaf");
        Element dom1leaf = new Element("leaf");

        dom2branch.addContent(dom2leaf);
        dom1branch.addContent(dom1leaf);

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(dom1.getInternalDocument()),
                           new StreamResult(os));
        String dom1String = os.toString();

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(dom2.getInternalDocument()),
                           new StreamResult(os));
        String dom2String = os.toString();

        assertEquals("DOMs do not match.\n\n" +
                     "DOM1 version: " + dom1String + "\n" +
                     "DOM2 version: " + dom2String,
                     dom1String,
                     dom2String);
    }

    /*
      assertNotNull(seqValue1);
      assertEquals("nextval followed by currval didn't get the " +
      "same thing.  This might just mean someone else " +
      "called nextval in the middle.",
      seqValue1,seqValue2);

      assert(! seqValue1.equals(seqValue2));
    */


}

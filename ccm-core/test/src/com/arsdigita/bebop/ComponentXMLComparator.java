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

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

/**
 *  This class implements base functionality for generating XML from a
 *  Bebop Component, and comparing that XML to canonical files that
 *  are under change control.
 *
 *  An option is provided via {@link #GENERATE_FILES} to (re) generate the XML files
 *  for a Component.
 *
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 */
public abstract class ComponentXMLComparator {

    public static final String versionId = "$Id: ComponentXMLComparator.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static Logger log =
        Logger.getLogger(ComponentXMLComparator.class.getName());

    /**
     *  XXX This flag controls whether the class compares XML files,
     *  or attemps to generate new files. This is a bit of a hack,
     *  but is an extremely convenient way to (re)generate the canonical
     *  XML files within the test framework.
     */
    static boolean GENERATE_FILES = false;
    private static final String XMLReferenceDir =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/bebop/xml/";
    private String m_testPrefix = "default";

    private final ComponentTestHarness m_harness;
   // The name of the XML file to compare against.
    private String m_XMLFileName;

    /**
     *  Constructor. Takes a Component and fileName. The Component is
     *  wrapped in a Page object if it is not itself a Page.
     *
     *  9 out of 10 times, it's evil to use <code>instanceof</code>,
     *  but due to the unique role of Page objects in Bebop XML
     *  generation, it is appropriate here. Only wrap * non-Page
     *  Components in a Page to generate XML. The Page constructor
     *  really should * be used instead, but this exists just in case
     *  this class is used in some polymorphic * context.
     *
     *  @param c The Component to generate and compare XML for.
     *  @param testName The name of the test, which is used as the prefix for the filename.
     *  @param fileName The name of the XML file to compare against.
     */
    protected ComponentXMLComparator(Component c, String testName, String fileName) {

        m_harness = new ComponentTestHarness(c);

        if ( testName != null ) {
            m_testPrefix = testName;
        }

        m_XMLFileName = XMLReferenceDir + m_testPrefix + "-" + fileName;
    }


    /**
     *  This test method gets the XML for a given component, and compares it against
     *  a change controlled version of the xml file.
     *
     *  The test will fail if there are any errors generating the XML, or if the
     *  newly generated XML differs from the file on disk.
     *
     *  The XML file will be FULL_CLASS_NAME.xml,
     *  ex. com.arsdigita.bebop.ColumnPanel.xml.
     *
     *  If there are differences in the XML, the new xml will be output. The file
     *  name will have a -BAD suffix.
     *
     *  @exception Exception if there are any errors.
     */
    public void testXMLGeneration() throws XMLComparatorError, Exception {
        final com.arsdigita.xml.Document xml = m_harness.generateXML();

        if( GENERATE_FILES ) {
            log.info("Generating: " + getXMLFileName() );
            writeXMLFile( getXMLFileName(), xml.getInternalDocument() );
        }
        else {
            compareXML(xml.getInternalDocument());
        }
    }

    /**
     *
     * This method converts an org.w3c.dom.Document and applies the
     * identity transform to convert the Document into a character
     * array.
     *
     * @param xml The XML document.
     *
     */
    private char[] DocumentToCharArray(Document xml) throws TransformerConfigurationException, TransformerException {

        DOMSource canonicalDocumentSource = new DOMSource(xml);
        CharArrayWriter canonicalStream = new CharArrayWriter();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(canonicalDocumentSource, new StreamResult(canonicalStream));

        char[] buffer = canonicalStream.toCharArray();

        return buffer;

    }

    /**
     *  This method reads in the original XML file, and compares it to the
     *  newly generated XML. If there are differences, a new file will be
     *  output, and the test will fail.
     *
     *  The file name will have the -BAD suffix.
     *
     *  @param xml The xml of the component.
     * */
    private void compareXML(Document xml) throws XMLComparatorError, Exception {

        File canonicalDocumentFile = new File(getXMLFileName());

        SAXBuilder builder = new SAXBuilder(false);
        org.jdom.Document original = builder.build(canonicalDocumentFile);
        DOMBuilder domb = new DOMBuilder();
        org.jdom.Document current = domb.build(xml);
        XMLOutputter out = new XMLOutputter();

        final Diff diff = new Diff(out.outputString(original), out.outputString(current));


        DetailedDiff detail = new DetailedDiff(diff);

        if( detail.getAllDifferences().size() > 0 ) {
            writeBadFile( xml );

            throw new XMLComparatorError("Files differ! File Name is: " + getXMLFileName() +
                    System.getProperty("line.separator") +
                    detail);
        }

    }

    /**
     *  This method is called if the newly generated XML differs from the version
     *  on disk. This method generates a new file.
     *
     *  The file name will have the -BAD suffix.
     *
     *  @param xml The xml of the component.
     *
     */
    private void writeBadFile(final Document xml) throws IOException, Exception {
        final String fileName = getXMLFileName() + "-BAD";
        writeXMLFile( fileName, xml );
    }

    /**
     *  This method is called to generate an XML file.
     *
     *  @param fileName The name of the file to create
     *  @param xml The xml of the component
     *
     */
    private void writeXMLFile(final String fileName, Document xml) throws IOException, Exception {

        FileOutputStream xmlFile = null;

        try {
            xmlFile = new FileOutputStream(fileName);
            XMLOutputter out = new XMLOutputter();
            DOMBuilder builder = new DOMBuilder();
            final org.jdom.Document document = builder.build(xml);
            Namespace bebopNS = Namespace.getNamespace("bebop", Component.BEBOP_XML_NS);
            document.getRootElement().addNamespaceDeclaration(bebopNS);
            out.output(document, xmlFile);
        }
        finally {
            if( null != xmlFile ) {
                try {
                    xmlFile.close();
                }
                catch(IOException e) {
                }
            }
        }

    }

    /**
     *  Gets name of XML file.
     *
     *  @return name of file.
     */
    protected String getXMLFileName() {
        return m_XMLFileName;
    }

}

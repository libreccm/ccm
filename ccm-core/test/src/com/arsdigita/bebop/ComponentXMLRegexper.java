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

import com.arsdigita.util.Assert;
import java.io.CharArrayWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.apache.oro.text.perl.Perl5Util;
import org.w3c.dom.Document;

/**
 *  This class implements base functionality for generating XML from a
 *  Bebop Component, and comparing that XML to a regular expression
 *
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class ComponentXMLRegexper
{

    public static final String versionId = "$Id: ComponentXMLRegexper.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static DocumentBuilderFactory s_DocumentBuilderFactory;

    // The Page to use for XML generation.
    private Page m_page;
    // The regular expression to compare against.
    private String m_regexp;
    // The name of the XML file to compare against.
    private String m_XMLFileName;
    private static final String XMLReferenceDir =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/bebop/xml/";
    private Perl5Util m_perl5;

    /**
     *  Constructor. Takes a Component and regular expression. The
     *  Component is wrapped in a Page object if it is not itself a
     *  Page.
     *
     *  9 out of 10 times, it's evil to use <code>instanceof</code>,
     *  but due to the unique role of Page objects in Bebop XML
     *  generation, it is appropriate here. Only wrap non-Page
     *  Components in a Page to generate XML. The Page constructor
     *  really should be used instead, but this exists just in case
     *  this class is used in some polymorphic context.
     *
     *  @param c The Component to generate and compare XML for.
     *      @param regexp regular expression to compare against.
     */
    protected ComponentXMLRegexper(Component c, String regexp) {
        if ( s_DocumentBuilderFactory == null ) {
            s_DocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            s_DocumentBuilderFactory.setNamespaceAware(true);
        }

        if( !(c instanceof Page) ) {
            m_page = makePageForComponent(c);
        } else {
            m_page = (Page)c;
        }

        m_perl5 = new Perl5Util();
        m_regexp = regexp;

        m_XMLFileName
            = XMLReferenceDir + "regexp-" + c.getClass().getName() + ".xml";
    }

    /**
     *  Utility method for wrapping a Component in a Page.
     *
     *  @param c Component to wrap.
     *
     *  @return A Page object that wraps c.
     *
     *  @pre !(c instanceof Page)
     *  @post retval.isLocked()
     */
    private Page makePageForComponent( Component c )
    {
        Assert.assertTrue( !(c instanceof Page) );

        Page page = new Page();
        page.add( c );
        page.lock();

        return page;
    }

    /**
     *  This test method gets the XML for a given component, and
     *  compares it against a regular expression.
     *
     *  The test will fail if there are any errors generating the
     *  XML, or if the generated XML does not match the regexp.
     *
     *  If XML does not match, it will be output. The file
     *  name will have a -BAD suffix.
     *
     *      @param is_debug Should the RequestContext indicate debugging?
     *  @exception Exception if there are any errors.  */
    public void testXMLGeneration(boolean is_debug)
        throws XMLComparatorError, Exception {
        final com.arsdigita.xml.Document xml
            = ComponentToXML.getDocument(m_page, is_debug);
        TestCase.assertTrue("getDocument must not return null", xml != null);
        regexpXML(xml.getInternalDocument());
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
    private char[] DocumentToCharArray(Document xml)
        throws TransformerConfigurationException, TransformerException {

        DOMSource canonicalDocumentSource = new DOMSource(xml);
        CharArrayWriter canonicalStream = new CharArrayWriter();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(canonicalDocumentSource,
                             new StreamResult(canonicalStream));
        char[] buffer = canonicalStream.toCharArray();

        return buffer;
    }

    /**
     *  This method compares the newly generated XML against the
     *  regexp. If it doesn't match, a new file will be output, and
     *  the test will fail.
     *
     *  The file name will have the -BAD suffix.
     *
     *  @param xml The xml of the component.
     * */
    private void regexpXML(Document xml)
        throws XMLComparatorError, Exception {
        char[] buffer = DocumentToCharArray(xml);
        String text = new String(buffer);

        final boolean does_match
            = m_perl5.match("m$" + m_regexp + "$", text);

        if (!does_match) {
            writeBadFile( xml );
            throw new XMLComparatorError("XML does not match '" + m_regexp
                                         + "'.");
        }
    }

    /**
     *  This method is called if the newly generated XML does not
     *  match the regular expression.  This method generates a new
     *  file.
     *
     *  The file name will have the -BAD suffix.
     *
     *  @param xml The xml of the component.
     * */
    private void writeBadFile(final Document xml)
        throws IOException, Exception {
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

        char[] buffer = DocumentToCharArray(xml);
        String xmlString = new String(buffer);
        FileOutputStream xmlFile = null;

        try {
            xmlFile = new FileOutputStream(fileName);
            xmlFile.write( xmlString.getBytes() );
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

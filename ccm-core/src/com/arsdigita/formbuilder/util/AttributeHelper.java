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
package com.arsdigita.formbuilder.util;


// I am persisting the attributes on XML format and
// retrieve the attributes from this format I use an
// SAX XML parser
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
// Jaxp classes
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import java.io.StringReader;


// For storing the attributes
import java.util.Map;

import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.oro.text.perl.Perl5Util;

/**
 * This class is used by the PersistentComponent to
 * convert HTML attributes back and forth between
 * an XML attribute format - one String, stored
 * in the database - and a Java Map key-value format.
 *
 * @author Peter Marklund
 * @version $Id: AttributeHelper.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class AttributeHelper {

    public static final String versionId = "$Id: AttributeHelper.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Used during SAX parsing by the DefaultHandler to set the
    // attributes that it finds.
    private com.arsdigita.bebop.util.Attributes m_attributesObject;

    /**
     * Copies attributes from the XML string to the Attributes
     * object
     */
    private DefaultHandler m_attributeHandler =
        new DefaultHandler() {
            public void startElement(String uri,
                                     String localName,
                                     String qName,
                                     Attributes attributes)
                throws SAXException {

                for (int i = 0; i < attributes.getLength(); ++i) {

                    AttributeHelper.this.m_attributesObject
                        .setAttribute((String)attributes.getQName(i),
                                      attributes.getValue(i));
                }
            }
        };

    /**
     * Takes a Bebop Attribute object
     * with the attributes of the Bebop Component and returns those attributes
     * in a String on XML attribute format.
     *
     * @return All attributes on the XML attribute format
     *         key1="value1" key2="value2" ... keyN="valueN"
     */
    public static String
        getAttributeString(com.arsdigita.bebop.util.Attributes attributes) {

        if (attributes == null) {
            return "";
        }

        // This is a contrived way of retrieving the attributes but the upside
        // is that I am not modifying the Bebop code in any way
        ExtendedElement componentElement =
            new ExtendedElement("ignore", "ignore");

        // Export the attributes to the element
        attributes.exportAttributes(componentElement);

        Perl5Util perl = new Perl5Util();

        // Iterate over the attributes and put them in a String on the
        // XML attribute format
        StringBuffer attributeStringBuffer = new StringBuffer();
        java.util.Map attributeMap = componentElement.getAttributes();
        java.util.Iterator attributeNameIterator =
            attributeMap.keySet().iterator();
        while (attributeNameIterator.hasNext()) {
            String attributeName = (String)attributeNameIterator.next();
            String attributeValue = (String)attributeMap.get(attributeName);
            
            Assert.isTrue(perl.match("/^\\w+$/",
                                         attributeName));            
            attributeValue = StringUtils.quoteHtml(attributeValue);

            attributeStringBuffer.append(attributeName + "=\"" +
                                         attributeValue + "\"");

            // If we are going to add more attributes, delimit with a space
            if (attributeNameIterator.hasNext()) {
                attributeStringBuffer.append(" ");
            }
        }

        return attributeStringBuffer.toString();
    }

    /**
     * Takes a String on XML attribute format and copies the attributes
     * in this String to a Attribute object
     *
     * @param attributeString The String with the XML attributes that we
     *                        are copying from.
     * @param attributesObject The Attribute object of a Bebop Component
     *                         that we are copying to.
     */
    public com.arsdigita.bebop.util.Attributes 
        getAttributesMap(String attributeString) {

        if (attributeString == null || attributeString.equals("")) {
            return new com.arsdigita.bebop.util.Attributes();
        }

        String xmlString = "<dummyElement " + 
            attributeString
            + " />";

        m_attributesObject = new com.arsdigita.bebop.util.Attributes();

        SAXParserFactory saxParserFactory;
        SAXParser saxParser;
        try {

            saxParserFactory = SAXParserFactory.newInstance();
            saxParser = saxParserFactory.newSAXParser();

            // The attribute handler object will copy the attribute values
            // to the HashMap of this AttributeHelper
            saxParser.parse(new InputSource(new StringReader(xmlString)),
                            m_attributeHandler);

        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

        return m_attributesObject;
    }
}

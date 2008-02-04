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
package com.arsdigita.templating.html;

import com.arsdigita.templating.html.HTMLParserException;
import com.arsdigita.templating.html.HTMLlat1;
import com.arsdigita.templating.html.HTMLspec;
import com.arsdigita.templating.html.HTMLsym;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX-based implementation of {@link HTMLParser}. Only good for parsing
 * XHTML. One way to ensure that the incoming text is a valid XHTML fragment is
 * to use a {@link com.arsdigita.bebop.parameters.TidyHTMLValidationListener
 * JTidy-based validation listener}.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-21
 * @version $Date: 2004/08/16 $
 **/
public class XHTMLParser implements HTMLParser {

    private final static Logger s_log =
        Logger.getLogger(XHTMLParser.class);
    private final static String ROOT_TAG = "root";
    private final static String OPENING_ROOT_TAG = "<" + ROOT_TAG +  ">";
    private final static String CLOSING_ROOT_TAG = "</" + ROOT_TAG+ ">";
    private final static String LINE_END = System.getProperty("line.separator");

    private final static Set s_emptyTags = new HashSet();

    static {
        s_emptyTags.add("br");
        s_emptyTags.add("hr");
    }

    private final Set m_tags;
    private SAXParser m_saxParser;

    public XHTMLParser() throws HTMLParserException {
        m_tags = new HashSet();
        try {
            m_saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException ex) {
            throw new HTMLParserException
                ("The underlying SAX parser can't be configured.", ex);
        } catch (SAXException ex) {
            throw new HTMLParserException
                ("Error instantiating a SAX parser", ex);
        }
    }

    public void registerTag(String qName) {
        m_tags.add(qName);
    }

    public boolean isRegistered(String qName) {
        return m_tags.contains(qName);
    }

    /**
     * Parses the <code>html</code> by invoking a SAX parser. The assumption is,
     * of course, that <code>html</code> is well-formed.
     **/
    public void parse(String html, ContentHandler handler)
        throws HTMLParserException {

        StringBuffer wellFormedHTML = new StringBuffer();
        // define reference entities that the HTML DTD supports
        wellFormedHTML.append("<!DOCTYPE html [").append(LINE_END);
        wellFormedHTML.append(HTMLlat1.getAllEntityDeclarations());
        wellFormedHTML.append(HTMLsym.getAllEntityDeclarations());
        wellFormedHTML.append(HTMLspec.getAllEntityDeclarations());
        wellFormedHTML.append(LINE_END).append("]>").append(LINE_END);
        // Add the root element to pacify the SAXParser
        wellFormedHTML.append(OPENING_ROOT_TAG).append(html).append(CLOSING_ROOT_TAG);

        char[] chars =new char[wellFormedHTML.length()];
        wellFormedHTML.getChars(0, wellFormedHTML.length(), chars, 0);
        CharArrayReader input = new CharArrayReader(chars);

        try {
            m_saxParser.parse
                (new InputSource(input), new SAXDefaultHandler(handler, this));
        } catch (SAXException ex) {
            throw new HTMLParserException(ex);
        } catch (IOException ex) {
            throw new HTMLParserException(ex);
        }
    }

    private static class SAXDefaultHandler extends DefaultHandler {

        private HTMLParser m_parser;

        private ContentHandler m_handler;
        private StringBuffer m_buffer;

        public SAXDefaultHandler(ContentHandler htmlHandler, HTMLParser parser) {
            m_handler = htmlHandler;
            m_parser = parser;
            m_buffer = new StringBuffer();
        }

        public void startDocument() throws SAXException {
            try {
                m_handler.startDocument();
            } catch (HTMLParserException ex) {
                throw new SAXException(ex);
            }
        }

        public void endDocument() throws SAXException {
            try {
                if ( m_buffer.length() > 0 ) {
                    m_handler.text(m_buffer.toString());
                }
                m_buffer = new StringBuffer();
                m_handler.endDocument();
            } catch (HTMLParserException ex) {
                throw new SAXException(ex);
            }
        }

        public void characters(char[] ch, int start, int len) {
            for (int i = 0; i < len; i++) {
                m_buffer.append(ch[start + i]);
            }
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes)
            throws SAXException {

            if ( ROOT_TAG.equals(qName) ) {
                return;
            }

            if ( !m_parser.isRegistered(qName) ) {
                m_buffer.append("<").append(qName);
                for (int ii=0; ii<attributes.getLength(); ii++) {
                    m_buffer.append(" ").append(attributes.getQName(ii));
                    m_buffer.append("=\"");
                    m_buffer.append(attributes.getValue(ii)).append("\"");
                }

                if ( s_emptyTags.contains(qName.toLowerCase()) ) {
                    m_buffer.append("/");
                }
                m_buffer.append(">");
                return;
            }

            try {
                m_handler.text(m_buffer.toString());
                m_buffer = new StringBuffer();
                m_handler.startElement(qName, new AttributeMapImpl(attributes));
            } catch (HTMLParserException ex) {
                throw new SAXException(ex);
            }
        }

        public void endElement(String uri, String localName, String qName)
            throws SAXException {

            if ( ROOT_TAG.equals(qName) ) {
                return;
            }
            
            if ( !m_parser.isRegistered(qName) ) {
                if ( !s_emptyTags.contains(qName.toLowerCase()) ) {
                    // FIXME: Although we know that this element was not
                    // supposed to have any children, we don't currently check
                    // whether it, in fact did or not.
                    m_buffer.append("</").append(qName).append(">");
                }
                return;
            }

            try {
                if ( m_buffer.length() > 0 ) {
                    m_handler.text(m_buffer.toString());
                    m_buffer = new StringBuffer();
                }
                m_handler.endElement(qName);
            } catch (HTMLParserException ex) {
                throw new SAXException(ex);
            }
        }
    }
}

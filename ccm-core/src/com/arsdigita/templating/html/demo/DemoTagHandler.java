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
package com.arsdigita.templating.html.demo;

import com.arsdigita.templating.html.AttributeMap;
import com.arsdigita.templating.html.ContentHandler;
import com.arsdigita.templating.html.HTMLParser;
import com.arsdigita.templating.html.HTMLParserConfigurationException;
import com.arsdigita.templating.html.HTMLParserException;
import com.arsdigita.templating.html.HTMLParserFactory;
import com.arsdigita.templating.html.StringTemplate;
import com.arsdigita.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * Handles two custom tags: <code>&lt;toc/></code> and
 * <code>&lt;footnote></code>. See {@link com.arsdigita.templating.html.demo}
 * for details.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-21
 * @version $Date: 2004/08/16 $
 * @see com.arsdigita.templating.html.demo
 **/
public class DemoTagHandler implements ContentHandler {

    private static final Logger s_log = Logger.getLogger(DemoTagHandler.class);

    private static final String TOC_TAG = "toc";
    private static final String FOOTNOTE_TAG   = "footnote";
    private static final String H0_TAG         = "h0"; // don't puzzle over this
    private static final String H1_TAG         = "h1";
    private static final String H2_TAG         = "h2";
    private static final String H3_TAG         = "h3";

    private static final String HEADER_PREFIX      = "h";

    private StringBuffer m_buffer;
    private List m_headers;
    private FootnoteTagHandler m_footnoteTagHandler;
    private Map m_handlers;
    private Stack m_stack;
    private boolean m_inFootnote;

    private int m_tocPosition;

    public DemoTagHandler() {
        m_buffer    = new StringBuffer();
        m_headers = new ArrayList();
        m_stack = new Stack();
        m_tocPosition = -1;

        m_handlers  = new HashMap();
        m_handlers.put(TOC_TAG, new TocTagHandler());
        m_footnoteTagHandler =         new FootnoteTagHandler();
        m_handlers.put(FOOTNOTE_TAG,   m_footnoteTagHandler);
        HeaderTagHandler headerTagHandler = new HeaderTagHandler();
        m_handlers.put(H1_TAG,         headerTagHandler);
        m_handlers.put(H2_TAG,         headerTagHandler);
        m_handlers.put(H3_TAG,         headerTagHandler);
    }

    public void startDocument() {}

    public void endDocument() throws HTMLParserException {
        m_footnoteTagHandler.generateFootnotes(m_buffer);
        generateToc();
    }

    /**
     * Inserts a table of contents where the TOC tag was found, if any.
     **/
    private void generateToc() throws HTMLParserException {
        if ( m_tocPosition < 0 ) {
            return;
        }

        Iterator headers = m_headers.iterator();
        if ( !headers.hasNext() ) {
            return;
        }

        StringBuffer toc = new StringBuffer();
        HeaderTag lastTag = new HeaderTag(H0_TAG);
        while ( headers.hasNext() ) {
            HeaderTag tag = (HeaderTag) headers.next();
            StringTemplate template =
                new StringTemplate("<a href=\"#$\">$</a>");
            template.bind(tag.getAnchor());
            template.bind(tag.getText());

            int distance = lastTag.distanceTo(tag);
            if ( distance < 0 ) {
                // this is a case of h1 following h3
                //   <h3>Section 1.2</h3>
                // <h1>Section 2</h1>
                // So, we must close all the heretofore unclosed <ol> tags.
                for (int i=0; i < -distance; i++) {
                    toc.append("</li>\n</ol>\n");
                }
                toc.append("\t<li>");
            } else if ( distance == 0 ) {
                // this is a case of h2 following h2. no need to close or open
                // the <ol> tag.
                toc.append("</li>\n\t<li>");
            } else {
                if ( distance > 1 ) {
                    throw new HTMLParserException
                        (tag.toString() + " does not have a preceding h" +
                         (tag.getLevel() - 1) + " tag");
                }
                // this is a case of h2 following h1
                // <h1>Section 2</h1>
                //   <h2>Section 1.2</h2>
                // So, we must open a new <ol> tag.
                StringTemplate ol = new StringTemplate("\n<ol type=\"$\">\n\t<li>");
                ol.bind(tag.getType());
                toc.append(ol);
            }
            toc.append(template);
            lastTag = tag;
        }
        // close all the unclosed <ol> tags.
        for (int i=0; i < lastTag.getLevel(); i++) {
            toc.append("</li>\n</ol>\n");
        }
        m_buffer = m_buffer.replace(m_tocPosition, m_tocPosition,
                                    toc.toString());
    }

    public void text(String text) throws HTMLParserException {
        if ( m_stack.empty() ) {
            m_buffer.append(text);
            return;
        }

        TagHandler handler = (TagHandler) m_stack.peek();
        handler.text(text);
    }

    public boolean isValid(String qName) {
        Assert.exists(qName, "element");
        return m_handlers.containsKey(qName);
    }

    public void startElement(String qName, AttributeMap attrs)
        throws HTMLParserException {

        if ( !isValid(qName) ) {
            throw new HTMLParserException(qName + " is not a valid tag.");
        }

        TagHandler handler = (TagHandler) m_handlers.get(qName);
        handler.startElement(qName, attrs);
        m_stack.push(handler);
        if ( FOOTNOTE_TAG.equals(qName) ) {
            if ( m_inFootnote ) {
                throw new HTMLParserException
                    ("nested " + FOOTNOTE_TAG + " tags are not allowed.");
            }
            m_inFootnote = true;
        }
    }

    public void endElement(String qName) throws HTMLParserException {
        if ( !isValid(qName) ) {
            throw new HTMLParserException(qName + " is not a valid tag.");
        }
        if ( FOOTNOTE_TAG.equals(qName) ) {
            m_inFootnote = false;
        }

        if ( m_stack.empty() ) {
            throw new HTMLParserException
                ("no matching opening tag for " + qName);
        }
        ((TagHandler) m_stack.pop()).endElement(qName);
    }

    public String getExtrapolatedDocument() {
        s_log.info("extrapolated:\n'" + m_buffer.toString() + "'");
        return m_buffer.toString();
    }

    public static String getExtrapolatedDocument(String textWithCustomMarkup)
        throws HTMLParserException {

        HTMLParser parser = null;
        try {
            parser = HTMLParserFactory.newInstance();
        } catch (HTMLParserConfigurationException ex) {
            throw new HTMLParserException(ex);
        }
        parser.registerTag(TOC_TAG);
        parser.registerTag(FOOTNOTE_TAG);
        parser.registerTag(H1_TAG);
        parser.registerTag(H2_TAG);
        parser.registerTag(H3_TAG);
        DemoTagHandler handler = new DemoTagHandler();
        parser.parse(textWithCustomMarkup, handler);
        return handler.getExtrapolatedDocument();
    }

    private interface TagHandler {
        void startElement(String qName, AttributeMap attrs)
            throws HTMLParserException;

        void endElement(String qName) throws HTMLParserException;

        void text(String text) throws HTMLParserException;
    }

    private StringBuffer getCurrentBuffer() {
        return m_inFootnote ? m_footnoteTagHandler.getBuffer() : m_buffer;
    }


    private class TocTagHandler implements TagHandler {
        public void startElement(String qName, AttributeMap attrs)
            throws HTMLParserException {

            if ( m_tocPosition > -1 ) {
                throw new HTMLParserException
                    ("There can be only one " + TOC_TAG);
            }
            if ( getCurrentBuffer() != m_buffer ) {
                throw new HTMLParserException
                    ("Improperly nested " + TOC_TAG);
            }
            m_tocPosition = m_buffer.length();
        }

        public void text(String text) throws HTMLParserException {
            throw new HTMLParserException
                (TOC_TAG + " should be an empty tag.");
        }

        public void endElement(String qName) {}
    }

    private class FootnoteTagHandler implements TagHandler {
        StringBuffer m_footnoteBuffer;
        List m_footnotes;

        public FootnoteTagHandler() {
            m_footnoteBuffer = new StringBuffer();
            m_footnotes = new ArrayList();
        }

        public void startElement(String qName, AttributeMap attrs) {
            StringTemplate template = new StringTemplate
                ("<a href='#$'><sup id='$'>$</sup></a>");
            String footnoteAnchor = getAnchor(m_footnotes.size()+1);
            template.bind(footnoteAnchor);
            template.bind(getBackAnchor(footnoteAnchor));
            template.bind(m_footnotes.size()+1);
            m_buffer.append(template);
        }

        public void text(String text) {
            m_footnoteBuffer.append(text);
        }

        public void endElement(String qName) {
            m_footnotes.add(m_footnoteBuffer.toString());
            m_footnoteBuffer = new StringBuffer();
        }

        public StringBuffer getBuffer() {
            return m_footnoteBuffer;
        }

        private String getAnchor(int footnoteNumber) {
            StringBuffer anchor = new StringBuffer(10);
            anchor.append("fn").append(footnoteNumber);
            return anchor.toString();
        }

        private String getBackAnchor(String anchor) {
            return "b" + anchor;
        }

        private void generateFootnotes(StringBuffer buffer) {

            if ( m_footnotes.size() == 0 ) {
                return;
            }
            buffer.append("<hr size='1' noshade />\n<ol>\n");

            int count = 1;
            for (Iterator i= m_footnotes.iterator(); i.hasNext(); ) {
                StringTemplate template = new StringTemplate
                    ("\t<li><p id='$'>$<a href='#$'><sup>back</sup></a></p></li>\n");
                String footnoteAnchor = getAnchor(count++);
                template.bind(footnoteAnchor);
                String footnote = (String) i.next();
                template.bind(footnote);
                String backAnchor = getBackAnchor(footnoteAnchor);
                template.bind(backAnchor);
                buffer.append(template);
            }
            buffer.append("</ol>\n");
        }
    }

    /**
     *  A helper class for storing tags in the m_headers list.
     **/
    private static class HeaderTag {
        public final static String TYPE_I = "I";
        private String m_tagName;
        private String m_text;
        private String m_anchor;
        private int m_level;

        public HeaderTag(String tagName) {
            Assert.exists(tagName, "tagName");
            m_tagName = tagName.toLowerCase();
            Assert.isTrue
                (H0_TAG.equals(tagName) || H1_TAG.equals(tagName)
                 || H2_TAG.equals(tagName) || H3_TAG.equals(tagName),
                 tagName + " is not a supported header tag.");
            m_level = Integer.parseInt(m_tagName.substring(1));
        }

        /**
         * Examples: (1) The distance from h1 to h3 is 2. The distance from h2
         * to h1 is -1.
         **/
        public int distanceTo(HeaderTag tag) {
            Assert.exists(tag, "tag");
            return tag.m_level - m_level;
        }

        public int getLevel() {
            return m_level;
        }

        public String getName() {
            return m_tagName;
        }

        public void setText(String text) {
            m_text = text;
        }

        public String getText() {
            return m_text;
        }

        public void setAnchor(String anchor) {
            m_anchor = anchor;
        }

        public String getAnchor() {
            return m_anchor;
        }

        public String getType() {
            int type = getLevel() % 3;
            if ( type == 1 ) {
                return TYPE_I;
            } else if ( type == 2 ) {
                return "A";
            } else {
                return "1";
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("<").append(getName()).append(">").append(getText());
            sb.append("</").append(getName()).append(">");
            return sb.toString();
        }
    }

    /**
     * Handles h1, h2, and h3 for generating a table of contents
     **/
    private class HeaderTagHandler implements TagHandler {
        private HeaderTag m_currentHeader;

        public void startElement(String qName, AttributeMap attrs)
            throws HTMLParserException {

            if ( m_currentHeader != null ) {
                throw new HTMLParserException
                    ("Header tags like " + qName + " cannot be nested.");
            }
            m_currentHeader = new HeaderTag(qName);

            StringTemplate template = new StringTemplate("<$ id='$'>");
            String headerAnchor = HEADER_PREFIX + (m_headers.size()+1);
            m_currentHeader.setAnchor(headerAnchor);
            template.bind(qName);
            template.bind(headerAnchor);
            getCurrentBuffer().append(template);
        }

        public void text(String text) {
            m_currentHeader.setText(text);
            getCurrentBuffer().append(text);
        }

        public void endElement(String qName) throws HTMLParserException {
            if ( m_currentHeader.getText() == null || m_currentHeader.getText().equals("") ) {
                throw new HTMLParserException
                    ("Cannot have an empty " + qName + " tag.");
            }
            getCurrentBuffer().append("</").append(qName).append(">");
            m_headers.add(m_currentHeader);
            m_currentHeader = null;
        }
    }
}

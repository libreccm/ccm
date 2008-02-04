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

import com.arsdigita.templating.html.AttributeMap;

/**
 * Receives notification of the logical content of a document.
 *
 * <p>This is similar to the <a
 * href="http://java.sun.com/xml/jaxp/dist/1.1/docs/api/org/xml/sax/ContentHandler.html"><i>ContentHandler</i></a>
 * interface in SAX. The difference is that this class expects the invoking
 * {@link HTMLParser} to only send notifications of those elements that have
 * been registered with the parser. In other words, this handler would most
 * likely be only interested in handling special tags like the
 * <code>&lt;footnote></code> tag (see {@link HTMLParser}). In a typical use
 * case, regular HTML markup like <code>&lt;p></code> or <code>&lt;b></code>
 * would not generate the <code>startElement(String, AttributeMap)</code> and 
 * <code>endElement(String, AttributeMap)</code> events.  </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-21
 * @version $Id: ContentHandler.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public interface ContentHandler {

    /**
     * Receives notification of the beginning of a document.
     **/
    void startDocument() throws HTMLParserException;

    /**
     * Receives notification of the end of a document.
     **/
    void endDocument() throws HTMLParserException;

    /**
     * Receives notification of textual data.
     **/
    void text(String text) throws HTMLParserException;

    /**
     * Receives notification of the beginning of an element. The parser will
     * only notify the handler of those elements for which {@link
     * HTMLParser#isRegistered(String)} is true. Any other HTML markup will be
     * treated as text and will be dispatched to {@link #text(String)}.
     **/
    void startElement(String qName, AttributeMap attributes)
        throws HTMLParserException;

    /**
     * Receives notification of the end of an element. The parser will only
     * notify the handler of those elements for which {@link
     * HTMLParser#isRegistered(String)} is true. Any other HTML markup will be
     * treated as text and will be dispatched to {@link #text(String)}.
     **/
    void endElement(String qName) throws HTMLParserException;
}

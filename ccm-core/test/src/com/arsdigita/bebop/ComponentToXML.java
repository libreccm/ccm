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
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.xml.Document;
import java.util.StringTokenizer;
import javax.servlet.ServletException;

/**
 *  This utility class takes a Bebop Component and generates the XML for
 *  that component type. If the type is a general component, it will
 *  be inserted into a Page object for XML generation.
 *return p
 *  If the Component is of type Page, it will not be further nested.
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class ComponentToXML {


    /**
     *  This method places the given component into a page, generates XML, and returns
     *  a <code>String</code> representation of the XML
     *
     *  @return The XML document as a String
     */
    static public String getXMLString( Component c ) throws ServletException
    {
        return docToString( getDocument(c) );
    }

    /**
     *  This method generates XML for the page, and returns
     *  a <code>String</code> representation of the XML
     *
     *  @return The XML document as a String
     *
     *  @pre p.isLocked()
     */
    static public String getXMLString( Page p ) throws ServletException
    {
        return docToString( getDocument(p) );
    }

    /**
     *  This method places the given Component into a Page, *
     *  generates XML, and returns the <code>Document</code> *
     *  representation of the XML. If this method is called for a *
     *  Component reference ot a Page type, it will not be inserted *
     *  into a Page.  Assumes debugging.
     *
     *  @return The XML document */
    static public Document getDocument(Component c)
        throws ServletException {
        return getDocument(c, true);
    }

    /**
     * Generate XML from a Component.  Like
     * {@link#getDocument(Component)}, but it allows specifying wheter
     * debug mode is enabled */
    static public Document getDocument(Component c, boolean isDebug)
        throws ServletException {

        // Just in case...
        if( c instanceof Page ) {
            return getDocument((Page)c, isDebug);
        }

        Page page = new Page();
        page.add( c );
        page.lock();

        return getDocument(page, isDebug);
    }

    /**
     *  This method generates XML for the page, and returns
     *  the <code>Document</code> representation of the XML
     *
     *  @return The XML document
     *
     *  @pre p.isLocked()
     */
    static public Document getDocument(Page p, boolean isDebug)
        throws ServletException {
        Assert.assertTrue( p.isLocked() );
        HttpServletDummyRequest request = new HttpServletDummyRequest(isDebug);
        HttpServletDummyResponse response = new HttpServletDummyResponse();

        return p.buildDocument( request, response );
    }

    /**
     *  Function converts the Document object into a String.
     *
     *  As a quick & dirty aid to human reading, it tokenizes the
     *  document based on the > end of XML tags, inserting a
     *  newline after each. This will make any errors discovered
     *  in regression testing easier to diagnose.
     *
     *  Could probably do something more clever here involving
     *  parsing the DOM tree.
     *
     *  @param doc The XML document to convert to a readable string sequence.
     *
     * @return The XML document as a String.  */
    private static String docToString(Document doc)
    {
        String docStr = doc.toString();
        StringTokenizer st = new StringTokenizer(docStr, ">");
        StringBuffer buf = new StringBuffer(docStr.length());
        while(st.hasMoreTokens())
            {
                String token = st.nextToken() + ">\n";
                buf.append( token );
            }
        return buf.toString();

    }

}

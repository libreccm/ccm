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

import com.arsdigita.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * A utility class to make it easier to read Java code that generates HTML.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2002-08-26
 * @version $Id: StringTemplate.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class StringTemplate {

    private static final Logger s_log = Logger.getLogger(StringTemplate.class);

    List m_fragments;
    List m_bindVars;
    int  m_nUnboundVars;

    /**
     * Constructs a template.
     *
     * @param htmlFragment a string like <code>"&lt;h1>Java is $ times better
     * than Perl.&lt;/h1>"</code>, where the $ sign is a place holder for a
     * variable that can be bound via the {@link #bind(Object)} method.
     *
     * <p><span style="color: FireBrick;">Known bug</span>: a run of multiple
     * dollar signs as in <code>"foo$$$bar"</code> is treated as a single
     * placeholder, i.e., it is equivalent to <code>"foo$bar"</code>. </p>
     **/
    public StringTemplate(String htmlFragment) {
        Assert.assertNotNull(htmlFragment, "htmlFragment");
        
        m_fragments = new ArrayList();
        m_bindVars  = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(htmlFragment, "$", false);
        if ( tokenizer.hasMoreTokens() ) {
            while (tokenizer.hasMoreTokens()) {
                m_fragments.add(tokenizer.nextToken());
            }
            m_nUnboundVars = m_fragments.size() - 1;
        } else {
            m_fragments.add(htmlFragment);
        }
    }

    /**
     * Binds the next available placeholder that has not been bound yet.
     *
     * <p>To continue the example introduced in the {@link #StringTemplate(String)
     * constructor}, after doing something like this </p>
     *
     * <pre>
     *   StringTemplate html = new StringTemplate("&lt;h1>Java is $ times better than Perl&lt;/h1>");
     *   html.bind("5");
     * </pre>
     *
     * <p><code>html.toString()</code> will evaluate to <code>"&lt;h1>Java is 5
     * times better than Perl&lt;/h1>"</code>.  </p>
     **/
    public void bind(String value) {
        if ( m_nUnboundVars == 0 ) {
            final String msg =
                "Programmer's error. All vars have been bound already. ";
            s_log.error(msg + m_fragments);
            throw new IllegalStateException(msg);
        }
        m_bindVars.add(value);
        m_nUnboundVars--;
    }

    public void bind(Object object) {
        bind(object == null ? "null" : object.toString());
    }

    /**
     * A conveniece method for binding <code>int</code> values.
     *
     * @see #bind(String)
     **/
    public void bind(int i) {
        bind(Integer.toString(i));
    }

    public String toString() {
        if ( m_nUnboundVars > 0 ) {
            final String msg =
                "Programmer's error. Not all vars have been bound yet. ";
            s_log.error(msg + m_fragments);
            throw new IllegalStateException(msg);
        }
        if ( m_fragments.size() == 1 ) {
            return (String) m_fragments.get(0);
        }

        Iterator fragments = m_fragments.iterator();
        Iterator bindVars =  m_bindVars.iterator();
        StringBuffer sb = new StringBuffer((String) fragments.next());
        while ( bindVars.hasNext() ) {
            sb.append((String) bindVars.next());
            sb.append((String) fragments.next());
        }

        // don't think we need to cache this in this class.
        return sb.toString();
    }
}

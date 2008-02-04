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
 */

package com.arsdigita.london.util.db;


import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class InvariantHandler extends DefaultHandler {

    private static final Logger s_log =
        Logger.getLogger(InvariantHandler.class);

    private static final String XMLNS =
        "http://xmlns.redhat.com/waf/london/invariants/1.0";


    private List m_invariants;
    private StringBuffer m_buf;
    private String m_description;
    private String m_query;
    private String m_expected;

    public Iterator getInvariants() {
        return m_invariants.iterator();
    }

    public void characters(char[] ch, int start, int len) {
        if (m_buf != null) {
            m_buf.append(ch, start, len);
        }
    }

    public void startElement(String uri, String localName, String qn,
                             Attributes attrs) {
        if (localName.equals("invariants")) {
            m_invariants = new ArrayList();
        } else if (localName.equals("invariant")) {
            m_buf = null;
            m_query = null;
            m_expected = "1";
            m_description = attrs.getValue("description");
        } else if (localName.equals("query")) {
            m_buf = new StringBuffer();
            if (attrs.getValue("expected") != null) {
                m_expected = attrs.getValue("expected");
            }
        }
    }
    
    public void endElement(String uri, String localName, String qn) {
        if (localName.equals("invariant")) {
            m_invariants.add(InvariantFactory.getInstance()
                             .createInvariant(m_description,
                                              m_query,
                                              m_expected));
        } else if (localName.equals("query")) {
            m_query = m_buf.toString();
        }
    }

}


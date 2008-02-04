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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

/**
 * An implementation of the {@link AttributeMap} interface.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2002-08-29
 * @version $Id: AttributeMapImpl.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public final class AttributeMapImpl implements AttributeMap {

    private Map m_map;

    public AttributeMapImpl() {
        m_map = new HashMap();
    }

    public AttributeMapImpl(Attributes attrs) {
        this();
        for (int i=0; i<attrs.getLength(); i++) {
            putValue(attrs.getQName(i), attrs.getValue(i));
        }
    }

    public boolean contains(String qName) {
        return m_map.containsKey(qName);
    }

    public String getValue(String qName) {
        return (String) m_map.get(qName);
    }

    public void putValue(String qName, String value) {
        m_map.put(qName, value);
    }

    public int size() {
        return m_map.size();
    }

    public Iterator keys() {
        return m_map.keySet().iterator();
    }

    public Iterator pairs() {
        List list = new ArrayList();
        for (Iterator i=m_map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();

            AttributeMap.Pair pair = new PairImpl((String) entry.getValue(),
                                                  (String) entry.getKey());
            list.add(pair);
        }
        return list.iterator();
    }
}

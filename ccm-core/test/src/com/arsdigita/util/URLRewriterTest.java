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
package com.arsdigita.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;

public class URLRewriterTest extends TestCase {

    public static final String versionId = "$Id: URLRewriterTest.java 747 2005-09-02 11:02:24Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public URLRewriterTest(String s) {
        super(s);
    }

    public void setUp() {
        URLRewriter.addParameterProvider(new SampleParameterProvider1());
        URLRewriter.addParameterProvider(new SampleParameterProvider2());
    }

    public void tearDown() {
        URLRewriter.clearParameterProviders();
    }

    public void testGetGlobalParams() {
        HttpServletDummyRequest req = new HttpServletDummyRequest("localhost","", "/foo", "/bar", null);
        new RequestEnvironment(req, new HttpServletDummyResponse());
        
        HashSet set = new HashSet();
        set.add("param1");
        set.add("param2");
        req.setParameterValues("x", "xvalue");
        req.setParameterValues("y", "32");
        Set rs = URLRewriter.getGlobalParams(req);
        Iterator iter = rs.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            set.remove(entry.getKey());
        }
        assertTrue(set.isEmpty());
    }

    public void testEncodeURL() {
        HttpServletDummyRequest req = new HttpServletDummyRequest("localhost","", "/foo", "/bar", null);
        HttpServletDummyResponse resp = new HttpServletDummyResponse();
//        req.setURL("/foo/bar");
        req.setParameterValues("x", "xvalue");
        req.setParameterValues("y", "32");
        String encoded = URLRewriter.encodeURL(req, resp,
                                               "/baz/quux?y=32&z=z%20value");
        assertTrue(encoded.startsWith("/baz/quux?"));
        assertTrue(encoded.indexOf("param2=param2value") > 0);
        assertTrue(encoded.indexOf("param1=param1value") > 0);
        assertTrue(encoded.indexOf("y=32") > 0);
        assertTrue(encoded.indexOf("z=z+value") > 0 ||
               encoded.indexOf("z=z%20value") > 0);

        encoded = URLRewriter.encodeRedirectURL(req, resp,
                                                "/baz/quux?y=32&z=z%20value");
        assertTrue(encoded.startsWith("/baz/quux?"));
        assertTrue(encoded.indexOf("param2=param2value") > 0);
        assertTrue(encoded.indexOf("param1=param1value") > 0);
        assertTrue(encoded.indexOf("y=32") > 0);
        assertTrue(encoded.indexOf("z=z+value") > 0 ||
               encoded.indexOf("z=z%20value") > 0);
    }


    private class SampleParameterProvider1 implements ParameterProvider {
        // we can't test models without a dependency on Bebop
        public Set getModels() {
            return java.util.Collections.EMPTY_SET;
        }

        public Set getParams(HttpServletRequest req) {
            HashSet set = new HashSet();
            set.add(new MapEntry("param1", "param1value"));
            return set;
        }
    }

    private class SampleParameterProvider2 implements ParameterProvider {
        // we can't test models without a dependency on Bebop
        public Set getModels() {
            return java.util.Collections.EMPTY_SET;
        }

        public Set getParams(HttpServletRequest req) {
            HashSet set = new HashSet();
            set.add(new MapEntry("param2", "param2value"));
            return set;
        }
    }

    private static class MapEntry implements Map.Entry {
        private Object m_key;
        private Object m_value;

        public MapEntry(Object key, Object value) {
            m_key = key;
            m_value = value;
        }

        public Object getKey() {
            return m_key;
        }

        public Object getValue() {
            return m_value;
        }

        public Object setValue(Object o) {
            Object old = m_value;
            m_value = o;
            return old;
        }
    }
}

/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.web.PathMapCache.LongestMatch;
import com.arsdigita.web.PathMapCache.LongestMatchException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import junit.framework.TestCase;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2004-01-14
 * @version $Revision: #4 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public class PathMapCacheTest extends TestCase {

    public PathMapCacheTest(String name) {
        super(name);
    }

    public void testLongestMatch() {
        try {
            new LongestMatch(null);
            fail("should've choked on null");
        } catch (NullPointerException ex) {
            ;
        }

        try {
            new LongestMatch("/foo");
            new LongestMatch("foo/");
            fail("should've complained about a missing slash");
        } catch (LongestMatchException ex) {
            ;
        }

        final String path = "/foo/bar/baz//quux/";
        List elems = Arrays.asList
            (new String[] {path, "/foo/bar/baz//", "/foo/bar/baz/",
                           "/foo/bar/", "/foo/", "/"});
        testLongestMatch(path, elems);

        final String path2 = "/content/";
        testLongestMatch(path2, Arrays.asList(new String[] {path2, "/"}));

        testLongestMatch("/", Arrays.asList(new String[] {"/"}));
    }

    private void testLongestMatch(String path, List expected) {
        Iterator actual = new LongestMatch(path);

        for (Iterator elems=expected.iterator(); elems.hasNext(); ) {
            assertTrue("has next", actual.hasNext());
            assertEquals((String) elems.next(), (String) actual.next());
        }
        assertFalse("has next", actual.hasNext());
        try {
            actual.next();
            fail("should have thrown NoSuchElementException");
        } catch (NoSuchElementException ex) {
            ;
        }
    }
}

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
package com.arsdigita.globalization;

import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MixedResourceBundleTest extends BaseTestCase {


    public MixedResourceBundleTest(String name) {
        super(name);
    }

    private int countEnumeration(Enumeration e) {
        int count = 0;
        while (e.hasMoreElements()) {
            count++;
            e.nextElement();
        }
        return count;
    }

    private void verify(ResourceBundle rb,
                        String[][] pairs) throws Exception
    {
        assertEquals("bundle size", pairs.length,
                     countEnumeration(rb.getKeys()));

        for (int i = 0; i < pairs.length; i++) {
            assertEquals("value for " + pairs[i][0],
                         pairs[i][1], rb.getString(pairs[i][0]));
        }
    }

    private ResourceBundle getBundle(String number,
                                     String language, String country)
    {
        return ResourceBundle.getBundle(
                                        "com.arsdigita.globalization.MRBundle" + number,
                                        new Locale(language, country));
    }

    public void test0() throws Exception {
        try {
            getBundle("0", "", "");
            fail("found non-existent MRBundle0");
        } catch (MissingResourceException e) {
        }
    }

    public void test1() throws Exception {
        verify(
               getBundle("1", "", ""),
               new String[][] { }
               );
    }
}

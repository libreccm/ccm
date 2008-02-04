/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-11-18
 * @version $Revision: #4 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public class EncodingTest extends TestCase {

    private static final Logger s_log = Logger.getLogger(EncodingTest.class);
    private static final Encoding E = null;

    private static final String LATIN1   = "ISO-8859-1";
    private static final String UTF16    = "UTF-16";
    private static final String UTF16B   = "UTF-16BE";
    private static final String UTF16L   = "UTF-16LE";
    private static final String UTF8     = "UTF-8";

    private static final char CAFE = '\ucafe';

    public EncodingTest(String name) {
        super(name);
    }

    public void testToHex() {
        assertEquals("0x0C03", E.toHex('\u0c03'));
        assertEquals("0x0000", E.toHex((char) 0));
        assertEquals("0x00FF", E.toHex((char) 255));
        assertEquals("0xCAFE", E.toHex(CAFE));
        assertEquals("0xFFFF", E.toHex(Character.MAX_VALUE));

        assertEquals("0x00", E.toHex((byte) 0x00));
        assertEquals("0x7F", E.toHex((byte) 0x7F));
        assertEquals("0xFE", E.toHex((byte) 0xFE));
    }

    /**
     * http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc
     **/
    public void testIsSupportedEncoding() {
        String[] encs = new String[]
            {"US-ASCII", LATIN1, UTF8, UTF16B, UTF16L, UTF16};

        for (int ii=0; ii<encs.length; ii++) {
            assertTrue(encs[ii], E.isSupportedEncoding(encs[ii]));
        }
    }

    public void testGetBytes() {
        try {
            E.getBytes(CAFE, "CAFE");
            fail("Since when is CAFE a supported encoding?");
        } catch (Encoding.UnsupportedException ex) {
            ;
        }

        assertEqualArrays(new byte[] {(byte) 0x7D},
                          E.getBytes('\u007D', LATIN1));
        assertEqualArrays(new byte[] {(byte) 0xC2, (byte) 0xBF},
                          E.getBytes('\u00bf', UTF8));
        assertEqualArrays(new byte[] {(byte) 0xCA, (byte) 0xFE},
                          E.getBytes(CAFE, UTF16B));
        assertEqualArrays(new byte[] {(byte) 0xFE, (byte) 0xCA},
                          E.getBytes(CAFE, UTF16L));
        assertEquals("No BOM please", 2, E.getBytes(CAFE, UTF16).length);
    }

    private void assertEqualArrays(byte[] a1, byte[] a2) {
        assertEquals("same length", a1.length, a2.length);
        for (int ii=0; ii<a1.length; ii++) {
            assertEquals("same element", a1[ii], a2[ii]);
        }
    }
}

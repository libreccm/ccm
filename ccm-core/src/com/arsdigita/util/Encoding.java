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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * A collection of utility methods for manipulating character and byte data.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-11-15
 * @version $Revision: #4 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public final class Encoding {
    private final static String PADDING = "0x";
    private final static int    PAD_LEN = PADDING.length();

    private final static ByteArrayInputStream BAIS =
        new ByteArrayInputStream(new byte[] {(byte) 0});

    private Encoding() {}

    /**
     * Returns the hexadecimal representation of the character.
     *
     * <p>For example, <code>toHex('\u0c03')</code> is <code>"0x0C03"</code> and
     * <code>toHex('A')</code> is <code>"0x0041"</code>.</p>
     **/
    public static String toHex(char ch) {
        return pad(toHex((int) ch), 4);
    }

    /**
     * Returns the hexadecimal representation of the byte, with the return value
     * ranging from "0x00" to "0xFF".
     *
     * <p>For example, <code>toHex((byte) 126)</code> is <code>"0x7E"</code> and
     * <code>toHex((byte) -2)</code> is <code>"0xFE"</code>.</p>
     **/
    public static String toHex(byte b) {
        int num = b>=0 ? b : b + 256;
        return pad(toHex(num), 2);
    }

    /**
     * Returns <code>true</code> if the character encoding <code>enc</code> is
     * supported by the host JVM.</p>
     **/
    public static boolean isSupportedEncoding(String enc) {
        try {
            new InputStreamReader(BAIS, enc);
            return true;
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }

    /**
     * Returns the byte representation of the character <code>ch</code> in the
     * specified encoding <code>enc</code>.  If the specified encoding is
     * "UTF-16", the result is a platform-dependent two-byte sequence, with the
     * byte-order mark (BOM) stripped out.
     *
     * @throws Encoding.UnsupportedException
     **/
    public static byte[] getBytes(char ch, String enc) {
        if ( enc == null ) {
            throw new NullPointerException("enc");
        }

        try {
            byte[] result = String.valueOf(ch).getBytes(enc);
            if ( "UTF-16".equals(enc.toUpperCase()) ||
                 "UTF16".equals(enc.toUpperCase()) ) {
                return subarray(result, 2);
            }
            return result;
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedException(enc, ex);
        }
    }

    private static byte[] subarray(byte[] array, int start) {
        if ( start < 0 || start >= array.length ) {
            throw new IllegalArgumentException
                ("array.length=" + array.length + "; start=" + start);
        }
        byte[] result = new byte[array.length - start];
        for (int ii=0; ii<array.length-start; ii++) {
            result[ii] = array[start+ii];
        }
        return result;
    }

    private static String toHex(int num) {
        return Integer.toHexString(num).toUpperCase();
    }

    private static String pad(String hex, int width) {
        final int bufLen = width + PAD_LEN;
        StringBuffer result = new StringBuffer(bufLen);
        result.append(PADDING);

        final int len = hex.length();
        if (len >= width) {
            return result.append(hex).toString();
        }

        result.setLength(PAD_LEN + width - len);
        for (int ii=PAD_LEN; ii<PAD_LEN+width-len; ii++) {
            result.setCharAt(ii, '0');
        }
        result.append(hex);
        return result.toString();
    }

    /**
     * This is an unchecked exception.
     **/
    public static class UnsupportedException extends UncheckedWrapperException {
        public UnsupportedException(String msg) {
            super(msg);
        }

        public UnsupportedException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public UnsupportedException(Throwable cause) {
            super(cause);
        }
    }
}

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

import java.util.Locale;

/**
 * <p>
 * Represents one of possibly many of the values of the Accept-Charset HTTP
 * header.
 * </p>
 *
 * @version $Id: AcceptCharset.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class AcceptCharset extends AcceptField {

    private final static String DEFAULT_CHARSET = "ISO-8859-1";

    private String m_charset = "";

    /**
     * <p>
     * Constructor.
     * </p>
     *
     * @param acceptCharset String representing one entry from the
     *        Accept-Charset HTTP headers. This can look like: "en" or "en; q=0.75" or "*"
     */
    public AcceptCharset(String acceptCharset) {
        if (acceptCharset == null) {
            throw new NullPointerException("acceptCharset cannot be null");
        }

        int semi = acceptCharset.indexOf(';');

        if (semi == -1) {
            setCharset(acceptCharset);
            setQValue(DEFAULT_Q_VALUE);
        } else {
            setCharset(acceptCharset.substring(0, semi));
            setQValue(acceptCharset.substring(semi + 1));
        }
    }

    public final String getCharset() {
        return m_charset;
    }

    private void setCharset(String charset) {
        charset = charset.trim().toUpperCase(Locale.ENGLISH);

        if (charset.equals("*")) {
            m_charset = DEFAULT_CHARSET;
        } else {
            m_charset = charset;
        }
    }
}

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
 * Represents one of possibly many of the values of the Accept-Language HTTP
 * headers.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class AcceptLanguage extends AcceptField {
    public final static String versionId = "$Id: AcceptLanguage.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_language = "";
    private Locale m_locale = null;

    /**
     * <p>
     * Constructor.
     * </p>
     *
     * @param acceptLanguage String representing one entry from the
     *        Accept-Language HTTP headers. This can look like: "en" or "en;
     *        q=0.75" or "*"
     */
    public AcceptLanguage(String acceptLanguage) {
        if (acceptLanguage == null) {
            throw new NullPointerException("acceptLanguage cannot be null");
        }

        int semi = acceptLanguage.indexOf(';');

        if (semi == -1) {
            setLanguage(acceptLanguage);
            setQValue(DEFAULT_Q_VALUE);
        } else {
            setLanguage(acceptLanguage.substring(0, semi));
            setQValue(acceptLanguage.substring(semi + 1));
        }

        setLocale();
    }

    public final String getLanguage() {
        return m_language;
    }

    private void setLanguage(String language) {
        Locale dl = Locale.getDefault();

        language = language.trim();

        if (language.equals("*")) {
            m_language =
                dl.getCountry() == null || dl.getCountry().length() == 0 ?
                dl.getLanguage() :
                dl.getLanguage() + "-" + dl.getCountry();
        } else {
            m_language = language;
        }
    }

    public final Locale getLocale() {
        return m_locale;
    }

    private void setLocale() {
        int dash = m_language.indexOf('-');

        if (dash == -1) {
            // no country provided
            m_locale = new Locale(m_language, "");
        } else {
            m_locale = new Locale(
                                  m_language.substring(0, dash),
                                  m_language.substring(dash + 1)
                                  );
        }
    }
}

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
import java.util.ResourceBundle;

/**
 * <p>
 * Provides Locale of the client application.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class ClientLocaleProvider implements LocaleProvider {
    public final static String versionId = "$Id: ClientLocaleProvider.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_targetBundle = "";
    private String m_acceptLanguages = "";

    public Locale getLocale() {
        ResourceBundle b = null;
        Locale l = null;

        if (m_acceptLanguages.length() > 0) {

            AcceptField[] af =
                (new AcceptLanguageHeader(m_acceptLanguages)).getAcceptFields();

            for (int i = 0; i < af.length; i++) {
                l = ((AcceptLanguage) af[i]).getLocale();

                // Get the ResourceBundle for this Locale. Don't let the
                // search fallback to match other languages.
                SystemLocaleProvider slp = (SystemLocaleProvider)
                    LocaleNegotiator.getSystemLocaleProvider();
                Locale defaultLocale = null;
                if (slp != null) {
                    defaultLocale = slp.getLocale();
                }
                if (defaultLocale == null) {
                    defaultLocale = Locale.getDefault();
                }

                b = Globalization.getBundleNoFallback(
                                                      m_targetBundle, l, defaultLocale
                                                      );

                if (b != null) {
                    break;
                }
            }

            if (b == null) {
                l = ((AcceptLanguage) af[0]).getLocale();
            }
        }

        return l;
    }

    public void setTargetBundle(String targetBundle) {
        m_targetBundle =
            (targetBundle != null) ? targetBundle : "";
    }

    public void setAcceptLanguages(String acceptLanguages) {
        m_acceptLanguages =
            (acceptLanguages != null) ? acceptLanguages : "";
    }
}

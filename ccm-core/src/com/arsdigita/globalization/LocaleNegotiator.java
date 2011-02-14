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
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

/**
 * <p>
 * Adapted from com.oreilly.servlet.LocaleNegotiator. Determines the
 * appropriate Locale, Charset, and ResourceBundle for a request.
 * </p>
 * <p>
 * The Locale for a request is determined in one of four ways in order of
 * precedence:
 * <ol>
 *   <li>Locale associated with the current PackageInstance.</li>
 *   <li>User's preferred Locale as determined by the ACS.</li>
 *   <li>
 *     <p>
 *       User's preferred Locale as determined by the Accept-Language header.
 *       For a definition of the format for the Accept-Language HTTP header
 *       look at RFC 2616.
 *     </p>
 *     <p>
 *       We handle quality values (or q values) for the Accept-Language HTTP
 *       header. This is how we interpret q-values:
 *       <ul>
 *         <li>
 *           Languages in the Accept-Language header are sorted by q-value.
 *         </li>
 *         <p></p>
 *         <li>
 *           If a language doesn't have an associated q-value, the value is
 *           assumed to be 1, that is the maximum.
 *         </li>
 *         <p></p>
 *         <li>
 *           If a language has a q-value of 0 it is removed from the list of
 *           languages that we will try to match.
 *         </li>
 *         <p></p>
 *         <li>
 *           If the special case "*" language is found, it is replaced by the
 *           system default language. It's q-value is left the same.
 *         </li>
 *         <p></p>
 *         <li>
 *           The case of "*" mapping to a language that was specifically
 *           denied by the user, as in: "en; q=0, *" where the default
 *           langauge is "en" is handled by accepting the "en" language.
 *         </li>
 *         <p></p>
 *         <li>
 *           If the headers specify something like: "en-gb;q=1.0, en; q=0.0"
 *           and the system does not support "en_GB" directly but does support
 *           "en" then "en" will be served. The system does not obey the RFC
 *           specified fallback overriding.
 *       </ul>
 *     </p>
 *   </li>
 *   <li>The default Locale of the system.</li>
 * </ol>
 * </p>
 * <p>
 * The Charset for a request Charset is determined in one of three ways in
 * order of precedence:
 * <ol>
 *   <li>
 *     User's preferred Charset as determined by the ACS (as long as it is
 *     supported by the selected Locale.
 *   </li>
 *   <li>
 *     User's preferred Charset as determined by the Accept-Charset
 *     headers.
 *   </li>
 *   <li>The default Charset of the selected Locale.</li>
 * </ol>
 * </p>
 * <p>
 * The ResourceBundle is determined by the targetBundle as determined by the
 * application and the selected Locale.
 * </p>
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 * @version $Id: LocaleNegotiator.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class LocaleNegotiator {

    private static final Logger s_cat =
        Logger.getLogger(LocaleNegotiator.class.getName());

    private static LocaleProvider s_applicationLocaleProvider = null;
    private static LocaleProvider s_userLocaleProvider = null;
    private static LocaleProvider s_clientLocaleProvider = null;
    private static LocaleProvider s_systemLocaleProvider = null;
    private Locale s_applicationLocale = null;
    private Locale s_userLocale = null;
    private Locale s_clientLocale = null;
    private Locale s_systemLocale = null;

    private ResourceBundle m_chosenBundle = null;
    private Locale m_chosenLocale = null;
    private String m_chosenCharset = "";

    private ResourceBundle m_defaultBundle = null;
    private Locale m_defaultLocale = null;
    private String m_defaultCharset = "";

    {
        s_applicationLocale =
            (s_applicationLocaleProvider != null) ?
            s_applicationLocaleProvider.getLocale() : null;

        s_userLocale =
            (s_userLocaleProvider != null) ?
            s_userLocaleProvider.getLocale() : null;

        s_clientLocale =
            (s_clientLocaleProvider != null) ?
            s_clientLocaleProvider.getLocale() : null;

        s_systemLocale =
            (s_systemLocaleProvider != null) ?
            s_systemLocaleProvider.getLocale() : null;
    }

    /**
     * <p>
     * Select appropriate Locale, Charset, and ResourceBundle for a request.
     * </p>
     *
     * @param targetBundle The target ResourceBundle for this request.
     * @param acceptLanguages Acceptable languages according to the client.
     * @param acceptCharsets Acceptable character sets according to the client.
     */

    // FIXME: constructor parameters acceptLanguages and packageLocale are
    // passed in but never used. -- 2002-11-26
    public LocaleNegotiator(
                            String targetBundle,
                            String acceptLanguages,
                            String acceptCharsets,
                            Locale packageLocale
                            ) {
        if (acceptCharsets == null) {
            acceptCharsets = "";
        }

        ResourceBundle bundle = null;
        Locale locale = null;
        String charset = null;

        // Set up the defaults in case we can't do better.
        // XXX These should be some ad_parameter type thing eventually.
        m_defaultLocale =
            (s_systemLocale != null) ? s_systemLocale : Locale.getDefault();
        m_defaultCharset = "ISO-8859-1";
        m_defaultBundle = null;
        try {
            m_defaultBundle =
                ResourceBundle.getBundle(
                                         targetBundle, m_defaultLocale
                                         );
        } catch (MissingResourceException e) {
            if (s_cat.isInfoEnabled()) {
                s_cat.info("Didn't find ResourceBundle for " + targetBundle);
            }
        }

        // First case, Locale associated with a PackageInstance.
        if (s_applicationLocale != null) {
            locale = s_applicationLocale;
            // Second case, Locale as determined by ACS Preference for a User.
        } else if (s_userLocale != null) {
            locale = s_userLocale;
            // Third case, Locale as determined by Accept-Language headers.
        } else if (s_clientLocale != null) {
            locale = s_clientLocale;
            // Fourth case, use default.
        } else {
            locale = m_defaultLocale;
        }

        bundle = Globalization.getBundleNoFallback( targetBundle, locale,
                                                    m_defaultLocale   );

        // Find a charset we can use to display that Locale's language.
        charset = getCharsetForLocale(locale, acceptCharsets);

        // if charset is nulluse the default
        if (charset == null) {
            charset = m_defaultCharset;
        }

        // We didn't find a match
        m_chosenLocale = locale != null ? locale : m_defaultLocale;
        m_chosenCharset = charset != null ? charset : m_defaultCharset;
        m_chosenBundle = bundle != null ? bundle : m_defaultBundle;
    }

    /**
     * <p>
     * The negotiated Locale for this request
     * </p>
     *
     * @return Locale
     */
    public Locale getLocale() {
        return m_chosenLocale;
    }

    /**
     * <p>
     * The negotiated character set for this request
     * </p>
     *
     * @return String
     */
    public String getCharset() {
        return m_chosenCharset;
    }

    /**
     * <p>
     * The negotiated ResourceBundle for this request
     * </p>
     *
     * @return ResourceBundle
     */
    public ResourceBundle getBundle() {
        return m_chosenBundle;
    }

    /**
     * <p>
     * Get the character set associated with a request.
     * XXX We ignore the client charsets for now. Need to add this!
     * </p>
     *
     * @param locale The Locale object representing the language we want.
     * @param acceptCharsets Accpetable character sets according to the client.
     */
    protected String getCharsetForLocale(Locale locale, String acceptCharsets) {
        return Globalization.getDefaultCharset(locale);
    }

    /**
     * <p>
     * Get the application LocaleProvider.
     * </p>
     *
     * @return LocaleProvider
     */
    public static LocaleProvider getApplicationLocaleProvider() {
        return s_applicationLocaleProvider;
    }

    /**
     * <p>
     * Set the LocaleProvider for the application. This type of provider has
     * the highest priority.
     * </p>
     *
     * @param lp, The LocaleProvider for this type.
     */
    public static void setApplicationLocaleProvider(LocaleProvider lp) {
        s_applicationLocaleProvider = lp;
    }

    /**
     * <p>
     * Get the user LocaleProvider.
     * </p>
     *
     * @return LocaleProvider
     */
    public static LocaleProvider getUserLocaleProvider() {
        return s_userLocaleProvider;
    }

    /**
     * <p>
     * Set the LocaleProvider for the user. This type of provider has
     * the second highest priority.
     * </p>
     *
     * @param lp, The LocaleProvider for this type.
     */
    public static void setUserLocaleProvider(LocaleProvider lp) {
        s_userLocaleProvider = lp;
    }

    /**
     * <p>
     * Get the client LocaleProvider.
     * </p>
     *
     * @return LocaleProvider
     */
    public static LocaleProvider getClientLocaleProvider() {
        return s_clientLocaleProvider;
    }

    /**
     * <p>
     * Set the LocaleProvider for the client. This type of provider has
     * the third highest priority.
     * </p>
     *
     * @param lp, The LocaleProvider for this type.
     */
    public static void setClientLocaleProvider(LocaleProvider lp) {
        s_clientLocaleProvider = lp;
    }

    /**
     * <p>
     * Get the system LocaleProvider.
     * </p>
     *
     * @return LocaleProvider
     */
    public static LocaleProvider getSystemLocaleProvider() {
        return s_systemLocaleProvider;
    }

    /**
     * <p>
     * Set the LocaleProvider for the system. This type of provider has
     * the lowest priority.
     * </p>
     *
     * @param lp, The LocaleProvider for this type.
     */
    public static void setSystemLocaleProvider(LocaleProvider lp) {
        s_systemLocaleProvider = lp;
    }
}

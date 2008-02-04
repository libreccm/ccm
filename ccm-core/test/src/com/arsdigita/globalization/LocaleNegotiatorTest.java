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

import com.arsdigita.dispatcher.TestUtils;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleNegotiatorTest extends BaseTestCase {

    public static final String versionId = "$Id: LocaleNegotiatorTest.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public LocaleNegotiatorTest(String name) {
        super(name);
    }

    public static final String DefaultCharsetName = "ISO-8859-1";

    public void FAILStest0() throws Exception {
        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle0",
                                                   null, null, null);

        ResourceBundle rb = ResourceBundle.getBundle(
                                                     "com.arsdigita.globalization.TestBundle0");

        assertEquals("locale", Locale.getDefault(), ln.getLocale());
        assertEquals("charset", DefaultCharsetName, ln.getCharset());
        assertEquals("bundle", rb, ln.getBundle());
    }

    public void FAILStest1() throws Exception {
        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle0",
                                                   "", null, null);

        ResourceBundle rb = ResourceBundle.getBundle(
                                                     "com.arsdigita.globalization.TestBundle0");

        assertEquals("locale", Locale.getDefault(), ln.getLocale());
        assertEquals("charset", DefaultCharsetName, ln.getCharset());
        assertEquals("bundle", rb, ln.getBundle());
    }

    public void FAILStest2() throws Exception {
        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle0",
                                                   null, "", null);

        ResourceBundle rb = ResourceBundle.getBundle(
                                                     "com.arsdigita.globalization.TestBundle0");

        assertEquals("locale", Locale.getDefault(), ln.getLocale());
        assertEquals("charset", DefaultCharsetName, ln.getCharset());
        assertEquals("bundle", rb, ln.getBundle());
    }

    private Charset makeCharset(String name) {
        Charset cs = new Charset();
        cs.setCharset(name);
        cs.save();
        return cs;
    }

    private Locale makeLocale(
                              String language, String country, String variant,
                              Charset cs)
    {
        com.arsdigita.globalization.Locale l;

        l = new com.arsdigita.globalization.Locale(
                                                   language, country, variant);
        l.setDefaultCharset(cs);
        l.save();

        return l.toJavaLocale();
    }

    private void verify(LocaleNegotiator ln, Locale l,
                        String charsetName, String bundleName, Locale bundleLocale)
    {
        assertEquals("locale", l, ln.getLocale());
        assertEquals("charset", charsetName, ln.getCharset());
        assertEquals("bundle",
                     ResourceBundle.getBundle(
                                              "com.arsdigita.globalization.TestBundle" + bundleName,
                                              bundleLocale),
                     ln.getBundle());
    }

    public void FAILStest3() throws Exception {
        Charset cs0 = makeCharset("cs0");
        Locale l =
            makeLocale("l0", "",   "",   cs0);
        makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle3");
            clp.setAcceptLanguages("l0");
        }

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle3", "", "", null
                                                   );

        verify(ln, l, "cs0", "3", l);
    }

    public void FAILStest4() throws Exception {
        Charset cs0 = makeCharset("cs0");
        makeLocale("l0", "",   "",   cs0);
        Locale l =
            makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle3");
            clp.setAcceptLanguages("l0-C0");
        }

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle3", "", "", null
                                                   );

        verify(ln, l, "cs0", "3", l);
    }

    public void FAILStest5() throws Exception {
        Charset cs0 = makeCharset("cs0");

        Locale l = Locale.getDefault();

        makeLocale("l0", "",   "",   cs0);
        makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle3");
            clp.setAcceptLanguages("l0; q=0, *");
        }

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle3", "", "", null
                                                   );

        verify(ln, l, "ISO-8859-1", "3", Locale.getDefault());
    }

    public void FAILStest6() throws Exception {
        Charset cs0 = makeCharset("cs0");
        Locale l =
            makeLocale("l0", "",   "",   cs0);
        makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle3");
            clp.setAcceptLanguages("l1, l0");
        }

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle3", "", "", null
                                                   );

        verify(ln, l, "cs0", "3", l);
    }

    public void test7() throws Exception {
        Charset cs0 = makeCharset("cs0");
        makeLocale("l0", "",   "",   cs0);
        final Locale l =
            makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle3");
            clp.setAcceptLanguages("en");
        }

        LocaleNegotiator.setApplicationLocaleProvider(
                                                      new LocaleProvider() {
                                                          public Locale getLocale() {
                                                              return l;
                                                          }
                                                      }
                                                      );

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle3", "", "", null
                                                   );

        verify(ln, l, "cs0", "3", l);
    }

    public void test8() throws Exception {
        Charset cs0 = makeCharset("cs0");
        final Locale l =
            makeLocale("l0", "",   "",   cs0);
        makeLocale("l0", "C0", "",   cs0);

        LocaleNegotiator.setApplicationLocaleProvider(
                                                      new LocaleProvider() {
                                                          public Locale getLocale() {
                                                              return l;
                                                          }
                                                      }
                                                      );

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle8", "", "", null
                                                   );

        // Should this expect TestBundle8 instead of TestBundle8_en?
        verify(ln, l, "cs0", "8", new Locale("en", ""));
    }

    public void test9() throws Exception {
        TestUtils.setRequest(new HttpServletDummyRequest());
        Charset cs0 = makeCharset("cs0");
        Locale l =
            makeLocale("l0", "",   "",   cs0);
        makeLocale("l0", "C0", "",   cs0);

        ClientLocaleProvider clp = (ClientLocaleProvider)
            LocaleNegotiator.getClientLocaleProvider();
        if (clp != null) {
            clp.setTargetBundle("com.arsdigita.globalization.TestBundle9");
            clp.setAcceptLanguages("en; q=0.9, l0; q=1.0");
        }

        LocaleNegotiator ln = new LocaleNegotiator(
                                                   "com.arsdigita.globalization.TestBundle9", "", "", null
                                                   );

        verify(ln, l, "cs0", "9", l);
    }
}

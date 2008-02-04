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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * More tests for Locale DomainObject
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class Locale2Test extends BaseTestCase {
    public final static String versionId = "$Id: Locale2Test.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public Locale2Test(String name) {
        super(name);
    }

    private Charset getCharsetByName(String name) {
        DataCollection dc = SessionManager
            .getSession()
            .retrieve(Charset.BASE_DATA_OBJECT_TYPE);

        dc.addFilter("charset = :name").set("name", name);

        Charset charset = null;
        if (dc.next()) {
            charset = new Charset(dc.getDataObject());
        }

        dc.close();
        return charset;
    }

    private void testPredefinedLocale(String language, String country,
                                      String variant, Charset charset)
        throws Exception
    {
        Locale l;
        try {
            l = Locale.retrieve(language, country, variant);
        } catch (DataObjectNotFoundException e) {
            // this locale must not be installed
            return;
        }

        assertEquals("language", language, l.getLanguage());
        if (country.length() > 0) {
            assertEquals("country", country, l.getCountry());
        }
        if (variant.length() > 0) {
            assertEquals("variant", variant, l.getVariant());
        }
    }

    public void testPredefinedLocales() throws Exception {
        Charset iso88591 = getCharsetByName("ISO-8859-1");

        testPredefinedLocale("en", "US", "", iso88591);
        testPredefinedLocale("en", "",   "", iso88591);
        testPredefinedLocale("en", "ES", "", iso88591);
        testPredefinedLocale("es", "",   "", iso88591);
        testPredefinedLocale("da", "DK", "", iso88591);
        testPredefinedLocale("da", "",   "", iso88591);
        testPredefinedLocale("de", "DE", "", iso88591);
        testPredefinedLocale("de", "",   "", iso88591);
        testPredefinedLocale("fr", "FR", "", iso88591);
        testPredefinedLocale("fr", "",   "", iso88591);
    }

    public void testConstructor0() throws Exception {
        Locale l = new Locale();

        assertEquals("language", "", l.getLanguage());
        assertEquals("country", "", l.getCountry());
        assertEquals("variant", "", l.getVariant());
        assertNull("fallback", l.fallback());
        assertEquals("baseDataObjectType",
                     Locale.BASE_DATA_OBJECT_TYPE,
                     l.getBaseDataObjectType());
    }

    public void testConstructor1() throws Exception {
        Locale l = new Locale("xx");

        assertEquals("language", "xx", l.getLanguage());
        assertEquals("country", "", l.getCountry());
        assertEquals("variant", "", l.getVariant());
        assertNull("fallback", l.fallback());
        assertEquals("baseDataObjectType",
                     Locale.BASE_DATA_OBJECT_TYPE,
                     l.getBaseDataObjectType());
    }

    public void testConstructor2() throws Exception {
        try {
            Locale l = new Locale("");
            fail("created Locale with empty language");
        } catch (Exception e) {
        }

    }

    public void testConstructor3() throws Exception {
        try {
            Locale l = new Locale((String) null);
            fail("created Locale with null language");
        } catch (Exception e) {
        }
    }

    public void testConstructor4() throws Exception {
        Locale l = new Locale("xx", "YY");

        assertEquals("language", "xx", l.getLanguage());
        assertEquals("country", "YY", l.getCountry());
        assertEquals("variant", "", l.getVariant());
        assertNull("fallback", l.fallback());
        assertEquals("baseDataObjectType",
                     Locale.BASE_DATA_OBJECT_TYPE,
                     l.getBaseDataObjectType());
    }

    public void testConstructor5() throws Exception {
        Locale l = new Locale("xx", "YY", "ZZZ");

        assertEquals("language", "xx", l.getLanguage());
        assertEquals("country", "YY", l.getCountry());
        assertEquals("variant", "ZZZ", l.getVariant());
        assertNull("fallback", l.fallback());
        assertEquals("baseDataObjectType",
                     Locale.BASE_DATA_OBJECT_TYPE,
                     l.getBaseDataObjectType());
    }

    public void testSave0() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.retrieve("xx");
        assertEquals("id", l1.getID(), l2.getID());
        assertEquals("language", l1.getLanguage(), l2.getLanguage());
        assertEquals("country", l1.getCountry(), l2.getCountry());
        assertEquals("variant", l1.getVariant(), l2.getVariant());
        assertEquals("defaultCharset", l1.getDefaultCharset(),
                     l2.getDefaultCharset());
        assertEquals("fallback", l1.fallback(), l2.fallback());
    }

    public void testSave1() throws Exception {
        try {
            Locale l = new Locale();
            l.save();

            fail("saved Locale with null language");
        } catch (PersistenceException e) {
        }
    }

    public void testSave2() throws Exception {
        Locale l1 = new Locale("xx", "YY");
        l1.save();

        Locale l2 = Locale.retrieve("xx", "YY");
        assertEquals("id", l1.getID(), l2.getID());
        assertEquals("language", l1.getLanguage(), l2.getLanguage());
        assertEquals("country", l1.getCountry(), l2.getCountry());
        assertEquals("variant", l1.getVariant(), l2.getVariant());
        assertEquals("defaultCharset", l1.getDefaultCharset(),
                     l2.getDefaultCharset());
        assertEquals("fallback", l1.fallback(), l2.fallback());
    }

    public void testSave3() throws Exception {
        Locale l1 = new Locale("xx", "YY", "ZZZ");
        l1.save();

        Locale l2 = Locale.retrieve("xx", "YY", "ZZZ");
        assertEquals("id", l1.getID(), l2.getID());
        assertEquals("language", l1.getLanguage(), l2.getLanguage());
        assertEquals("country", l1.getCountry(), l2.getCountry());
        assertEquals("variant", l1.getVariant(), l2.getVariant());
        assertEquals("defaultCharset", l1.getDefaultCharset(),
                     l2.getDefaultCharset());
        assertEquals("fallback", l1.fallback(), l2.fallback());
    }

    public void testSave4() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = new Locale("yy");
        l2.save();

        if (l1.getID().equals(l2.getID())) {
            fail("different Locales have same ID");
        }
    }

    public void testFallback() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = new Locale("xx", "YY");
        l2.save();

        Locale l3 = new Locale("xx", "YY", "ZZZ");
        l3.save();

        Locale l4 = new Locale("xx", "AA");
        l4.save();

        Locale l5 = new Locale("xx", "AA", "BBB");
        l5.save();

        Locale l6 = new Locale("xx", "YY", "BBB");
        l6.save();

        assertEquals("fallback xx",        null, l1.fallback());
        assertEquals("fallback xx/YY",     l1,   l2.fallback());
        assertEquals("fallback xx/YY/ZZZ", l2,   l3.fallback());
        assertEquals("fallback xx/AA",     l1,   l4.fallback());
        assertEquals("fallback xx/AA/BBB", l4,   l5.fallback());
        assertEquals("fallback xx/YY/BBB", l2,   l6.fallback());

        assertEquals("static fallback xx",        null,
                     Locale.fallback(new java.util.Locale("xx", "")));
        assertEquals("static fallback xx/YY",     l1,
                     Locale.fallback(new java.util.Locale("xx", "YY")));
        assertEquals("static fallback xx/YY/ZZZ", l2,
                     Locale.fallback(new java.util.Locale("xx", "YY", "ZZZ")));
        assertEquals("static fallback xx/AA",     l1,
                     Locale.fallback(new java.util.Locale("xx", "AA")));
        assertEquals("static fallback xx/AA/BBB", l4,
                     Locale.fallback(new java.util.Locale("xx", "AA", "BBB")));
        assertEquals("static fallback xx/AA/BBB", l2,
                     Locale.fallback(new java.util.Locale("xx", "YY", "BBB")));
    }

    public void testSetLanguage0() throws Exception {
        Locale l = new Locale();
        l.setLanguage("xx");
        assertEquals("language", "xx", l.getLanguage());
    }

    public void testSetLanguage1() throws Exception {
        try {
            new Locale().setLanguage("");
            fail("Locale allowed empty language");
        } catch (Exception e) {
        }
    }

    public void testSetLanguage2() throws Exception {
        try {
            new Locale().setLanguage(null);
            fail("Locale allowed null language");
        } catch (Exception e) {
        }
    }

    public void testSetCountry0() throws Exception {
        Locale l = new Locale();
        l.setCountry("YY");
        assertEquals("country", "YY", l.getCountry());
    }

    public void testSetCountry1() throws Exception {
        Locale l = new Locale();
        l.setCountry("YY");
        l.setCountry("");
        assertEquals("country", "", l.getCountry());
    }

    public void testSetCountry2() throws Exception {
        Locale l = new Locale();
        l.setCountry("YY");
        l.setCountry(null);
        assertEquals("country", "", l.getCountry());
    }

    public void testSetVariant0() throws Exception {
        Locale l = new Locale();
        l.setVariant("ZZZ");
        assertEquals("variant", "ZZZ", l.getVariant());
    }

    public void testSetVariant1() throws Exception {
        Locale l = new Locale();
        l.setVariant("ZZZ");
        l.setVariant("");
        assertEquals("variant", "", l.getVariant());
    }

    public void testSetVariant2() throws Exception {
        Locale l = new Locale();
        l.setVariant("ZZZ");
        l.setVariant(null);
        assertEquals("variant", "", l.getVariant());
    }

    public void testSetDefaultCharset0() throws Exception {
        Locale l = new Locale("xx", "YY", "ZZZ");

        Charset cs = getCharsetByName("ISO-8859-1");
        l.setDefaultCharset(cs);
        assertEquals("defaultCharset", cs, l.getDefaultCharset());
    }

    public void testSetDefaultCharset1() throws Exception {
        Locale l = new Locale("xx", "YY", "ZZZ");

        Charset cs = getCharsetByName("ISO-8859-1");
        l.setDefaultCharset(cs);
        l.setDefaultCharset(null);
        assertNull("defaultCharset", l.getDefaultCharset());
    }

    public void testFromJavaLocale0() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.fromJavaLocale(
                                          new java.util.Locale("xx", ""));

        assertEquals(l1, l2);
    }

    public void testFromJavaLocale1() throws Exception {
        try {
            Locale l = Locale.fromJavaLocale(
                                             new java.util.Locale("xx", ""));
            fail("no exception for Locale xx");
        } catch (GlobalizationException e) {
        }
    }

    public void testFromJavaLocale2() throws Exception {
        Locale l1 = new Locale("xx", "YY");
        l1.save();

        Locale l2 = Locale.fromJavaLocale(
                                          new java.util.Locale("xx", "YY"));
        assertEquals(l1, l2);
    }

    public void testFromJavaLocale3() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        try {
            Locale l2 = Locale.fromJavaLocale(new java.util.Locale("xx", "YY"));
            fail("found Locale for xx/YY");
        } catch (GlobalizationException e) {
        }
    }

    public void testFromJavaLocale4() throws Exception {
        Locale l1 = new Locale("xx", "YY");
        l1.save();

        try {
            Locale l2 = Locale.fromJavaLocale(
                                              new java.util.Locale("xx", ""));
            fail("found Locale for xx");
        } catch (GlobalizationException e) {
        }
    }

    public void testFromJavaLocale5() throws Exception {
        Locale l1 = new Locale("xx", "YY", "ZZZ");
        l1.save();

        Locale l2 = Locale.fromJavaLocale(
                                          new java.util.Locale("xx", "YY", "ZZZ"));
        assertEquals(l1, l2);
    }

    public void testFromJavaLocale6() throws Exception {
        Locale l1 = new Locale("xx", "YY", "qqq");
        l1.save();

        try {
            Locale l2 = Locale.fromJavaLocale(
                                              new java.util.Locale("xx", "YY", "ZZZ"));
            fail("found Locale for xx/YYY/ZZZ");
        } catch (GlobalizationException e) {
        }
    }

    public void testFromJavaLocaleBestMatch0() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", ""));

        assertEquals(l1, l2);
    }

    public void testFromJavaLocaleBestMatch1() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("yy", ""));
        if (l2 != null) {
            fail("found best match for yy");
        }
    }

    public void testFromJavaLocaleBestMatch2() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", "YY"));
        assertEquals(l1, l2);
    }

    public void testFromJavaLocaleBestMatch3() throws Exception {
        Locale l1 = new Locale("xx", "QQ");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", "YY"));
        if (l2 != null) {
            fail("found best match for xx/YY");
        }
    }

    public void testFromJavaLocaleBestMatch4() throws Exception {
        Locale l1 = new Locale("xx");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", "YY", "ZZZ"));
        assertEquals(l1, l2);
    }

    public void testFromJavaLocaleBestMatch5() throws Exception {
        Locale l1 = new Locale("xx", "YY");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", "YY", "ZZZ"));
        assertEquals(l1, l2);
    }

    public void testFromJavaLocaleBestMatch6() throws Exception {
        Locale l1 = new Locale("xx", "YY", "QQQ");
        l1.save();

        Locale l2 = Locale.fromJavaLocaleBestMatch(
                                                   new java.util.Locale("xx", "YY", "ZZZ"));
        if (l2 != null) {
            fail("found best match for xx/YY/ZZZ");
        }
    }

    public void testToJavaLocale0() throws Exception {
        Locale l1 = new Locale("xx");

        java.util.Locale l2 = l1.toJavaLocale();
        assertEquals("language", "xx", l2.getLanguage());
        assertEquals("country", "", l2.getCountry());
        assertEquals("variant", "", l2.getVariant());

        java.util.Locale l3 = Locale.toJavaLocale(l1);
        assertEquals(l2, l3);
    }

    public void testToJavaLocale1() throws Exception {
        Locale l1 = new Locale("xx", "YY");

        java.util.Locale l2 = l1.toJavaLocale();
        assertEquals("language", "xx", l2.getLanguage());
        assertEquals("country", "YY", l2.getCountry());
        assertEquals("variant", "", l2.getVariant());

        java.util.Locale l3 = Locale.toJavaLocale(l1);
        assertEquals(l2, l3);
    }

    public void testToJavaLocale2() throws Exception {
        Locale l1 = new Locale("xx", "YY", "ZZZ");

        java.util.Locale l2 = l1.toJavaLocale();
        assertEquals("language", "xx", l2.getLanguage());
        assertEquals("country", "YY", l2.getCountry());
        assertEquals("variant", "ZZZ", l2.getVariant());

        java.util.Locale l3 = Locale.toJavaLocale(l1);
        assertEquals(l2, l3);
    }
}

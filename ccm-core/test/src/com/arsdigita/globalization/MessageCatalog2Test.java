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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * More tests for MessageCatalog DomainObject
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class MessageCatalog2Test extends BaseTestCase {
    public final static String versionId = "$Id: MessageCatalog2Test.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public MessageCatalog2Test(String name) {
        super(name);
    }

    public void testConstructor0() throws Exception {
        MessageCatalog mc = new MessageCatalog("testmc");

        assertEquals("mc.getName()", "testmc", mc.getName());
        assertEquals("mc.isReadOnly()", false, mc.isReadOnly());
        assertNull("mc.getLocale()", mc.getLocale());
        assertNull("mc.getMap()", mc.getMap());
        assertEquals("mc.getBaseDataObjectType()",
                     mc.getClass().getName(), mc.getBaseDataObjectType());
    }

    public void testConstructor1() throws Exception {
        try {
            String nullString = null;
            MessageCatalog mc = new MessageCatalog(nullString);
            fail("MessageCatalog allowed null name");
        } catch (Exception e) {
            // Should get some exception.
        }
    }

    public void testConstructor2() throws Exception {
        try {
            MessageCatalog mc = new MessageCatalog("");
            fail("MessageCatalog allowed empty name");
        } catch (Exception e) {
            // Should get some exception.
        }
    }

    public void testConstructor3() throws Exception {
        // Make sure there's no globalization.Locale in the database.
        try {
            com.arsdigita.globalization.Locale locale
                = new com.arsdigita.globalization.Locale("xx", "xx", "xx");
            locale.delete();
        } catch (Exception e) {
        }

        try {
            java.util.Locale locale
                = new java.util.Locale("xx", "xx", "xx");
            MessageCatalog mc = new MessageCatalog("testmc", locale);
            fail("MessageCatalog allowed java.util.Locale with no corresponding com.arsdigita.globalization.Locale");
        } catch (Exception e) {
        }
    }

    public void testConstructor4() throws Exception {
        java.util.Locale locale = new java.util.Locale("en", "US");
        MessageCatalog mc = new MessageCatalog("testmc", locale);

        assertEquals("getName", "testmc", mc.getName());
        assertEquals("isReadOnly", false, mc.isReadOnly());
        assertEquals("locale", locale, mc.getLocale());
        assertNull("mc.getMap()", mc.getMap());
        assertEquals("mc.getBaseDataObjectType()",
                     mc.getClass().getName(), mc.getBaseDataObjectType());
    }

    public void testConstructor5() throws Exception {
        java.util.Locale locale = new java.util.Locale("en", "US");
        try {
            MessageCatalog mc = new MessageCatalog("", locale);
            fail("MessageCatalog allowed empty name");
        }
        catch (Exception e) {
        }
    }

    public void testConstructor6() throws Exception {
        java.util.Locale locale = new java.util.Locale("en", "US");
        try {
            MessageCatalog mc = new MessageCatalog(null, locale);
            fail("MessageCatalog allowed null name");
        }
        catch (Exception e) {
        }
    }

    public void testConstructor7() throws Exception {
        MessageCatalog mc1 = new MessageCatalog("testmc1");
        MessageCatalog mc2 = new MessageCatalog("testmc2");

        mc1.save();
        mc2.save();

        if (mc1.getID().equals(mc2.getID())) {
            fail("mc1 and mc2 have same id");
        }
    }

    public void testSetLocale0() throws Exception {
        MessageCatalog mc = new MessageCatalog("testmc");
        java.util.Locale locale_en_US
            = new java.util.Locale("en", "US");
        java.util.Locale locale_es_ES
            = new java.util.Locale("es", "ES");

        assertNull("null 0", mc.getLocale());

        mc.setLocale(locale_en_US);
        assertEquals("en_US", locale_en_US, mc.getLocale());

        mc.setLocale(locale_es_ES);
        assertEquals("es_ES", locale_es_ES, mc.getLocale());

        mc.setLocale((java.util.Locale) null);
        assertNull("null 1", mc.getLocale());
    }

    public void testSetLocale1() throws Exception {
        MessageCatalog mc = new MessageCatalog("testmc");
        com.arsdigita.globalization.Locale locale_en_US
            = com.arsdigita.globalization.Locale.retrieve(
                                                          "en", "US");
        java.util.Locale j_en_US = locale_en_US.toJavaLocale();
        com.arsdigita.globalization.Locale locale_es_ES
            = com.arsdigita.globalization.Locale.retrieve(
                                                          "es", "ES");
        java.util.Locale j_es_ES = locale_es_ES.toJavaLocale();

        assertNull("null 0", mc.getLocale());

        mc.setLocale(locale_en_US);
        assertEquals("en_US", j_en_US, mc.getLocale());

        mc.setLocale(locale_es_ES);
        assertEquals("es_ES", j_es_ES, mc.getLocale());

        mc.setLocale((com.arsdigita.globalization.Locale) null);
        assertNull("null 1", mc.getLocale());
    }

    public void testSetMap0() throws Exception {
        MessageCatalog mc = new MessageCatalog("testmc");

        Map tmap = new TreeMap();
        Map hmap = new HashMap();

        mc.setMap(tmap);
        assertEquals("mc.getMap()", hmap, mc.getMap());

        tmap.put("positive", "Yes");
        tmap.put("negative", "No");
        mc.setMap(tmap);

        assertEquals("mc.getMap()", tmap, mc.getMap());

        tmap.put("uncertain", "Maybe");
        if (tmap.equals(mc.getMap())) {
            fail("mc.setMap() failed to copy map");
        }

        hmap.put("#ff0000", "red");
        hmap.put("#00ff00", "green");
        hmap.put("#0000ff", "blue");

        mc.setMap(hmap);

        assertEquals("mc.getMap()", hmap, mc.getMap());

        mc.setMap(null);

        assertNull("mc.getMap()", mc.getMap());
    }

    private void purgeMessageCatalog(String name, java.util.Locale locale) {
        try {
            MessageCatalog mc
                = MessageCatalog.retrieveForEdit(name, locale);
            mc.delete();
        } catch (Exception e) {
        }
    }

    private void testNoSuchMessageCatalog(String name) throws Exception {
        purgeMessageCatalog(name, null);

        try {
            MessageCatalog mc = MessageCatalog
                .retrieve(name);
            fail("MessageCatalog \"" + name + "\" exists");
        }

        catch (GlobalizationException e) {
            try {
                MessageCatalog mc = MessageCatalog
                    .retrieveForEdit(name);
                fail("MessageCatalog \"" + name + "\" exists");
            }

            catch (GlobalizationException e2) {
            }
        }
    }

    private void testNoSuchMessageCatalog(String name,
                                          java.util.Locale locale) throws Exception
    {
        purgeMessageCatalog(name, locale);

        try {
            MessageCatalog mc = MessageCatalog
                .retrieve(name, locale);
            fail("MessageCatalog \"" + name + "\"/" + locale + " exists");
        }

        catch (GlobalizationException e) {
            try {
                MessageCatalog mc = MessageCatalog
                    .retrieveForEdit(name, locale);
                fail("MessageCatalog \"" + name + "\"/" + locale + " exists");
            }

            catch (GlobalizationException e2) {
            }
        }
    }

    public void testRetrieve0() throws Exception {
        testNoSuchMessageCatalog("testmc");
    }

    public void testRetrieve1() throws Exception {
        testNoSuchMessageCatalog("");
    }

    public void testRetrieve2() throws Exception {
        testNoSuchMessageCatalog(null);
    }

    public void testRetrieve3() throws Exception {
        testNoSuchMessageCatalog("testmc",
                                 new java.util.Locale("en", "US"));
    }

    public void testRetrieve4() throws Exception {
        testNoSuchMessageCatalog("",
                                 new java.util.Locale("en", "US"));
    }

    public void testRetrieve5() throws Exception {
        testNoSuchMessageCatalog(null,
                                 new java.util.Locale("en", "US"));
    }

    private void testSaveRetrieve(String name, java.util.Locale locale,
                                  Map map) throws Exception
    {
        purgeMessageCatalog(name, locale);

        MessageCatalog mc = (locale == null)
            ? new MessageCatalog(name)
            : new MessageCatalog(name, locale);
        if (map != null) {
            mc.setMap(map);
        }
        mc.save();

        MessageCatalog mc2 = (locale == null)
            ? MessageCatalog.retrieve(name)
            : MessageCatalog.retrieve(name, locale);

        assertEquals("id", mc.getID(), mc2.getID());
        assertEquals("name", mc.getName(), mc2.getName());
        assertEquals("locale", mc.getLocale(), mc2.getLocale());
        assertEquals("map", mc.getMap(), mc2.getMap());
        assertEquals("isReadOnly", true, mc2.isReadOnly());

        try {
            mc2.setMap(null);
            fail("retrieve returned modifiable MessageCatalog");
        }

        catch (GlobalizationException e) {
        }

        MessageCatalog mc3 = (locale == null)
            ? MessageCatalog.retrieveForEdit(name)
            : MessageCatalog.retrieveForEdit(name, locale);

        assertEquals("id", mc.getID(), mc3.getID());
        assertEquals("name", mc.getName(), mc3.getName());
        assertEquals("locale", mc.getLocale(), mc3.getLocale());
        assertEquals("map", mc.getMap(), mc3.getMap());
        assertEquals("isReadOnly", false, mc3.isReadOnly());

        mc3.setMap(null);
        mc3.save();

        MessageCatalog mc4 = (locale == null)
            ? MessageCatalog.retrieveForEdit(name)
            : MessageCatalog.retrieveForEdit(name, locale);
        if (mc4.getMap() != null) {
            fail("MessageCatalog had wrong map");
        }

        mc3.setMap(map);
        mc3.save();

        MessageCatalog mc5 = (locale == null)
            ? MessageCatalog.retrieveForEdit(name)
            : MessageCatalog.retrieveForEdit(name, locale);
        assertEquals("map", map, mc5.getMap());
    }

    public void testRetrieve6() throws Exception {
        testSaveRetrieve("testmc", null, null);
    }

    public void testRetrieve7() throws Exception {
        testSaveRetrieve("testmc",
                         new java.util.Locale("en", "US"), null);
    }

    public void testRetrieve8() throws Exception {
        testSaveRetrieve("testmc",
                         new java.util.Locale("es", "ES"), null);
    }

    public void testRetrieve9() throws Exception {
        Map map = new TreeMap();

        map.put("positive", "Yes");
        map.put("negative", "No");

        testSaveRetrieve("testmc",
                         new java.util.Locale("en", "US"), map);
    }

    public void testRetrieve10() throws Exception {
        Map map = new HashMap();

        map.put("positive", "Yes");
        map.put("negative", "No");

        testSaveRetrieve("testmc",
                         new java.util.Locale("en", "US"), map);
    }

    public void testRetrieve11() throws Exception {
        Map map = new TreeMap();

        testSaveRetrieve("testmc",
                         new java.util.Locale("en", "US"), map);
    }

    public void testRetrieve12() throws Exception {
        String name = "testmc";
        java.util.Locale locale = new java.util.Locale("en", "US");

        Map map1 = new TreeMap();
        map1.put("a", "apple");
        map1.put("b", "banana");
        map1.put("c", "carrot");

        Map map2 = new TreeMap();
        map2.put("1", "one");
        map2.put("2", "two");

        purgeMessageCatalog(name, locale);

        MessageCatalog mc = new MessageCatalog(name, locale);
        mc.setMap(map1);
        mc.save();

        mc.setMap(map2);
        mc.save();

        MessageCatalog mc2 = MessageCatalog.retrieve(name, locale);

        assertEquals("id", mc.getID(), mc2.getID());
        assertEquals("name", mc.getName(), mc2.getName());
        assertEquals("locale", mc.getLocale(), mc2.getLocale());
        assertEquals("isReadOnly", true, mc2.isReadOnly());

        assertEquals("map", map2, mc2.getMap());
    }

    private void testSaveRetrieveForEdit(String name,
                                         java.util.Locale locale,
                                         Map map) throws Exception
    {
        purgeMessageCatalog(name, locale);

        MessageCatalog mc = (locale == null)
            ? new MessageCatalog(name)
            : new MessageCatalog(name, locale);
        if (map != null) {
            mc.setMap(map);
        }
        mc.save();

        MessageCatalog mc2 = (locale == null)
            ? MessageCatalog.retrieve(name)
            : MessageCatalog.retrieve(name, locale);

        assertEquals("id", mc.getID(), mc2.getID());
        assertEquals("name", mc.getName(), mc2.getName());
        assertEquals("locale", mc.getLocale(), mc2.getLocale());
        assertEquals("map", mc.getMap(), mc2.getMap());
        assertEquals("isReadOnly", true, mc2.isReadOnly());

        try {
            mc2.setMap(null);
            fail("retrieve returned modifiable MessageCatalog");
        }

        catch (GlobalizationException e) {
        }
    }
}

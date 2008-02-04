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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * More tests for Charset DomainObject
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class Charset2Test extends BaseTestCase {
    public final static String versionId = "$Id: Charset2Test.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public Charset2Test(String name) {
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

    public void testIso88591() {
        Charset cs = getCharsetByName("ISO-8859-1");
        assertEquals("charset", "ISO-8859-1", cs.getCharset());
        assertEquals("getBaseDataObjectType",
                     "com.arsdigita.globalization.Charset",
                     cs.getBaseDataObjectType());
    }

    public void testConstructor0() {
        Charset cs = new Charset();

        assertNull("charset", cs.getCharset());
        assertEquals("getBaseDataObjectType",
                     "com.arsdigita.globalization.Charset",
                     cs.getBaseDataObjectType());
    }

    public void testSetCharset0() {
        try {
            new Charset().setCharset(null);
            fail("setCharset allowed null");
        }

        catch (IllegalArgumentException e) {
        }
    }

    public void testSetCharset1() {
        try {
            new Charset().setCharset("");
            fail("setCharset allowed empty string");
        }

        catch (IllegalArgumentException e) {
        }
    }

    public void testSetCharset2() {
        Charset cs = new Charset();
        cs.setCharset("foo");
        assertEquals("charset", "foo", cs.getCharset());
    }

    public void testSetCharset3() {
        Charset cs = new Charset();
        cs.setCharset("some-long-charset-name");
        assertEquals("charset", "some-long-charset-name", cs.getCharset());
    }

    public void testSave0() {
        Charset cs = new Charset();

        try {
            cs.save();
            fail("save succeeded with null charset");
        } catch (PersistenceException e) {
        }
    }

    public void testSave1() {
        Charset cs = new Charset();
        cs.setCharset("some-long-charset-name");
        cs.save();
    }

    public void testSave2() {
        Charset cs1 = new Charset();
        cs1.setCharset("foo");
        cs1.save();

        assertNotNull("id", cs1.getID());

        Charset cs2 = getCharsetByName("foo");
        assertEquals("charset", cs1.getCharset(), cs2.getCharset());
        assertEquals("id", cs1.getID(), cs2.getID());
    }

    public void testSave3() {
        Charset cs1 = new Charset();
        cs1.setCharset("foo");
        cs1.save();

        Charset cs2 = new Charset();
        cs2.setCharset("bar");
        cs2.save();

        if (cs1.getID().equals(cs2.getID())) {
            fail("different Charsets have same id");
        }
    }
}

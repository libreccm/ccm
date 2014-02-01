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
import java.util.Locale;

/**
 * <p>
 * Testing the GlobalizedMessage class.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class GlobalizedMessageTest extends BaseTestCase {
    

    private final static String bundleName = "com.arsdigita.globalization.GMBundle";
    private final static Locale l_en = new Locale("en", "", "");
    private final static Locale l_es = new Locale("es", "", "");

    public GlobalizedMessageTest(String name) {
        super(name);
    }

    public void test1() throws Exception {
        String key = "key.1";
        String message_en = "message.1";
        String message_es = "mensaje.1";

        GlobalizedMessage gm = new GlobalizedMessage(key, bundleName);

        assertEquals("message_en", message_en, (String) gm.localize(l_en));
        assertEquals("message_es", message_es, (String) gm.localize(l_es));
    }

    public void test2() throws Exception {
        String key = "key.2";
        String message_en = "This is a red wine.";
        String message_es = "Este es un vino rojo.";
        Object[] args = new Object[] {
            new GlobalizedMessage("key.3", bundleName)
        };

        GlobalizedMessage gm = new GlobalizedMessage(key, bundleName, args);

        assertEquals("message_en", message_en, (String) gm.localize(l_en));
        assertEquals("message_es", message_es, (String) gm.localize(l_es));
    }

    public void test3() throws Exception {
        String key = "key.4";
        String message_en = "message.4";
        String message_es = "mensaje.4";
        Object[] args = null;

        GlobalizedMessage gm = new GlobalizedMessage(key, bundleName, args);

        assertEquals("message_en", message_en, (String) gm.localize(l_en));
        assertEquals("message_es", message_es, (String) gm.localize(l_es));
    }

    public void test4() throws Exception {
        String key = "key.5";
        String message_en = "message.5";
        String message_es = "mensaje.5";
        Object[] args = new Object[] { null };

        GlobalizedMessage gm = new GlobalizedMessage(key, bundleName, args);

        assertEquals("message_en", message_en, (String) gm.localize(l_en));
        assertEquals("message_es", message_es, (String) gm.localize(l_es));
    }

    public void test5() throws Exception {
        String key = "key.6";
        String message_en = "message.null.6";
        String message_es = "mensaje.null.6";
        Object[] args = new Object[] { null };

        GlobalizedMessage gm = new GlobalizedMessage(key, bundleName, args);

        assertEquals("message_en", message_en, (String) gm.localize(l_en));
        assertEquals("message_es", message_es, (String) gm.localize(l_es));
    }
}

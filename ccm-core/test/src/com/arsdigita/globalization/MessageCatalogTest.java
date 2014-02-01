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

/**
 * <p>
 * Test for MessageCatalog DomainObject
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class MessageCatalogTest extends BaseTestCase {
    

    /**
     * Construct a MessageCatalogTest with the specified name.
     *
     * @param name
     */
    public MessageCatalogTest(String name) {
        super(name);
    }

    /**
     * Test persistence of MessageCatalog DomainObject.
     */
    public void testPersistence() throws Exception {
        String bundleName = getClass().getName();
        java.util.Locale locale = new java.util.Locale("es", "", "");
        String key1 = "key1";
        String message1 = "message1";
        String key2 = "key2";
        String message2 = "message2";
        String key3 = "key3";
        String message3 = "message3";

        Map map = new HashMap();

        map.put(key1, message1);
        map.put(key2, message2);
        map.put(key3, message3);

        // create a MessageCatalog object and save it to the database.
        MessageCatalog catalog = new MessageCatalog(bundleName);
        catalog.setMap(map);
        catalog.save();

        // retrieve the MessageCatalog object we just created and make sure
        // all values are what we expect.
        catalog = MessageCatalog.retrieve(bundleName);
        map = catalog.getMap();

        assertEquals(
                     "Messages don't match",
                     message1,
                     (String) map.get(key1)
                     );
    }
}

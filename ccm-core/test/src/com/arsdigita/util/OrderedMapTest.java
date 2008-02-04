/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import java.util.Iterator;
import junit.framework.TestCase;

/**
 * @author Jim Parsons
 * @author Justin Ross
 * @version $Id: OrderedMapTest.java 750 2005-09-02 12:38:44Z sskracic $
 */
public class OrderedMapTest extends TestCase {
    public void testOrderedMap() {
        final OrderedMap map = new OrderedMap();

        map.put(new Integer(1), "one");
        map.put(new Integer(2), "two");
        map.put(new Integer(3), new String("three"));
        map.put(new Integer(1), new String("one"));
        map.put(new Integer(3), new String("four"));

        final Iterator iter = map.values().iterator();

        while (iter.hasNext()) {
            System.out.println((String) iter.next());
        }
    }
}

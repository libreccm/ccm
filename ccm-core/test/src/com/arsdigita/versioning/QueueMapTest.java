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
package com.arsdigita.versioning;

import java.util.NoSuchElementException;
import junit.framework.TestCase;

public class QueueMapTest extends TestCase {
    private final static String S1 = "s1";
    private final static String S2 = "s2";
    private final static String S3 = "s3";
    private final static String S4 = "s4";

    public QueueMapTest(String name) {
        super(name);
    }

    public void testQueueMap() {
        QueueMap qm = new QueueMap();
        assertTrue("queue map is empty", !qm.hasNext());
        qm.enqueue(S1, S1);
        assertTrue("queue map is not empty", qm.hasNext());
        qm.enqueue(S2, S2);
        assertEquals("S1 dequeued", S1, (String) qm.dequeue());
        boolean hasBeenRaised = false;
        try {
            qm.enqueue(S1, "whatever");
        } catch (QueueMap.AlreadyEnqueuedException ex) {
            hasBeenRaised = true;
        }
        assertTrue("exception has been properly raised",
                   hasBeenRaised);
        qm.enqueue(S3, S3);
        assertEquals("S2 dequeued", S2, (String) qm.dequeue());
        assertEquals("S3 dequeed", S3, (String) qm.dequeue());
        assertTrue("queue map is empty", !qm.hasNext());

        hasBeenRaised = false;
        try {
            qm.dequeue();
        } catch (NoSuchElementException ex) {
            hasBeenRaised = true;
        }
        assertTrue("NoSuchElementException has been properly raised",
                   hasBeenRaised);
        assertTrue("contains S1", qm.contains(S1));
        assertTrue("contains S2", qm.contains(S2));
        assertTrue("contains S3", qm.contains(S3));
    }

    public void testRewind() {
        QueueMap qm = new QueueMap();
        qm.enqueue(S1, S1);
        qm.enqueue(S2, S2);
        qm.enqueue(S3, S3);
        qm.dequeue();
        qm.dequeue();
        qm.dequeue();
        qm.rewind();
        assertEquals("S1 dequeued", S1, (String) qm.dequeue());
        qm.dequeue();
        qm.rewind();
        qm.dequeue();
        assertEquals("S2 dequeued", S2, (String) qm.dequeue());
    }
}

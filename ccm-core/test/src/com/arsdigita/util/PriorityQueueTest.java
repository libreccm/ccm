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
package com.arsdigita.util;

import java.util.NoSuchElementException;
import junit.framework.TestCase;

public class PriorityQueueTest extends TestCase {

    public PriorityQueueTest(String name) {
        super(name);
    }

    public void testPriorityQueueAscending() {
        PriorityQueue pq = new PriorityQueue();

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        pq.enqueue("Test 2", 2);
        pq.enqueue("Test 3", 3);
        pq.enqueue("Test 1", 1);

        assertTrue("isEmpty is returned true on an nonempty queue", !pq.isEmpty());

        String peek1 = (String)pq.peek();
        String s1 = (String)pq.dequeue();
        String peek2 = (String)pq.peek();
        String s2 = (String)pq.dequeue();
        String s3 = (String)pq.dequeue();

        assertTrue("size was incorrect (should be 3)", pq.size() != 3);
        assertTrue("isEmpty is returned false on an empty queue", pq.isEmpty());

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        assertTrue("Peek #1 incorrect", peek1.equals("Test 1"));
        assertTrue("Peek #2 incorrect", peek2.equals("Test 2"));
        assertTrue("Dequeue #1 incorrect", s1.equals("Test 1"));
        assertTrue("Dequeue #2 incorrect", s2.equals("Test 2"));
        assertTrue("Dequeue #3 incorrect", s3.equals("Test 3"));
    }

    public void testPriorityQueueDescending() {
        PriorityQueue pq = new PriorityQueue(false);

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        pq.enqueue("Test 2", 2);
        pq.enqueue("Test 1", 3);
        pq.enqueue("Test 3", 1);

        String peek1 = (String)pq.peek();
        String s1 = (String)pq.dequeue();
        String peek2 = (String)pq.peek();
        String s2 = (String)pq.dequeue();
        String s3 = (String)pq.dequeue();

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        assertTrue("Peek #1 incorrect", peek1.equals("Test 1"));
        assertTrue("Peek #2 incorrect", peek2.equals("Test 2"));
        assertTrue("Dequeue #1 incorrect", s1.equals("Test 1"));
        assertTrue("Dequeue #2 incorrect", s2.equals("Test 2"));
        assertTrue("Dequeue #3 incorrect", s3.equals("Test 3"));
    }
}

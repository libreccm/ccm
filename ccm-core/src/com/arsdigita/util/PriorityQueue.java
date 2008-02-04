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

import java.util.Comparator;
import java.util.TreeSet;

/**
 * A simple priority queue that is implemented by a backing tree set.
 * Ordering of the dequeues (ascending or descending) is specified at creation
 * time.  Note that this class defers synchronization to the user.
 *
 * @author Patrick McNeill
 * @since 4.7
 * @version $Id: PriorityQueue.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PriorityQueue {
    private TreeSet m_model = new TreeSet(new PQComparator());
    private boolean m_ascending;

    /**
     * Creates a new PriorityQueue that orders dequeues in ascending order
     * (lowest priority first)
     *
     */
    public PriorityQueue() {
        this(true);
    }

    /**
     * Creates a new PriorityQueue that orders dequeues in either ascending
     * or descending order.
     *
     * @param ascending true for ascending order, false otherwise
     */
    public PriorityQueue(boolean ascending) {
        m_ascending = ascending;
    }

    /**
     * Return the next object in the queue, removing it from the queue.
     *
     * @return the next object in the queue
     * @exception NoSuchElementException if the queue is empty
     */
    public Object dequeue() {
        PQEntry pq;
        if (m_ascending) {
            pq = (PQEntry)m_model.first();
        } else {
            pq = (PQEntry)m_model.last();
        }

        m_model.remove(pq);

        return pq.m_object;
    }

    /**
     * Return the next object in the queue.
     *
     * @return the next object in the queue
     * @exception NoSuchElementException if the queue is empty
     */
    public Object peek() {
        PQEntry pq;
        if (m_ascending) {
            pq = (PQEntry)m_model.first();
        } else {
            pq = (PQEntry)m_model.last();
        }

        return pq.m_object;
    }

    /**
     * Adds a new object to the queue with a given priority.
     *
     * @param object the Object to add
     * @param priority the Object's priority in the queue
     */
    public void enqueue(Object object, int priority) {
        m_model.add(new PQEntry(object, priority));
    }

    /**
     * Returns true if the queue is empty, false otherwise
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return m_model.isEmpty();
    }

    /**
     * Changes the dequeue ordering.
     *
     * @param ascending true for ascending order, false for descending
     */
    public void setAscending(boolean ascending) {
        m_ascending = ascending;
    }

    /**
     * Returns the number of objects in the queue
     *
     * @return the number of objects in the queue
     */
    public int size() {
        return m_model.size();
    }

    /**
     * A comparator implemenation that compares PQEntrys.
     */
    private class PQComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            PQEntry pq1 = (PQEntry)o1;
            PQEntry pq2 = (PQEntry)o2;

            if (pq1.m_priority < pq2.m_priority) {
                return -1;
            } else if (pq1.m_priority > pq2.m_priority) {
                return 1;
            } else {
                return 0;
            }
        }

        public boolean equals(Object o1, Object o2) {
            return (compare(o1, o2) == 0);
        }
    }

    /**
     * Internal-only class that is used as the data for the TreeSet since
     * we need to quickly associate an object with a priority.  No accessors
     * since it's not used outside this class.
     */
    private class PQEntry {
        public int m_priority;
        public Object m_object;

        public PQEntry(Object object, int priority) {
            m_priority = priority;
            m_object = object;
        }
    }
}

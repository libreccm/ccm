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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// new versioning

/**
 * <p>This data structure is a map that also behaves like a queue.
 * <strong>Warning:</strong> this collection is <em>not</em> synchronized. </p>
 *
 * <p>This data structure is only used by the {@link DiffSet} class.
 * Since rolling back a data object may involve rolling back its versioned
 * compound properties, the diff set class performs a pre-order
 * breadth-first traversal of this graph of objects.  The standard way to
 * implement a BF traversal is to keep a queue of visited nodes.  Since we don't
 * want to visit each node more than once, the queue must "remember" the
 * previously enqueued and dequeued elements, i.e. it must operate as a set.
 * Additionally, the diff set expects the <code>enqueue</code> operation
 * to take oids and the <code>dequeue</code> operation to yield proxy data
 * objects.  For this reason, we need a queue that behaves like a map. </p>
 *
 * <p>To make unit testing easier, the queue map does not know about {@link
 * com.arsdigita.persistence.OID oids} and {@link ProxyDataObject proxy data
 * objects}. Its methods deal with {@link java.lang.Object} instances.  </p>
 *
 * <p>The queue map works like so. Calling {@link #QueueMap()} creates an
 * empty queue.</p>
 * 
 * <p>The only way to add elements to the queue is via the {@link
 * #enqueue(Object key, Object value)} method. You can only enqueue the same
 * <code>key</code> once. Once <code>key</code> is enqueued, subsequent attempts
 * to enqueue the same <code>key</code> will cause an exception.  You can use
 * the {@link #contains(Object)} method to check whether a key has already been
 * enqueued. The rationale for this is to avoid rolling back the same oid
 * twice. This breaks cycles that may exist in the data object graph that is
 * being rolled back.</p>
 * 
 * <p>Assume, for the sake of having a shared visual representation, that the
 * queue is horizontal with enqueued items attached to the right end of the
 * queue and dequeued items coming out of the left end.  If we enqueue foo, bar,
 * and baz, then the internal data structure will look like so: </p>
 * 
 * <pre>
 *    foo    bar    baz
 *    ^
 * </pre>
 * 
 * <p>Where the marker <strong><code>^</code></strong> indicates the item to be
 * yielded by the next call to {@link #dequeue()}.  Once foo is dequeued, the
 * internal data structure looks like so: </p>
 * 
 * <pre>
 *    foo    bar    baz
 *           ^
 * </pre>
 * 
 * <p>In other words, the marker moved to "bar", but we continue to hold
 * "foo" even after it has been "dequeued".  </p>
 * 
 * <p>The queue can be rewound by calling {@link #rewind()}. This moves the
 * marker to the leftmost element. </p>
 * 
 * <pre>
 *    foo    bar    baz
 *    ^
 * </pre>
 * 
 * <p>Aside from {@link #dequeue()}, there exist two other methods for
 * retrieving the <code>value</code> parts of the enqueued <code>(key,
 * value)</code> pairs.  The method {@link #get(Object key)} will return the
 * <code>value</code> which has been previously enqueued with this key.</p>
 * 
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-04-16
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/
final class QueueMap {
    // use ArrayList to make calls to get(int) fast.
    private List m_list = new ArrayList();
    private Map m_map;
    private int m_next;

    public QueueMap() {
        m_list = new ArrayList();
        m_map = new HashMap();
        m_next = 0;
    }

    /**
     * <p>Adds the <code>(key, value)</code> pair to the queue. A key can only
     * be enqueued once.  Subsequent attempts to enqueue a pair whose key has
     * already been enqueued are ignored.</p>
     *
     * @throws AlreadyEnqueuedException if a pair with a key equal to
     * <code>key</code> has already been enqueued.
     **/
    public void enqueue(Object key, Object value)
        throws AlreadyEnqueuedException {

        if ( m_map.containsKey(key) ) throw new AlreadyEnqueuedException(key);

        m_map.put(key, value);
        m_list.add(value);
    }

    /**
     * Dequeues the next value from the queue. Note that the queue map retains a
     * reference to the dequeued object.
     *
     * @return the next value. The object returned is the <code>value</code>
     * object that was previously {@link #enqueue(Object, Object)} - not the
     * <code>key</code> object.
     *
     * @throws NoSuchElementException if there are no more items in the queue
     **/
    public Object dequeue() throws NoSuchElementException {
        if ( !hasNext() ) {
            throw new NoSuchElementException();
        }
        return m_list.get(m_next++);
    }

    /**
     * Returns the <code>value</code> that was previously {@link
     * #enqueue(Object, Object)} for this <code>key</code>. Returns
     * <code>null</code> if the <code>key</code> has not been previously
     * enqueued.
     **/
    public Object get(Object key) {
        return m_map.get(key);
    }

    public boolean hasNext() {
        return m_next < m_list.size();
    }

    /**
     * Rewinds the queue by moving the queue marker to the beginning.
     **/
    public void rewind() {
        m_next = 0;
    }


    /**
     * Returns <code>true</code> if <code>key</code> has been previously {@link
     * #enqueue(Object, Object) enqueued}. Dequeuing the <code>value</code>
     * associated with this <code>key</code> does not affect the return value of
     * this method. The only thing this method checks for is if <code>key</code>
     * has ever been enqueued in this queue map.
     *
     * @see #rewind()
     **/
    public boolean contains(Object key) {
        return m_map.containsKey(key);
    }

    static class AlreadyEnqueuedException extends IllegalArgumentException {
        private String m_msg;

        public AlreadyEnqueuedException(Object key) {
            StringBuffer sb = new StringBuffer();
            sb.append("The key ").append(key);
            sb.append(" has already been enqueued.");
            m_msg = sb.toString();
        }

        public String getMessage() {
            return m_msg;
        }
    }
}

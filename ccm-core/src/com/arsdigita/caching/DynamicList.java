/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.caching;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Linked hash map.  Warning: this implementation is not thread-safe!
 *
 * @since 2001/12/20
 * @version $Revision: #12 $ $DateTime: 2004/08/16 18:10:38 $
 */

// FIXME: This should be replaced with LinkedHashMap when we move to JDK 1.4.
// See:
// http://java.sun.com/j2se/1.4.2/docs/api/java/util/LinkedHashMap.html
class DynamicList {


    private static final Logger s_log = Logger.getLogger(DynamicList.class);

	private Set m_entryRemovalListeners = new HashSet();
	private static Set s_allTableEntryRemovalListeners = new HashSet();
	
    private long    m_count = 0; // linked list size
    private Node    m_head;
    private Node    m_tail;
    private Map     m_map;
    private String  m_cacheID;

    private int m_maxSize;
    // in seconds
    private int m_maxAge;

    public DynamicList(String cacheID, int size, int age) {
        m_maxSize = size;
        setMaxAge(age);
        m_cacheID = cacheID;
        m_map = new HashMap();
    }

    public void clear() {
        m_head  = null;
        m_tail  = null;
        m_count= 0;
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
        	TimeStamped ts = (TimeStamped)it.next();
        	CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, ts.getKey(), ts.getValue(), CacheEntryRemovalEvent.INVALIDATED_ENTRY);
        	notifyEntryRemovalListeners(e); 
        }
        m_map.clear();
    }

    public void setMaxAge(int age) {
        m_maxAge = age;
        s_log.debug("Set max age to " + age);
    }

    public int getMaxAge() {
        return m_maxAge;
    }

    public int getMaxSize() {
        return m_maxSize;
    }

    public void removeLRUEntry() {
    	TimeStamped ts = (TimeStamped) removeTail();
		CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, ts.getKey(), ts.getValue(), CacheEntryRemovalEvent.CACHE_FULL_LRU);
       	notifyEntryRemovalListeners(e);
        String key = ts.getKey();
        m_map.remove(key);
    }

    public void removeLRUEntryIfExpired() {
    	TimeStamped ts = (TimeStamped) removeTailIfExpired();
    	if (ts != null) {
    		CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, ts.getKey(), ts.getValue(), CacheEntryRemovalEvent.CACHE_FULL_LRU);
    		notifyEntryRemovalListeners(e);
    		String key = ts.getKey();
    		m_map.remove(key);
    	}
    }

	protected static void addAllTableEntryRemovalListener(CacheEntryRemovalListener listener) {
		s_allTableEntryRemovalListeners.add(listener);
		
	}
	
	protected void addTableEntryRemovalListener(CacheEntryRemovalListener listener) {
		m_entryRemovalListeners.add(listener);
	}
	
	private void notifyEntryRemovalListeners(CacheEntryRemovalEvent e) {
		s_log.debug("start - notify listeners of removal of item with key " + e.getKey() + " from cache " + e.getCacheID());
		Iterator it = m_entryRemovalListeners.iterator();
		while (it.hasNext()) {
			CacheEntryRemovalListener listener = (CacheEntryRemovalListener)it.next();
			listener.entryRemoved(e);
		}
		it = s_allTableEntryRemovalListeners.iterator();
		while (it.hasNext()) {
			CacheEntryRemovalListener listener = (CacheEntryRemovalListener)it.next();
			listener.entryRemoved(e);
		}
	}
	
    public void put(String key, Object value) {
        if ( key==null ) { throw new NullPointerException("key"); }

        // if cache is full, first try to delete an expired item
        if (m_count >= m_maxSize) {
            s_log.info("Cache " + m_cacheID + " is almost full. current size: " +
                    m_count + "; max size: " + m_maxSize);
            removeLRUEntryIfExpired();
        }

        // make sure cache is not overfilled
        if (m_count >= m_maxSize) {
           s_log.warn("Cache " + m_cacheID + " is full. current size: " +
        		   m_count + "; max size: " + m_maxSize);
            removeLRUEntry();
        }


        Node node = (Node) m_map.get(key);
        // do we have this key already?
        if (node == null) {
            log(m_cacheID, " PUT ", key);
            addHead(new TimeStamped(key, value));
            m_map.put(key, m_head);
        } else {
            log(m_cacheID, "RFRSH", key);
            setElementAt(node, new TimeStamped(key, value));
        }
    }

    public void remove(String key) {
        log(m_cacheID, "REMUC", key);
        Node node = (Node) m_map.remove(key);
        if (node != null) {
            TimeStamped ts = removeElementAt(node);
            CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, key, ts.getValue(), CacheEntryRemovalEvent.INVALIDATED_ENTRY);
       		notifyEntryRemovalListeners(e);
        }

    }

    public boolean removeIfOutdated(String key, int hashCode) {
        Node node = (Node) m_map.get(key);
        boolean removed = false;
        if (node != null) {
            TimeStamped ts = getElementAt(node);
            if (ts.getValue().hashCode() != hashCode) {
                log(m_cacheID, "OUTDT", key);
                removeElementAt(node);
                m_map.remove(key);
                CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, ts.getKey(), ts.getValue(), CacheEntryRemovalEvent.STALE_ENTRY);
               	notifyEntryRemovalListeners(e);
                removed = true;
            } else {
                log(m_cacheID, "VALID", key);
            }
        } else {
            log(m_cacheID, "NOENT", key);
        }

        return removed;
    }

    public Object get(String key) {
        // Remove the node first.  It will be either put back at head
        // because of that LRU thing, or it will be thrown away if the node
        // turns out to be stale.
        Node node = (Node) m_map.remove(key);
        if (node != null) {
            TimeStamped ts = (TimeStamped) getElementAt(node);
            removeElementAt(node);
            //  Check whether the cache got stale
            if (ts.isExpired(m_maxAge)) {
                // destroy map entry
                log(m_cacheID, "STALE", key);
                CacheEntryRemovalEvent e = new CacheEntryRemovalEvent(m_cacheID, key, ts.getValue(), CacheEntryRemovalEvent.STALE_ENTRY);
                notifyEntryRemovalListeners(e);
                
                return null;
            }
            //  Re-add this node at the head.
            m_map.put(key, addHead(ts));
            // mark additional hit
            ts.hit();
            log(m_cacheID, "  HIT", key);
            return ts.getValue();
        }

        log(m_cacheID, " MISS", key);
        return null;
    }

	

    /**
     * @return list size
     */
    public final long size() {
        return m_count;
    }

    /**
     * @param position - the node postion in the linked list
     * @return data/value at the position 'position'
     */
    private TimeStamped getElementAt(Node position) {
        return (position == null ? null : position.m_data);
    }
    
    /**
     * Removes Tail node in the list and returns data stored in the tail
     * @return data/value sored in the old Tail node
     */
    private TimeStamped removeTail() {
        TimeStamped removedData = null;
        if (m_count != 0) {
            removedData = m_tail.m_data;
            m_count --;

            if (m_count == 0) {
                m_head = null;
                m_tail = null;
            } else if (m_count == 1) {
                m_head.m_next = null;
                m_tail = m_head;
            } else {
                m_tail = m_tail.m_prev;
                m_tail.m_next = null;
            }
        }

        return removedData;
    }

    /**
     * Removes Tail node in the list ONLY is it has expired
     * @return data/value sored in the old Tail node
     */
    private TimeStamped removeTailIfExpired() {
    	if (m_count != 0) {
    		if (m_tail.m_data.isExpired(m_maxAge)) {
    			s_log.debug("removing " + m_tail.m_data.getKey() + " because it has expired");
    			return removeTail();
    		}
    	}
    	return null;
    }

    /**
     * Adds new Head node to the list
     * @param newElement - new data to be stored in the Head node
     * @return Position of the new Head node
     */
    private Node addHead(TimeStamped newElement) {
        Node newHead = new Node();

        if (m_count == 0) {
            m_head = newHead;
            m_tail = newHead;
        } else if (m_count == 1) {
            m_head = newHead;
            m_tail.m_prev = m_head;
            m_head.m_next = m_tail;
        } else {
            m_head.m_prev  = newHead;
            newHead.m_next = m_head;
            m_head = newHead;
        }
        newHead.m_data = newElement;
        m_count ++;

        return newHead;
    }

    /**
     * Sets new data in the specified position and returns old value
     * @param position - position where data will be stored
     * @param newValue - new data value of node
     * @return old data value of node
     */
    private Object setElementAt(Node position, TimeStamped newValue) {
        Object oldValue = null;
        if (position != null) {
            oldValue = position.m_data;
            position.m_data = newValue;
        }
        return oldValue;
    }

    /**
     * Removes node at the specified position an returns data value stored in that node
     * @param position - the position of node to be removed
     * @return data value stored in removed node.
     */
    private TimeStamped removeElementAt(Node position) {
        TimeStamped oldValue = null;
        if (position != null && m_count != 0) {
            if (m_count == 1) {
                m_head = null;
                m_tail = null;
            } else if (m_count == 2) {
                m_head = position.m_prev != null ? position.m_prev : position.m_next;
                m_tail = m_head;
                m_head.m_next = m_head.m_prev = m_tail.m_next = m_tail.m_prev = null;
            } else {
                if (position == m_tail) {
                    position.m_prev.m_next = null;
                    m_tail = position.m_prev;
                } else if (position == m_head) {
                    position.m_next.m_prev = null;
                    m_head = position.m_next;
                } else {
                    position.m_prev.m_next = position.m_next;
                    position.m_next.m_prev = position.m_prev;
                }
            }

            m_count--;
            oldValue = position.m_data;
        }

        return oldValue;
    }


    private static void log (String cacheID, String what, String key) {
        if (s_log.isDebugEnabled()) {
            s_log.debug(cacheID + ": " + what + " " + key);
        }
    }


    public Set entrySet() {
        Set set = new HashSet();
        for (Node nn=m_head; nn!=null; nn=nn.m_next) {
            set.add(nn.m_data);
        }
        return set;
    }

    private class TimeStamped implements CacheTable.TimestampedEntry {

        private long m_timestamp;
        private String m_key;
        private Object m_obj;
        private int m_hits;

        TimeStamped(long timestamp, String key, Object obj) {
            if ( key==null ) { throw new NullPointerException("key"); }

            m_timestamp = timestamp;
            m_key = key;
            m_obj = obj;
            m_hits = 1;
        }

        TimeStamped(String key, Object obj) {
            m_timestamp = System.currentTimeMillis();
            m_key = key;
            m_obj = obj;
            m_hits = 1;
        }

        public boolean isExpired(int seconds) {
            return (System.currentTimeMillis() - m_timestamp > seconds * 1000L);
        }
        
        /** mark the item has being hit one more time */
        public void hit() {
        	++m_hits;
        }

        public String getKey() {
            return m_key;
        }

        public Object getValue() {
            return m_obj;
        }

        void setValue(Object obj) {
            m_obj = obj;
        }

        public int getHits() {
            return m_hits;
        }

        /** for debugging only */
        public Date getTimestamp() {
            return new Date(m_timestamp);
        }

        public boolean equals(Object obj) {
            if ( ! (obj instanceof TimeStamped) ) { return false; }

            return getKey().equals(((TimeStamped) obj).getKey());
        }

        public int hashCode() {
            return m_key.hashCode();
        }
    }


    /**
     * List's node class
     */
    static class Node {
        TimeStamped  m_data;
        Node    m_next;
        Node    m_prev;
    }
}

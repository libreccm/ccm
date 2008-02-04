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
package com.arsdigita.util.url;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Helper class for caching fetched URLs. A more advanced implementation of
 * com.arsdigita.util.Cache which rather than being capacity limited in terms
 * of number of stored keys, it is limited according to memory usage.
 */

public class  URLCache {

    private static final Logger s_log = Logger.getLogger(URLCache.class);

    private HashMap m_cache = new HashMap();

    // Maximum cache size in characters. This includes both Site data _and_ URL length
    private long m_maxSize;
    // Default expiration time  for cached items, in milliseconds
    private long m_defaultExpiryTime;

    // Current size of the cache in characters
    private long m_curSize = 0;

    public static final int FIFTEEN_MINUTES = 15*60*1000;

    /**
     * Create a new URLCache. If more elements are put into the cache than it
     * can hold, expired items will be evicted.
     *
     */
    public URLCache() {
        this(10000000, FIFTEEN_MINUTES);
    }
    /**
     * Create a new URLCache with a maximum size of size characters.  If more
     * elements are put into the cache than it can hold, expired items will
     * be evicted.
     *
     * @param size cache size in characters
     */
    public URLCache(long size) {
        this(size, FIFTEEN_MINUTES);
    }

    /**
     * Create a new URLCache with a maximum size of size characters.  If more
     * elements are put into the cache than it can hold, expired items will
     * be evicted.
     * <p>
     * Also allows an expiration time to be set; items in the cache that are
     * older than that time will be evicted.
     *
     * @param size cache size in characters
     * @param expiryTime default expiry time for cached items. When
     * retrieving an item, if its age exceeds expiry time, then it will be
     * discarded.
     */
    public URLCache(long size, long expiryTime) {
        assertCacheSize(size);
        assertExpiryTime(expiryTime);

        this.m_maxSize = size;
        this.m_defaultExpiryTime = expiryTime;
    }
    /**
     *
     * @return The current size of the cache.
     */
    public long getCurrentSize() {
        return m_curSize;
    }

    /**
     * Returns max size - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, expired items are evicted until there is
     * sufficient space for the new item. If this is not enough, items will be
     * randomly evicted.
     *
     * @return max size - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, least recently used items are evicted until
     * there is sufficient space for the new item.
     */
    public long getMaxSize() {
        return m_maxSize;
    };

    /**
     * Utility method to return all URLs currently in the cache.
     */
    public Collection getURLS() {
        LinkedList list = new LinkedList(m_cache.keySet());
        return list;
    }

    /**
     * Sets max size - Maximum memory usage allowed for the data stored in the
     * cache. When exceeded, expired items are evicted until there is
     * sufficient space for the new item. If this is not enough, items will be
     * randomly evicted.
     *
     * Warning: Shrinking the cache size will cause the cache to immediately
     * purge excess entries to maintain the class invariant.
     *
     * @param maxSize - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, least recently used items are evicted until
     * there is sufficient space for the new item.
     *
     * @pre maxSize > 0
     *
     * @post getCurrentSize() <= getMaxSize()
     */
    public synchronized void setMaxSize(long maxSize) {
        assertCacheSize(maxSize);
        final long oldSize = m_maxSize;
        m_maxSize = maxSize;
        if (oldSize > maxSize) {
            makeRoomInCache(0);
        }
    }

    /**
     * Returns default expiry time for cached items. When retrieving an item,
     * if its age exceeds expiry time, then it will be discarded.
     *
     * @return expiry time - default expiry time for cached items. When
     * retrieving an item, if its age exceeds expiry time, then it will be
     * discarded. Default expiry time is 15*60*1000.
     */
    public long getDefaultExpiryTime() {
        return m_defaultExpiryTime;
    };

    /**
     * Sets default expiry time for cached items. When retrieving an item, if
     * its age exceeds expiry time, then it will be discarded.
     *
     * @param defaultExpiryTime - default expiry time for cached items. When
     * retrieving an item, if its age exceeds expiry time, then it will be
     * discarded. Default expiry time is FIFTEEN_MINUTES.
     *
     * @pre defaultExpiryTime > 0
     */
    public synchronized void setDefaultExpiryTime(long defaultExpiryTime) {
        assertExpiryTime(defaultExpiryTime);
        m_defaultExpiryTime = defaultExpiryTime;
    };

    /**
     *  @deprecated use {@link #store(String url, URLData data)}
     */
    public synchronized void store(String url, String data) {
        URLData urlData = new URLData(url);
        urlData.setContent(data.getBytes());
        store(url, urlData);
    }

    /**
     *  Stores data for a url in the cache. Expiry time is the default expiry
     *  time.
     *
     * @param url - URL to be stored in the cache.
     * @param data - data to be stored in the cache
     */
    public synchronized void store(String url, URLData data) {
        store (url, data, m_defaultExpiryTime);
    };


    /**
     *  @deprecated use {@link #store(String url, URLData data, long expiry)}
     */
    public synchronized void store(String url, String data, long expiry) {
        URLData urlData = new URLData(url);
        urlData.setContent(data.getBytes());
        store(url, urlData, expiry);
    }

    /**
     *  Stores data for a url in the cache.
     *
     * @param url - URL to be stored in the cache.
     * @param data - data to be stored in the cache
     * @param expiry - expiry time in milliseconds.
     */
    public synchronized void store(String url, URLData data, long expiry) {
        assertURL(url);
        if (null == data) {
            throw new IllegalArgumentException("Data can be empty, but not null!");
        }

        assertExpiryTime(expiry);

        final long dataSize = data.getContent().length + url.length();
        if (dataSize > m_maxSize) {
            throw new IllegalArgumentException("Cannot store data greater than maximum Cache size: " + m_maxSize +
                    ". URL is " + url.length() + " Data is: " + data.getContent().length);
        }

        s_log.debug("Storing location URL " + url + " in the URLCache.");

        // See if this is replacing an existing URL.
        // If so, purge the data
        if (retrieve(url) != null) {
            purge(url);
        }

        // If no room in cache, make room.
        long newSize = m_curSize + dataSize;
        if (newSize > m_maxSize) {
            makeRoomInCache(dataSize);
        }
        addToCache(url, data, expiry);
    }

    /**
     * Utility method for method store. Makes room in cache for new data.
     * First purges any old data. If there is still not enough room, purges
     * data until there is room.
     *
     * TODO: After purging expired cache entries, perhaps further purges should be
     * ordered by last use or creation time.
     *
     * @param dataSize The size of new data to add.
     */
    private synchronized void makeRoomInCache(final long dataSize) {
        purgeExpired();
        long newSize = m_curSize + dataSize;

        if (newSize > m_maxSize) {
            Iterator iter = m_cache.entrySet().iterator();
            while (newSize > m_maxSize && iter.hasNext()) {
                Map.Entry ent = (Map.Entry)iter.next();
                String entryURL = (String) ent.getKey();
                Entry e2 = (Entry) ent.getValue();
                //s_log.debug("Evicting " + ent.getKey() + " from the URLCache. (Just evicting)");
                final long entrySize = entryURL.length() + e2.data.getContent().length;
                iter.remove();
                m_curSize -= entrySize;
                newSize -= entrySize;
            }
        }

    }

    private synchronized void addToCache(String url, URLData data, long expiry) {
        final long dataSize = data.getContent().length + url.length();
        Assert.assertTrue(m_curSize + dataSize <= m_maxSize);
        Entry e = new Entry(data, System.currentTimeMillis(), expiry);
        m_cache.put(url, e);
        m_curSize += dataSize;
    }


    /**
     * removes a url from the cache
     */
    public synchronized void purge(String url) {
        assertURL(url);
        s_log.debug("Evicting " + url + " from the URLCache.");
        Entry e = (Entry)m_cache.get(url);
        if (e != null) {
            m_cache.remove(url);
            m_curSize = m_curSize-url.length()-e.data.getContent().length;
        }
    };

    /**
     * Purges the entire cache.
     */
    public synchronized void purgeAll() {
        m_cache.clear();
        m_curSize = 0;
    }

    /**
     *  Immediately removes any expired entries from the cache.
     */
    public synchronized void purgeExpired() {
        Iterator iter = m_cache.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry)iter.next();
            String entryURL = (String) ent.getKey();
            Entry e2 = (Entry) ent.getValue();
            if (e2.isExpired()) {
//                s_log.debug("Evicting " + ent.getKey() + " from the URLCache. (Expired)");
                iter.remove();
                m_curSize = m_curSize - entryURL.length()- e2.data.getContent().length;
            }
        }
    }


    /**
     *retrieves a url from the cache, returning null if not present or it has
     *expired.
     * @deprecated use {@link #retrieveData(String url)}
     */
    public String retrieve(String url) {
        URLData data = retrieveData(url);
        if (data != null) {
            return data.getContentAsString();
        } else {
            return null;
        }
    }

    /**
     *retrieves a url from the cache, returning null if not present or it has
     *expired.
     */
    public URLData retrieveData(String url) {
        assertURL(url);
        s_log.debug("Trying to retrieve " + url + " from the URLCache.");

        Entry e = null;
        synchronized(this) {
            e = (Entry)m_cache.get(url);
        }

        if (e == null) return null;
        // make sure the item hasn't expired
        if (e.isExpired()) {
            purge(url);
            // and pretend we never saw it
            return null;
        }

        e.lastUse = System.currentTimeMillis();
        s_log.debug("URL " + url + " is in the cache.");
        return e.data;
    }



    private static void assertExpiryTime(long expiryTime) {
        if (expiryTime < 0) {
            throw new IllegalArgumentException("Expiry time must be non negative: " +  expiryTime);
        }
    }

    private static void assertCacheSize(long size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Cache size must be greater than 0. " + size);
        }
    }

    private static void assertURL(String url) {
        if (StringUtils.emptyString(url)) {
            throw new IllegalArgumentException("URL cannot be empty!");
        }
    }


    /**
     * A single entry in the Cache.  Contains the actual object of
     * interest and the last time the object's been looked up; also
     * contains the object's creation time.
     */
    private final class Entry {
        final URLData data;
        long lastUse;
        final long creationTime;
        final long expiry;

        Entry (URLData data, long lastUse, long expiry) {
            this.data = data;
            this.lastUse = lastUse;
            this.creationTime = System.currentTimeMillis();
            this.expiry = expiry;
        }

        boolean isExpired() {
            final long now = System.currentTimeMillis();
            final boolean expired = expiry > 0 && creationTime < now - expiry;
            return expired;
        }
    }


}

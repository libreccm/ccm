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
package com.arsdigita.caching;

import com.arsdigita.util.SystemProperties;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * <p> Implements simple caching, where storage is limited in both size and age.
 * In case of overflow, the least recently used item is evicted.  There can be
 * any number of <code>CacheTable</code> instances throughout the system,
 * however each must have a unique tag.  This tag is later used by {@link
 * CacheServlet} to synchronize cache between multiple web servers. </p>
 *
 *
 * @author Artak Avetyan
 * @author Matthew Booth
 * @author Sebastian Skracic
 *
 * @version $Revision: #23 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CacheTable {

    private static final Logger s_log = Logger.getLogger(CacheTable.class);

    // this was 20, which was too high for small setups
    // cached Templates were eating 4megs each, so there you go
    public static final int MIN_CACHE_SIZE      =            2;

    public static final int MAX_CACHE_SIZE      =      1000000;
    public static final int MIN_CACHE_AGE       =            5;
    public static final int MAX_CACHE_AGE       =  60*60*24*30;
    public static final int DEFAULT_CACHE_SIZE  =         1000;
    public static final int DEFAULT_CACHE_AGE   =          300;

    private static Map s_caches = new Hashtable();

    private String m_cacheID;
    private boolean m_shared = true;
    private boolean purgeAllowed = false;
    private DynamicList m_list;

    /** For debugging only. */
    public static final Browser BROWSER = new BrowserImpl(s_caches);

    /**
     * <p>Creates cache storage tagged with the passed in identificator.  This
     * tag must be unique in the sense that no other cache table loaded by this
     * <code>CacheTable</code>'s {@link java.lang.ClassLoader class loader} may
     * have the same id.</p>
     *
     * <p>One of the purposes of the id is to serve as a unique identifier for
     * specifying the configurable parameters of this cache table in system
     * configuration files. </p>
     *
     * <p>The configurable parameters are <code>waf.caching.[id].max_size</code>
     * and <code>waf.caching.[id].max_age</code>. </p>
     *
     * @param id Unique identifier for the new storage area
     * @pre id != null
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public CacheTable(final String id) {
        this(id, DEFAULT_CACHE_SIZE, DEFAULT_CACHE_AGE, true);
    }

    /**
     * @param isShared should this cache table be synced with other peers in cluster
     */
    public CacheTable(final String id, boolean isShared) {
        this(id, DEFAULT_CACHE_SIZE, DEFAULT_CACHE_AGE, isShared);
    }

    public CacheTable(final String id, int defSize, int defAge) {
        this(id, defSize, defAge, true);
    }

    /**
     * <p>Creates cache storage tagged with the passed in identificator.  This
     * tag must be unique in the sense that no other cache table loaded by this
     * <code>CacheTable</code>'s {@link java.lang.ClassLoader class loader} may
     * have the same id.</p>
     *
     * <p>One of the purposes of the id is to serve as a unique identifier for
     * specifying the configurable parameters of this cache table in system
     * configuration files. </p>
     *
     * <p>The configurable parameters are <code>waf.util.caching.[id].size</code>
     * and <code>waf.util.caching.[id].age</code>. </p>
     *
     * @param id Unique identifier for the new storage area
     * @param size Initial default size
     * @param age Initial default age
     * @param isShared should this cache table be synced with other peers in cluster
     * @pre id != null
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public CacheTable(final String id, int defSize, int defAge, boolean isShared) {
        if ( id == null ) { throw new NullPointerException("id"); }

        m_cacheID = id;
        m_shared = isShared;

        final Parameter sizeParam = new IntegerParameter
            ("waf.util.caching." + id + ".size",
             Parameter.REQUIRED,
             new Integer(defSize));

        int size = ((Integer) SystemProperties.get(sizeParam)).intValue();
        if (size < MIN_CACHE_SIZE  ||  size > MAX_CACHE_SIZE) {
            s_log.warn("Cache size " + size + " was outside allowed range " +
                       MIN_CACHE_SIZE + "-" + MAX_CACHE_SIZE);
            size = DEFAULT_CACHE_SIZE;
        }

        final Parameter ageParam = new IntegerParameter
            ("waf.util.caching." + id + ".age",
             Parameter.REQUIRED,
             new Integer(defAge));

        int age = ((Integer) SystemProperties.get(ageParam)).intValue();

        m_list = new DynamicList(m_cacheID, size, saneMaxAge(age));

        register(m_cacheID, this);
    }

    /**
    * add a listener that will be notified when entries are removed from
    * this CacheTable (removal includes specific invalidation and
    * attempts to retrieve an entry that is stale)
    * @param listener
    */
    public void addEntryRemovalListener(CacheEntryRemovalListener listener) {
         m_list.addTableEntryRemovalListener(listener);
    }

    /**
    * add a listener that will be notified when entries are removed from any
    * CacheTable (removal includes specific invalidation and
    * attempts to retrieve an entry that is stale)
    * @param listener
    */
    public void addAllTableEntryRemovalListener(CacheEntryRemovalListener listener) {
         DynamicList.addAllTableEntryRemovalListener(listener);
    }


    private static void register(String id, CacheTable cache) {
        if ( s_caches.containsKey(id) ) {
            throw new IllegalArgumentException
                ("There already exists a CacheTable with the \"id\" of " +
                 id + ": " + s_caches.get(id));
        }
        s_caches.put( id, cache );
    }

    static CacheTable getCache( String id ) {
        return (CacheTable) s_caches.get( id );
    }

    /**
     *  <p> Returns the maximum cache item age (in seconds). </p>
     */
    public int getMaxAge() {
        return m_list.getMaxAge();
    }

    /**
     *  <p> Sets the maximum cache item age (in seconds). </p>
     *
     * @param age desired maximum cache size in seconds
     */
    public synchronized void setMaxAge(int age) {
        m_list.setMaxAge(saneMaxAge(age));
    }

    private int saneMaxAge(int age) {
        if (age < MIN_CACHE_AGE  ||  age > MAX_CACHE_AGE) {
            s_log.warn("Cache age " + age + " was outside allowed range " +
                       MIN_CACHE_AGE + "-" + MAX_CACHE_AGE);
            return DEFAULT_CACHE_AGE;
        } else {
            return age;
        }
    }

    /**
     *  Returns the actual number of items in cache.
     */
    public long getCurrentSize() {
        return m_list.size();
    }

    int getMaxSize() {
        return m_list.getMaxSize();
    }

    public boolean isShared() {
        return m_shared;
    }
    
    public void setPurgeAllowed(boolean purgeAllowed) {
        this.purgeAllowed = purgeAllowed;
    }

   
    public boolean isPurgeAllowed() {
        return purgeAllowed;
    }
    
    /**
     * A convenience wrapper around {@link #put(String, Object)}.
     *
     * @param key BigDecimal serving as a key for cache lookup
     *
     * @param value Object we're storing in cache
     */
    public synchronized void put(BigDecimal key, Object value) {
        put(key.toString(), value);
    }

    /**
     * <p> Puts object in a cache storage.  Object must be identified with a
     * unique key. </p>
     *
     * <p><em>Implementation note</em>: Any <code>put</code> operation on the
     * <code>CacheTable</code> invokes the {@link #remove(String)} method which
     * will invalidate that cache entry (if exists) on other nodes.  Had the
     * implementation failed to do so, the following scenario might have caused
     * cache incoherence.</p>
     *
     * <ul>
     *   <li>A cache table entry <code>("key1", value1)</code> exists on nodes X
     *   and Y.</li>
     *
     *   <li>Immediately after <code>"key1"</code> is evicted on X, it is
     *   reinserted into the same cache table with a different value, so that
     *   the entry on X is now <code>("key1", value2)</code>.</li>
     *
     *   <li>Nodes X and Y now have different values mapped to the
     *     same key. </li>
     * </ul>
     *
     * <p>To prevent this situation from happening, any insertion must first be
     * preceded by a removal of the affected entry from all peer nodes.</p>
     *
     * <p>As a performance optimization, we try to be clever and prevent valid
     * objects from being flushed out unnecessarily from peer nodes in the above
     * scenario.  This is accomplished by including in the "remove" message the
     * hash code of the object that may need removal from peer nodes.  If the
     * hash code sent by node X matches the hash code of the object mapped to
     * the same key on node Y, then the cache entry maintained by Y need not be
     * updated.</p>
     *
     * <p>Note that for this method to work, any cached object <em>O</em> must
     * hash to the same value regardless of the node on which the hash code is
     * computed.  The contract of {@link java.lang.Object#hashCode()} method
     * makes no such guarantees.  It is unreasonable to expect that hash codes
     * of the "same object" (for some reasonable definition of "sameness") are
     * always identical across different JVMs.  Even if you are running the same
     * JVM version on each of the participating nodes, hash codes of the "same
     * object" are not guaranteed to be identical (although in practice, they
     * often are). As a trivial illustration, consider the following
     * example.</p>
     *
     * <pre>
     * public final class QuuxEnum {
     *     public final static QuuxEnum QUUX1 = new QuuxEnum();
     *     public final static QuuxEnum QUUX2 = new QuuxEnum();
     * }
     * </pre>
     *
     * <p>The hash code of <code>QuuxEnum.QUUX1</code> is virtually guaranteed
     * to be different across peer nodes.</p>
     *
     * <p>Despite its seeming hokeyness, this approach works reasonably well in
     * practice.</p>
     *
     * @param key String serving as a key for cache lookup
     *
     * @param value Object we're storing in cache
     */
    public synchronized void put(String key, Object value) {
        m_list.put(key, value);
        final int hashCode = value.hashCode();

        if (s_log.isDebugEnabled()) {
            List trace = StringUtils.getStackList(new Throwable());
            final String caller = (String) trace.get(2);
            s_log.debug("Put key " + key + " in cache table " + m_cacheID
                        + " called at " +
                        caller + ", object is of type " + value.getClass() +
                        " with hash code: " + hashCode);
        }
        // If peer webservers don't contain the latest value,
        // remove this entry from their caches.
        if (m_shared) {
            CacheServlet.removeOutdatedFromPeers(m_cacheID, key, hashCode);
        }
    }

    public synchronized void removeAll() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("removeAll from cache table " + m_cacheID);
        }
        m_list.clear();
    }

    /**
     *  <p> Removes the object from cache.  Actually a wrapper for
     * {@link #remove(String,String)}. </p>
     *
     * @param key key of the object we're removing from cache
     */
    public void remove(String key) {
        if (m_shared) {
            remove(m_cacheID, key);
        } else {
            removeLocally(key);
        }
    }

    /**
     *  <p> Removes the object from cache.  Actually a wrapper for
     * {@link #remove(String,String)}. </p>
     *
     * @param key key of the object we're removing from cache
     */
    public void remove(BigDecimal key) {
        remove(key.toString());
    }

    /**
     *  <p> Static method which removes object from cache.  It is necessary
     * for implementing the {@link CacheServlet coherent caching}, since
     * it allows "outsiders" to invalidate (purge) certain objects from
     * cache. </p>
     *
     * @param id Unique identificator of cache storage
     *
     * @param key (BigDecimal) key of the object we're removing from cache
     */
    public static void remove(String id, BigDecimal key) {
        remove(id, key.toString());
    }

    /**
     *  <p> Static method which removes object from cache.  It is necessary
     * for implementing the {@link CacheServlet coherent caching}, since
     * it allows "outsiders" to invalidate (purge) certain objects from
     * cache. </p>
     *
     * @param id Unique identificator of cache storage
     *
     * @param key key of the object we're removing from cache
     */
    public static void remove(String id, String key) {
        CacheServlet.remove(id, key);
    }


    /**
     *  Unconditionally removes the entry from the local cache.
     *  This is meant to be invoked only from CacheServlet class.
     */
    synchronized void removeLocally(final String key) {
        m_list.remove(key);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removed key " + key + " from cache table " + m_cacheID);
        }
    }

    /**
     *  If the passed in hashCode doesn't match the that of local cache entry's,
     *  remove the cache entry locally.
     *  This is meant to be invoked only from CacheServlet class.
     */
    synchronized void removeLocallyIfOutdated(final String key, final int hashCode) {
        final boolean removed = m_list.removeIfOutdated(key, hashCode);

        if (s_log.isDebugEnabled()) {
            String msg;
            if (removed) {
                msg = "Removed ";
            } else {
                msg = "Didnt' remove ";
            }
            msg += " entry with key " + key + " hash " + hashCode;
            s_log.debug(msg);
        }
    }


    public synchronized void removeAllEntriesLocally() {
        m_list.clear();
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("removed all entries from cache table " + m_cacheID);
        } 
    }
    
    public static void removeAllCacheTables() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("remove all entries from all purge-able cache tables");
        }
        removeAllCacheTablesLocally();
        CacheServlet.removeAllFromPeers();
    }
        
    /**
     *  Iterator over all CacheTables in the cache 
     *  and clear all purge-able ones
     */
    public static synchronized void removeAllCacheTablesLocally() {
        for(Iterator it = s_caches.values().iterator(); it.hasNext(); ) {
            CacheTable ct = (CacheTable)it.next();
            if (ct.isPurgeAllowed()) {
                ct.removeAll();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("removed all entries from cache table " + ct.m_cacheID);
                }                
            }
        }
    }
    
    
    /**
     *  <p> Retrieves the object stored in cache.  If no object by the
     * passed key can be found in cache (maybe because it's expired or
     * it's been explicitly removed), null is returned. </p>
     *
     * @param key key of the object we're retrieving from cache
     *
     * @return Object stored in cache under key key
     */
    public synchronized Object get(BigDecimal key) {
        return get(key.toString());
    }

    /**
     *  <p> Retrieves the object stored in cache.  If no object by the
     * passed key can be found in cache (maybe because it's expired or
     * it's been explicitly removed), null is returned. </p>
     *
     * @param key key of the object we're retrieving from cache
     *
     * @return Object stored in cache under key key
     */
    public synchronized Object get(String key) {
        return m_list.get(key);
    }
    
    public interface TimestampedEntry {
        String getKey();
        Object getValue();
        Date getTimestamp();
        int getHits();
        boolean isExpired(int seconds);
    }

    public interface Browser {
        Set getTableIDs();
        int getMaxSize(String tableID);
        long getCurrentSize(String tableID);
        int getMaxAge(String tableID);
        Set getEntrySet(String tableID);
        String isShared(String tableID);
        boolean isPurgeAllowed(String tableID);
        void purge(String tableID);
        void purgeAll();
    }

    private static class BrowserImpl implements Browser {
        private final Map m_tableMap;

        BrowserImpl(Map tableMap) {
            if ( tableMap==null ) { throw new NullPointerException(); }
            m_tableMap = tableMap;
        }

        public Set getTableIDs() {
            return new HashSet(m_tableMap.keySet());
        }

        public int getMaxSize(String tableID) {
            return getCacheTable(tableID).getMaxSize();
        }

        public long getCurrentSize(String tableID) {
            return getCacheTable(tableID).getCurrentSize();
        }

        public int getMaxAge(String tableID) {
            return getCacheTable(tableID).getMaxAge();
        }

        public String isShared(String tableID) {
            return getCacheTable(tableID).isShared() ? "yes" : "no";
        }

        public boolean isPurgeAllowed(String tableID) {
            return getCacheTable(tableID).isPurgeAllowed();
        }
        
        public void purge(String tableID) {
            CacheTable table = getCacheTable(tableID);
            if (!table.isPurgeAllowed()) {
                throw new RuntimeException("Table "+tableID+" can't be purged.");
            }
            table.removeAll();
        }
        
        public void purgeAll() {
            CacheTable.removeAllCacheTables();
        }
        
        private static CacheTable getCacheTable(String tableID) {
            if ( tableID==null ) { throw new NullPointerException(); }

            CacheTable table = CacheTable.getCache(tableID);

            if ( table==null ) {
                throw new IllegalArgumentException
                    ("no such table: " + tableID);
            }

            return CacheTable.getCache(tableID);
        }

        public Set getEntrySet(String tableID) {
            return getCacheTable(tableID).m_list.entrySet();
        }
    }
}

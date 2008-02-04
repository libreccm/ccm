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
package com.arsdigita.web;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.TransactionListenerImpl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Maintains a mapping of URL fragments to objects.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-01-14
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public abstract class PathMapCache {
    private final static Logger s_log = Logger.getLogger(PathMapCache.class);

    private final CacheTable m_table;

    private static ThreadLocal s_refreshing = new ThreadLocal() {
            protected Object initialValue() {
                return Boolean.FALSE;
            }
        };


    /**
     * @see CacheTable#CacheTable(String)
     * @pre cacheID != null
     * @throws IllegalArgumentException if <code>cacheID</code> is not a
     * globally unique identifer.
     **/
    protected PathMapCache(String cacheID) {
        m_table = new CacheTable(cacheID);
    }

    /**
     * Returns the object mapped to the {@link #normalize(String) normalized}
     * <code>path</code> or its longest possible subpath. May return
     * <code>null</code>.
     *
     * <p>If no object is cached in memory for this <code>path</code> or any of
     * its subpaths, this method will try to retrieve the object by delegating
     * to {@link #retrieve(String)}. If retrieval returns a non-null object, it
     * will be cached in memory so that subsequent calls to <code>get</code> do
     * not have to call <code>retrieve</code> again. </p>
     *
     * @pre path != null && path.startsWith("/")
     **/
    protected final Object get(final String path) {
        if ( s_log.isDebugEnabled() ) { s_log.debug("path: " + path); }

        final String normalizedPath = normalize(path);
        if ( s_log.isDebugEnabled() ) {
            s_log.debug("normalizedPath=" + normalizedPath);
        }

        final Iterator fragments = new LongestMatch(normalizedPath);

        boolean found = false;
        boolean foundOnFirstTry = true;
        Object result = null;
        while ( !found && fragments.hasNext() ) {
            final String fragment = (String) fragments.next();

            result = m_table.get(fragment);
            if ( result==null ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("cache miss for " + fragment);
                }
                result = retrieve(fragment);
                if ( result!=null ) {
                    m_table.put(fragment, result);
                }
                if ( s_log.isDebugEnabled() ) {
                    s_log.debug("db " + (result==null ? "miss" : "hit") + " for " +
                                fragment);
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("cache hit for " + fragment);
                }
            }

            found = result!=null;

            if ( found && !foundOnFirstTry ) {
                if ( s_log.isDebugEnabled() ) {
                    s_log.debug("caching normalized path: " + normalizedPath);
                }
                m_table.put(normalizedPath, result);
            }

            foundOnFirstTry = false;
        }
        return result;
    }

    protected boolean isCached(String path) {
        return m_table.get(normalize(path)) != null;
    }

    protected void put(String path, Object value) {
        m_table.put(path, value);
    }

    protected void clearAll() {
        m_table.removeAll();
    }

    /**
     * Sets up and tears down the context in which {@link #refresh()} can be
     * run.  If you need to update the cache after one of your previously cached
     * (persistent) objects changes, then you should implement {@link
     * #refresh()} and call <code>refreshAfterCommit</code> in your persistent
     * object's {@link com.arsdigita.domain.DomainObject#beforeSave()} and/or
     * {@link com.arsdigita.domain.DomainObject#afterDelete()} methods.
     **/
    protected void refreshAfterCommit() {
        s_log.debug("entering refreshAfterCommit");

        // No need to register another listener, if this thread is already
        // scheduled to refresh the cache
        if (s_refreshing.get().equals(Boolean.TRUE)) {
            return;
        }

        s_log.debug("scheduling site node cache refresh after commit");
        s_refreshing.set(Boolean.TRUE);
        Session session = SessionManager.getSession();
        TransactionContext txn = session.getTransactionContext();

        // Note this listener is automatically unregistered after being called,
        // so there are no infinite recursion issues here.
        txn.addTransactionListener(new TransactionListenerImpl() {
                public void afterCommit(TransactionContext txn) {
                    s_refreshing.set(Boolean.FALSE);

                    txn.beginTxn();

                    try {
                        refresh();
                    } catch (RuntimeException t) {
                        txn.abortTxn();
                        throw t;
                    }

                    txn.commitTxn();
                }

                public void beforeAbort(TransactionContext txn) {
                    return;
                }

                public void afterAbort(TransactionContext txn) {
                    s_refreshing.set(Boolean.FALSE);
                }
            });
        s_log.debug("exiting refreshAfterCommit");
    }


    // This method accounts for differences in the path handling policies
    // between BaseDispatcher and SiteNode. BaseDispatcher appends a slash, if
    // it's missing, while SiteNode strips off the terminating path fragment, if
    // it doesn't end in a slash.
    /**
     * Given a path like <code>"/foo/bar/baz/quux"</code>, returns either
     * <code>"/foo/bar/baz/"</code> or <code>"/foo/bar/baz/quux/"</code>.
     *
     * @pre path != null && path.startsWith("/")
     * @post return.endsWith("/")
     **/
    protected abstract String normalize(String path);

    /**
     * This method is called when <code>PathMapCache</code> cannot find a cached
     * value mapped to this <code>path</code>.  If the returned value is not
     * null, it will be cached.  Therefore, care must be taken to ensure that
     * the value is cacheable across transactions.  (For example, if you the
     * value returned by this method is a <code>DataObject</code>, it must be
     * disconnected before being returned.)
     **/
    protected abstract Object retrieve(String path);


    protected abstract void refresh();


    /**
     * Given a slash-separated path like <code>"/foo/bar/baz/quux/"</code>, this
     * iterator returns the following path fragments (in this order):
     * <code>"/foo/bar/baz/quux/"</code>, <code>"/foo/bar/baz/"</code>,
     * <code>"/foo/bar/"</code>, <code>"/foo/"</code>, and <code>"/"</code>.
     *
     * <p>The returned iterator does not support {@link Iterator#remove()}.</p>
     *
     * @pre path != null && path.startsWith("/") && path.endsWith("/");
     * @throws NullPointerException if <code>path</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>path</code> does not begin with
     * and end in slash.
     **/
    // package-scoped for whitebox testing
    static class LongestMatch implements Iterator {
        private final static String SL = "/";

        private final String m_path;

        // this slides back iterating over indexes of '/'
        private int m_last;

        public LongestMatch(String path) {
            if ( path == null ) { throw new NullPointerException("path"); }
            if ( !path.startsWith(SL) ) {
                throw new LongestMatchException
                    ("path should start with /: " + path);
            }
            if ( !path.endsWith(SL) ) {
                throw new LongestMatchException
                    ("path should end with /: " + path);
            }
            m_path = path;
            m_last = m_path.length() - 1;
        }

        public boolean hasNext() {
            return m_last > -1;
        }

        public Object next() {
            if ( !hasNext() ) {
                throw new NoSuchElementException
                    ("initial path=" + m_path + "; current idx=" + m_last);
            }
            try {
                return m_path.substring(0, m_last+1);
            } finally {
                m_last = m_path.lastIndexOf('/', m_last-1);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // This is intended for whitebox testing only, so we don't catch
    // IllegalArgumentExceptions that are not explicitly thrown by LongestMatch.
    static class LongestMatchException extends IllegalArgumentException {
        LongestMatchException(String msg) {
            super(msg);
        }
    }
}

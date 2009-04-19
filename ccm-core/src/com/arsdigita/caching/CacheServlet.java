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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Host;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *  <p> A simple servlet which accepts the cache notifications issued by other
 * webserver to implement <em>coherent caching</em>.  Since there can be any
 * number of webservers attached to the same database instance, we must make
 * sure that caches among different webservers contain either the same,
 * consistent value for a particular cache entry, or no value at all. </p>
 *
 *  <p> Whenever a {@link CacheTable#put(String,Object)} is invoked, a HTTP
 * request is sent to all the other JVMs running this service, (except the
 * current server of course).
 *
 *  <p> The HTTP notification for newly added cache entry carries the hashcode
 * of the new entry, so that the peer caches can decide whether the entry they
 * already have (if at all) is outdated or not.  For
 * {@link CacheTable#remove(String)} invocations, no hash code is produced, and
 * peer caches will remove this item unconditionally. </p>
 *
 * @author Matthew Booth
 * @author Sebastian Skracic
 *
 * @version $Revision: #19 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CacheServlet extends HttpServlet {
    private static final Logger s_log =
        Logger.getLogger( CacheServlet.class );

    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String HASH = "hash";
    private static final String REMOVEALL = "removeAll";

    // If you change this, make sure that web.xml is changed as well
    static final String SERVLET_URL = "/expireCache";

    /**
     *  This is executed when foreign server asked us to drop an entry
     * from our cache.  Make sure that we don't end up in recursion.
     */
    protected void doGet( HttpServletRequest req, HttpServletResponse res ) {
        String id = req.getParameter( ID );
        String key = req.getParameter( KEY );     
        String removeAll = req.getParameter( REMOVEALL );
        
        if (s_log.isInfoEnabled()) {
            s_log.info("Got remove request from " + req.getRemoteHost());
        }

        if (id != null && key != null){
          //normal expire cache entry request
          if (s_log.isInfoEnabled()) {
              s_log.info("Got remove request from " + req.getRemoteHost());
          }  
            
          String hash = req.getParameter( HASH );
          expireCacheEntry(id, key, hash);
        } else if (id != null && key == null && removeAll != null) {
           //purge a single cache request
           if (s_log.isInfoEnabled()) {
              s_log.info("Got remove all entries request from " + req.getRemoteHost());
           } 
           if(removeAll.equals("true")){ 
               removeCacheEntries(id); 
           }
        } else if (id == null && key == null && removeAll != null) {
            //purge all caches request
            if (s_log.isInfoEnabled()) {
                s_log.info("Got remove all cache request from " + req.getRemoteHost());
            }
            if(removeAll.equals("true")){ 
                removeAllCache(); 
            }
        } else {
            s_log.error("Got an invalid cache request from " + req.getRemoteHost());
        }
    }
    
    protected void expireCacheEntry(String id, String key, String hash) {
        id = URLDecoder.decode(id);
        key = URLDecoder.decode(key);

        final CacheTable cache = CacheTable.getCache( id );
        if (cache == null) {
            s_log.debug("No cache with id " + id);
            return;
        }

        s_log.debug("Removing " + key + " from cache " + id);

        final Integer hashCode = getHashCode(hash);
        if (hashCode == null) {
            // unconditionally remove
            cache.removeLocally(key);
        } else {
            cache.removeLocallyIfOutdated(key, hashCode.intValue());
        }
    }

    protected void removeCacheEntries(String id) {
        id = URLDecoder.decode(id);
        final CacheTable cache = CacheTable.getCache( id );
        if (cache == null) {
            s_log.debug("No cache with id " + id);
            return;
        }

        s_log.debug("Removing all entries from cache " + id);
        // unconditionally remove
        cache.removeAllEntriesLocally();
    }
    
    protected void removeAllCache() {
        s_log.debug("Removing all Cache tables");
        // unconditionally remove all
        CacheTable.removeAllCacheTablesLocally();
    }
    
    private Integer getHashCode(final String hash) {
        if (hash == null) {
            return null;
        }
        Integer hashCode = null;
        try {
            hashCode = new Integer(hash);
        } catch (NumberFormatException nfe) {
            // just ignore and pretend that no hash value was supplied at all
            s_log.warn("format exception on hash " + hash + " : " + nfe.getMessage() );
        }
        return hashCode;
    }


    /**
     *  Complete removal - first get rid of entry in local cache,
     * then annoy other servers.
     */
    static void remove( String cache_id, String key ) {

        CacheTable cache = CacheTable.getCache( cache_id );
        if (cache == null) {
            return;
        }

        cache.removeLocally(key);
        removeFromPeers(cache_id, key);
    }

 
    /**
     * remote all entries from all purge-able tables in the peer's.
     * 
     * The fact that there is no ID parameter tells the 
     * peers to purge all cache tables. 
     */
    static void removeAllFromPeers() {
        final ParameterMap params = new ParameterMap();
        params.setParameter(REMOVEALL, "true");
        
        notifyPeers(params);
    }
    
    /**
     * remote all entries from the peer's cache table with an id of cacheID.
     * 
     * @param cacheID id of the cache table to purge
     */
    static void removeAllEntriesFromPeersTable(String cacheID) {
        final ParameterMap params = new ParameterMap();
        params.setParameter(ID, cacheID);
        params.setParameter(REMOVEALL, "true");
        
        notifyPeers(params);
    }
    
    /**
     *  Sometimes we need to remove entries only from peer webservers.
     */
    static void removeFromPeers(String cache_id,
                                String key) {
        notifyPeers(cache_id, key, null);
    }

    /**
     *  Notifies peers on adding a new cache entry.  Deletes the peer's cache entry
     * if its contain the object with hashcode not matching <tt>newHashCode</tt>.
     */
    static void removeOutdatedFromPeers(String cache_id,
                                        String key,
                                        int newHashCode) {
        notifyPeers(cache_id, key, String.valueOf(newHashCode));
    }

    /**
     *  Sends "GET /expireCache?" + params to all peer webservers.
     */
    private static void notifyPeers(final String id,
                                    final String key,
                                    final String hash) {    
        notifyPeers(makeParameterMap(id, key, hash));
    }
    
    private static void notifyPeers(ParameterMap params) {
        if (!Web.getConfig().getDeactivateCacheHostNotifications()) {
            s_log.debug("about to notify peers");
            final Session session = SessionManager.getSession();
            if (session == null) {
                s_log.debug("Server is bootstrapping, disabling peer notification");
                return;
            }
            final DomainCollection hosts = Host.retrieveAll();
            final HttpHost current = Web.getConfig().getHost();
            Filter f = hosts.addFilter(" not ( " + Host.SERVER_NAME + " = :currName "
                                       + " and " + Host.SERVER_PORT + " = :currPort )");
            f.set("currName", current.getName());
            f.set("currPort", new Integer(current.getPort()));
            while (hosts.next()) {
                final Host host = (Host) hosts.getDomainObject();
                notifyPeer(host, params);
            }
        }
    }

    private static void notifyPeer(Host host, ParameterMap params) {
        final String url = "http://" + host + SERVLET_URL + params;

        try {
            s_log.debug("sending notification to " + url);
            java.net.URL netURL = new java.net.URL(url);
            new Thread(new HTTPRequester(netURL)).start();
        } catch(MalformedURLException e) {
            s_log.error("malformed URL: " + url);
        }
    }

    private static ParameterMap makeParameterMap(final String id,
                                                 final String key,
                                                 final String hash) {
        final ParameterMap params = new ParameterMap();

        params.setParameter(ID, id);

        if (key != null) {
            params.setParameter(KEY, key);
        }

        if (hash != null) {
            params.setParameter(HASH, hash);
        }

        return params;
    }

    private static class HTTPRequester implements Runnable {
        private static final Logger s_log =
            Logger.getLogger( HTTPRequester.class );

        private final java.net.URL m_url;

        public HTTPRequester(java.net.URL url) {
            m_url = url;
        }

        public void run() {
            try {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Sending invalidate to " + m_url);
                }
                m_url.openStream();

                // XXX check status is 200, or rather, not an error code
            } catch (IOException e) {
                s_log.warn("Failure sending cache invalidate: " + m_url, e);
            }
        }
    }
}


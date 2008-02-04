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
package com.arsdigita.search;

import com.arsdigita.domain.GlobalObserver;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import com.arsdigita.search.lucene.Registry;

import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

// This class listens for global save / delete events
// and schedules a search index update in the beforeCommit
// event of the transaction.
class SearchObserver implements GlobalObserver {

    public static final String SEARCH_SAVE_ATTR = 
        SearchObserver.class.getName() + ".onSave";
    public static final String SEARCH_DELETE_ATTR = 
        SearchObserver.class.getName() + ".onDelete";

    private static final String SEARCH_TXN_ATTR = 
        SearchObserver.class.getName() + ".listener";

    private static final Logger s_log = 
        Logger.getLogger(SearchObserver.class);

    public boolean shouldObserve(DomainObject dobj) {
        MetadataProvider provider = MetadataProviderRegistry
            .findAdapter(dobj.getObjectType());
        if (provider != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Requesting observation for " + dobj.getOID());
            }
            return true;
        }
        
        // Legacy lucene support
        if (Search.getConfig().isLuceneEnabled()) {
            Registry reg = Registry.getInstance();
            if (reg.hasAdapter(dobj.getObjectType())) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Requesting legacy lucene observation for " + 
                                dobj.getOID());
                }
                return true;
            }
        }
        
        // Legacy intermedia support
        if (Search.getConfig().isIntermediaEnabled()) {
            if (dobj instanceof com.arsdigita.search.intermedia.Searchable) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Requesting legacy intermedia observation for " + 
                                dobj.getOID());
                }
                return true;
            }
        }

        if( s_log.isDebugEnabled() ) {
            s_log.debug("No adapter for " + dobj.getOID());
        }

        return false;
    }


    
    // The following events aren't needed
    public void set(DomainObject dobj,
                    String name,
                    Object old_value,
                    Object new_value) {}
    public void add(DomainObject dobj,
                    String name, 
                    DataObject dataObject) {}
    public void remove(DomainObject dobj,
                       String name, 
                       DataObject dataObject) {}
    public void clear(DomainObject dobj, 
                      String name) {}
    public void beforeSave(DomainObject dobj) throws PersistenceException {}
    public void beforeDelete(DomainObject dobj) throws PersistenceException {}

    
    // The two events that trigger updates
    public void afterSave(DomainObject dobj) throws PersistenceException {
        if (Search.getConfig().getLazyUpdates()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Scheduling onSave event for " + dobj.getOID());
            }
            
            registerListener();
            Set saveSet = getAttributeSet(SEARCH_SAVE_ATTR);
            Set deleteSet = getAttributeSet(SEARCH_DELETE_ATTR);

            if (deleteSet.contains(dobj)) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Not scheduling onSave event for deleted object" + 
                               dobj.getOID());
                }
                return;
            }

            saveSet.add(dobj);
        } else {
            DocumentObserver observer = Search.getConfig().getObserver();
            if (observer == null) {
                s_log.warn("No document observer configured for indexer " + 
                           Search.getConfig().getIndexer());
                return;
            }
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Invoking onSave event for " + dobj.getOID());
            }
            
            observer.onSave(dobj);
        }
    }

    public void afterDelete(DomainObject dobj) throws PersistenceException {
        if (Search.getConfig().getLazyUpdates()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Scheduling onDelete event for " + dobj.getOID());
            }
            
            registerListener();
            Set saveSet = getAttributeSet(SEARCH_SAVE_ATTR);
            Set deleteSet = getAttributeSet(SEARCH_DELETE_ATTR);

            if (saveSet.contains(dobj)) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Removing onSave event for deleted object " + 
                               dobj.getOID());
                }
                saveSet.remove(dobj);
            }

            deleteSet.add(dobj);
        } else {
            DocumentObserver observer = Search.getConfig().getObserver();
            if (observer == null) {
                s_log.warn("No document observer configured for indexer " + 
                           Search.getConfig().getIndexer());
                return;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Invoking onDelete event for " + dobj.getOID());
            }
                        
            observer.onDelete(dobj);
        }
    }
    

    // Returns the attribute map for the search observer.
    // Creates one if it doesn't exist
    private Set getAttributeSet(String sAttr) {
        TransactionContext ctx = SessionManager.getSession()
            .getTransactionContext();
        
        // Use a set to eliminate duplicates
        Set set = (Set)ctx.getAttribute(sAttr);
        if (set == null) {
            set = new HashSet();
            ctx.setAttribute(sAttr, set);
        }

        return set;
    }

    // Checks if the SearchTransactionListener is added.  Adds one if not
    private synchronized void registerListener() {
        TransactionContext ctx = SessionManager.getSession().getTransactionContext();
        String sTxn = (String)ctx.getAttribute(SEARCH_TXN_ATTR);

        if (sTxn == null) {
            //listener isn't added, so add it
            ctx.addTransactionListener(new SearchTransactionListener());
            ctx.setAttribute(SEARCH_TXN_ATTR, SEARCH_TXN_ATTR);
        }
    }
}

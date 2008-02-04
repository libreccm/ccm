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

import java.util.Set;
import java.util.Iterator;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.TransactionListener;

import org.apache.log4j.Logger;


// Invokes the search engine specific updater
class SearchTransactionListener implements TransactionListener {
    
    private static final Logger s_log = 
        Logger.getLogger(SearchTransactionListener.class);

    // Perform seach index updates, if any
    public void beforeCommit(TransactionContext txn) 
        throws PersistenceException {
        
        DocumentObserver observer = Search.getConfig().getObserver();
        
        if (observer == null) {
            s_log.warn("No document observer configured for indexer " + 
                       Search.getConfig().getIndexer());
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running beforeCommit event in search transaction listener");
        }
        
        Set toSave = (Set)txn.getAttribute(SearchObserver.SEARCH_SAVE_ATTR);
        if (toSave != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Save set has " + toSave.size() + " entries");
            }
            Iterator iter = toSave.iterator();
            while (iter.hasNext()) {
                DomainObject dobj = (DomainObject)iter.next();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Search save for object with OID " + 
                                dobj.getOID());
                }
                
                observer.onSave(dobj);
            }
        }

        Set toDelete = (Set)txn.getAttribute(SearchObserver.SEARCH_DELETE_ATTR);
        if (toDelete != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Delete set has " + toDelete.size() + " entries");
            }
            Iterator iter = toDelete.iterator();
            while (iter.hasNext()) {
                DomainObject dobj = (DomainObject)iter.next();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Search delete for object with OID " + 
                                dobj.getOID());
                }
                
                observer.onDelete(dobj);
            }
        }
    }
    
    public void afterCommit(TransactionContext txn) {}
    public void beforeAbort(TransactionContext txn) {}
    public void afterAbort(TransactionContext txn) {}
}

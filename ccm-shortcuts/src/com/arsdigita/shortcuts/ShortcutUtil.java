/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.shortcuts;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ShortcutUtil {

    private static final Logger s_log = Logger.getLogger(ShortcutUtil.class);

    private static CacheTable s_remote = new CacheTable("shortcuts");
    private static Map s_cache = new HashMap();
    
    public static String getTarget(String key) {
        Object val = s_remote.get("shortcuts");
        if (val == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Cache is empty");
            }
            repopulateCache();
        }
        return (String)s_cache.get(key.toLowerCase());
    }

    public static void repopulateShortcuts() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Trigger repopulate");
        }
        s_remote.remove("shortcuts");
        repopulateCache();
    }

    private static synchronized void repopulateCache() {
        Object val = s_remote.get("shortcuts");
        if (val != null) {
            return;
        }

        TransactionContext txn = SessionManager.getSession().getTransactionContext();
        boolean doTxn = !txn.inTxn();
        
        if (doTxn) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Starting DB transaction");
            }
            txn.beginTxn();
        }
        
        try {
            s_remote.put("shortcuts", "shortcuts");
            if (s_log.isDebugEnabled()) {
            s_log.debug("Actually repopulating the cache");
            }
            s_cache.clear();
            
            ShortcutCollection shortcuts = Shortcut.retrieveAll();
            while (shortcuts.next()) {
                Shortcut shortcut = shortcuts.getShortcut();
                if (s_log.isDebugEnabled()) {
                s_log.debug(shortcut.getUrlKey() + " -> " + shortcut.getRedirect());
                }
                s_cache.put(shortcut.getUrlKey().toLowerCase(), 
                            shortcut.getRedirect());
            }

            // only commit the Txn if we started it
            if (doTxn) {
                if (s_log.isDebugEnabled()) {
                s_log.debug("Committing DB transaction");
            }
            txn.commitTxn();
            }

        } finally {
            if (doTxn && txn.inTxn()) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Aborting DB transaction");
                }
                txn.abortTxn();
            }
        }
    }


    public static String cleanURLKey(String url) {
        if ( !url.startsWith("/") ) {
            url = "/" + url;
        }
        if ( !url.endsWith("/") ) {
            url = url + "/";
        }

        return url;
    }
}


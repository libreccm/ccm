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
 */

package com.arsdigita.themedirector;

import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

// This class listens for global save events for the theme.  Currently,
// it only cares about when the theme is actually published.  If the
// theme is published, then it creates a transaction listener that will
// update other servers.
public class ThemeObserver implements DomainObjectObserver {

    /** A logger instance.  */
    private static final Logger s_log = 
                                Logger.getLogger(ThemeObserver.class);

    public static final String THEME_TXN_ATTR =
        ThemeObserver.class.getName() + ".listener";

    
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

    
    /**
     * One of the two events that trigger updates.
     * @param dobj
     * @throws PersistenceException
     */
    public void afterSave(DomainObject dobj) throws PersistenceException {
        // if it was published in the last 3 minutes...3 minutes should
        // be long enough so that all other processes are run but
        // short enough that this won't occur for multiple transactions
        // the 3 minute time frame may need to be tuned, though.

        TransactionContext ctx = SessionManager.getSession().getTransactionContext();
        Set txnSet = (Set)ctx.getAttribute(THEME_TXN_ATTR);
        
        Theme theme = (Theme)dobj;

        if (txnSet == null || !txnSet.contains(theme)) {
            //listener isn't added, so add it if conditions are correct
            Date lastPublished = theme.getLastPublishedDate();
            if (lastPublished != null) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.add(Calendar.MINUTE, -3);
                if (lastPublished.after(calendar.getTime())) {
                    if (txnSet == null) {
                        ctx.addTransactionListener(new ThemeTransactionListener());
                        txnSet = new HashSet();
                        ctx.setAttribute(THEME_TXN_ATTR, txnSet);
                    }
                    txnSet.add(theme);
                }
            }
        } 
    }

    /**
     * Deletion not supported, empty implementation.
     * Triggers an update as well, but there is currently is no way 
     * to delete a Theme.
     * @param dobj
     * @throws PersistenceException
     */
    public void afterDelete(DomainObject dobj) throws PersistenceException {}
}

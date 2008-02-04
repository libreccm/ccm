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
package com.arsdigita.search.intermedia;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.util.TimerTask;


/**
 * The TimerFinished class provides the method that is scheduled
 * by the timer created by BuildIndex.  It simply calls the
 * timerFinished method of BuildIndex.
 *
 * @author Jeff Teeters
 * @version 1.0
 **/
class TimerFinished extends TimerTask {
    public static final String versionId = "$Id: TimerFinished.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static Logger s_log = Logger.getLogger(TimerFinished.class);
    public void run() {

        TransactionContext txn = getTransaction();
        txn.beginTxn();

        try {
            BuildIndex.checkState();
        } catch(Exception e) {
            s_log.error("BuildIndex.checkState() failed!", e);
            txn.abortTxn();
            return;
        }

        txn.commitTxn();
    }

    private TransactionContext getTransaction() {
        Session ssn = SessionManager.getSession();
        Assert.exists(ssn, Session.class);
        return ssn.getTransactionContext();
    }
}

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
package com.arsdigita.persistence;

import com.arsdigita.tools.junit.framework.BaseTestCase;

/*
 * TransactionContextTest
 *
*/

public class TransactionContextTest extends BaseTestCase {

    public TransactionContextTest(String name) {
   	super(name);
    }
  

    /**
     * Tests TransactionContext to check that it is properly.
     * See bugzilla 117883.
     */
    public void testCommitErrorHandling() throws Exception {
   	Session ssn = SessionManager.getSession();
	TransactionContext txn = ssn.getTransactionContext();
	TestTransactionListener listener = new TestTransactionListener();
	txn.addTransactionListener(listener);
	
	try {
	    txn.commitTxn();	
	    fail("Didn't throw exception");
	} catch(BeforeCommitException e) {
	    assertTrue("Not in a transaction", txn.inTxn());
	    assertFalse("Transaction was aborted somehow?", listener.m_isAborted);
	    txn.abortTxn();
	    assertTrue("Transaction not aborted?", listener.m_isAborted);
	}
    }
   
    private static final class BeforeCommitException extends RuntimeException {
   	public BeforeCommitException(String name) {
	    super(name);
	}
    }

    private static final class TestTransactionListener implements  TransactionListener {
	boolean m_isAborted = false;
	public void beforeCommit(TransactionContext txn) {
	    throw new BeforeCommitException("Testing commit error handling");
	}
	
	public void afterCommit(TransactionContext txn) {}
	public void beforeAbort(TransactionContext txn) {
	    m_isAborted = true;
	}
	public void afterAbort(TransactionContext txn) {}
	
    }

}

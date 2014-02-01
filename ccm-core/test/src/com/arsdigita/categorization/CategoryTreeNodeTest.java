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
package com.arsdigita.categorization;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import junit.framework.TestCase;

// ALL METHODS ARE COMMENTED OUT BECAUSE WE ARE REPLACING THE API
// AND SO WE HAVE TO REWRITE MOST OF THE TESTS
/**
 * This performs some smoke tests on the CategoryTreeNode class
 * Specifically, it makes sure that every method works as
 * advertised and that the system talks to the database currectly
 *
 *
 * @author Randy Graebner
 * @version 1.0
 * @see com.arsdigita.kernel
 * @see com.arsdigita.categorization
 */


public class CategoryTreeNodeTest extends TestCase {


    Session session;
    TransactionContext txn;

    /**
     * Constructs a CategoryTreeNodeTest with the specified name.
     *
     * @param name Test case name.
     **/
    public CategoryTreeNodeTest( String name ) {
        super( name );
    }

    public void setUp() {
        session = SessionManager.getSession();
        txn = session.getTransactionContext();
        txn.beginTxn();
    }

    public void tearDown() {
        txn.abortTxn();
    }


    public void testConstructors() {
    }

    /* TODO
       these are the methods to test
       public CategoryTreeNode(ACSObject c,
       boolean includeCategories,
       boolean includeObjects) {
       public Component getComponent() {
       public Iterator getChildren() {
       public boolean hasChildren() {
       public String getId() {
       public ACSObject getObject() {
       public String getLabel() {

    */
}

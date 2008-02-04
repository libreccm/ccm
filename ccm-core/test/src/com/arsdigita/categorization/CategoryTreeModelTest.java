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
 * This performs some smoke tests on the CategoryTreeModel
 * Specifically, it makes sure that every method works as
 * advertised and that the system talks to the database currectly
 *
 *
 * @author Randy Graebner
 * @version 1.0
 * @see com.arsdigita.kernel
 * @see com.arsdigita.categorization
 */


public class CategoryTreeModelTest extends TestCase {

    public static final String versionId = "$Id: CategoryTreeModelTest.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    Session session;
    TransactionContext txn;

    /**
     * Constructs a CategoryTreeModelTest with the specified name.
     *
     * @param name Test case name.
     **/
    public CategoryTreeModelTest( String name ) {
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

    /*
      these are the methods to test
      TODO
      public CategoryTreeModel (Category rootCategory) {
      public void setIncludeObjects(boolean includeObjects) {
      public boolean getIncludeObjects() {
      public void setIncludeCategories(boolean includeCategories) {
      public boolean getIncludeCategories() {
      public TreeNode getRoot(PageState data) {
      public Iterator getChildren(TreeNode n, PageState data) {

    */
}

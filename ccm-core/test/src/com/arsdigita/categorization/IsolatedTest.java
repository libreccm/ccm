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
package com.arsdigita.categorization;

import com.arsdigita.developersupport.Debug;
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import org.apache.log4j.Logger;

/**
 * Temporary placeholder for tests that need to be run in isolation.
 */
public class IsolatedTest extends CategoryTestCase {
    // turned off.  doesn't really fail
    public final static boolean FAILS = true;

    private final static Logger s_log = Logger.getLogger(IsolatedTest.class);

    private Session m_ssn;

    public IsolatedTest(String name) {
        super(name);
    }

    public void setUp() {
        m_ssn = SessionManager.getSession();
    }

    public void testGetObjects() {
        Category category1 = new Category();
        Category category2 = new Category();

        Group group1 = new Group();
        Group group2 = new Group();
        group1.setName("group1");
        group2.setName("group2");

        category1.addChild(category2);
        category1.addChild(group1);
        category1.addChild(group2);
        category2.addChild(group2);

        assertContains(category1.getChildren(), category2);

        //final String oldLevel = queryLoggingOn();
        assertContains(category1.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group1));
        //queryLoggingOff(oldLevel);

        assertContains(category1.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group2));

        assertContains(category2.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group2));

        group1.delete();
        group2.delete();
        category1.delete();
        category2.delete();
    }

    private final static String QUERY_LOGGER =
        "com.redhat.persistence.engine.rdbms.RDBMSEngine";

    private static String queryLoggingOn() {
        return Debug.setLevel(QUERY_LOGGER, "INFO");
    }

    private static void queryLoggingOff(String oldLevel) {
        Debug.setLevel(QUERY_LOGGER, oldLevel);
    }

    private void fakeCommit() {
        s_log.debug("calling fakeCommit");
        TestTransaction.testCommitTxn(m_ssn.getTransactionContext());
    }

}

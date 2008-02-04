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
package com.arsdigita.forum;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;

import com.arsdigita.kernel.User;

import com.arsdigita.web.Application;

import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Unit tests for forum subscriptions
 *
 * @author <a href="mailto:manu.nath@devlogics.com">Manu R Nath</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @since ACS 4.6.5
 */

public class SubscriptionTest extends BaseTestCase {

    public static final String versionId = "$Id: SubscriptionTest.java 287 2005-02-22 00:29:02Z sskracic $ by $author$ by $DateTime: 2004/08/17 23:26:27 $";

    private ForumSubscription m_subscription;
    private Forum m_bboard;

    public SubscriptionTest(String name) {
        super(name);
    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ForumTest.class);
    }

    public void setUp() {
        m_bboard = createForum();
        m_subscription = new ForumSubscription(m_bboard);
        m_subscription.save();
    }

    // tests Forum Subscription's gets, sets and associations
    public void testSubscriptionSmokeTest() {

        ForumSubscription subscription2 = null;
        try {
            subscription2 =
                new ForumSubscription(m_subscription.getID());
        } catch (DataObjectNotFoundException e) {
            fail("couldn't retrieve bboard subscription after we saved it");
        }

        assertEquals(m_bboard, subscription2.getForum());

        /// test the association from bboardForum to subscription

        DataCollection dc = m_bboard.getSubscriptions();
        dc.addEqualsFilter("id", m_subscription.getID());
        assertEquals(1L, dc.size());

        while (dc.next()) {
            ForumSubscription s = new ForumSubscription(dc.getDataObject());
            assertEquals(m_subscription, s);
        }

    }

    public void testMembers() {
        User user = BboardSuite.createUser();
        assertTrue(! m_subscription.isSubscribed(user));
        m_subscription.subscribe(user);
        m_subscription.save();
        assertTrue(m_subscription.isSubscribed(user));
        m_subscription.unsubscribe(user);
        m_subscription.save();
        assertTrue(! m_subscription.isSubscribed(user));
    }

    public void testSendNotification() {
        m_subscription.sendNotification(createMsg(m_bboard));
    }


    private static Forum createForum() {
        /*
         * don't use Forum.create() because that creates subscriptions
         * which interfere with these tests.
         */

        Forum bboard = (Forum) Application.createApplication
            (Forum.BASE_DATA_OBJECT_TYPE,
             "subscription_test",
             "Subscription Test Forum",
             null);
        bboard.setDescription("Subscription Test Description");
        bboard.save();

        return bboard;
    }

    public static Post createMsg(Forum bboard) {
        Post msg = Post.create(bboard);
        msg.setSubject("the subject");
        msg.setBody("the body", "text/plain");
        msg.setFrom(BboardSuite.createUser());
        msg.save();
        return msg;
    }
}

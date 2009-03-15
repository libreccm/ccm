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
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.messaging.MessageThread;

import com.arsdigita.tools.junit.framework.BaseTestCase;

public class ThreadSubscriptionTest extends BaseTestCase {

    public static final String versionId = "$Id: ThreadSubscriptionTest.java 1844 2009-03-05 13:25:28Z terry $ by $Author: terry $, $DateTime: 2004/08/17 23:26:27 $";

    private ThreadSubscription m_subscription;
    private MessageThread m_thread;

    public ThreadSubscriptionTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ThreadSubscriptionTest.class);
    }

    public void setUp() {
        m_subscription = new ThreadSubscription();

        m_thread = createThread();
        m_subscription.setThread(m_thread);

        m_subscription.save();
    }

    public void testSubscriptionCreate() {
        assertEquals(m_thread, m_subscription.getThreadReal());
    }

    public void testSubscriptionRetrieve() {

        ThreadSubscription subscription2 = null;
        try {
            subscription2 =
                new ThreadSubscription(m_subscription.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("couldn't retrieve bboard subscription after we saved it");
        }

        assertEquals(m_thread, subscription2.getThreadReal());

    }

    public static MessageThread createThread() {
        Post msg = Post.create(createForum());
        msg.setSubject("the subject");
        msg.setBody("the body", "text/plain");
        msg.setFrom(BboardSuite.createUser());
        msg.save();
        return msg.getThread();
    }

    private static Forum createForum() {
        Forum forum =
            Forum.create("subscription_test", "Subscription Test Forum",
                         null);
        forum.setDescription("Subscription Test Description");
        forum.save();

        return forum;
    }

    public void testGetThreadSubscription() {
        assertEquals(m_subscription,
                     ThreadSubscription.getThreadSubscription(m_thread));
    }

    public void testGetUserSubs() {
        User user = BboardSuite.createUser();
        m_subscription.subscribe(user);
        m_subscription.save();

        DomainCollection subs = ThreadSubscription.getSubsForUser(user, null);

        boolean found = false;
        while (subs.next()) {
            if (subs.getDomainObject().equals(m_subscription)) {
                found = true;
            }
        }

        assertTrue("Didn't find the ThreadSubscription for our user", found);
    }
}

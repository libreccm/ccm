/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.messaging;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.Date;

public class ThreadTest extends BaseTestCase {

    private MessageThread m_thread = null;
    private ThreadedMessage m_root = null;
    private Date m_updated = null;
    private long m_postPopulateNumber = 0L;

    public ThreadTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ThreadTest.class);
    }

    protected void setUp() {
        m_root = new ThreadedMessage(getUser(), "subject", "body");
        m_root.save();
        m_thread = m_root.getThread();
        m_updated = m_root.getSentDate();
    }

    public void testSmoketest() {
        assertEquals(m_root, m_thread.getRootMessage());
        assertEquals(0L, m_thread.getNumReplies());
        assertEquals(m_root.getSentDate(), m_thread.getLatestUpdateDate());
        assertEquals(m_root.getFrom(), m_thread.getAuthor());
        assertEquals(m_root.getSubject(), m_thread.getSubject());
    }

    public void testThreadUpdates() {
        populateThread();
        // need to retrieve a new copy because the existing ones
        // don't get updated when the database changes
        MessageThread t = MessageThread.getFromRootMessage(m_root);

        assertEquals(m_postPopulateNumber, t.getNumReplies());

        // and this still fails because the retrieved date is
        // truncated to the second.
        //          assertEquals(m_updated,
        //                       MessageThread.getFromRootMessage(m_root)
        //                       .getLatestUpdateDate());
    }

    public void testRetrievals() {
        assertEquals(m_thread, MessageThread.getFromRootMessage(m_root));
        assertEquals(m_thread, m_root.getThread());
    }

    private User getUser() {

        String key   = String.valueOf(System.currentTimeMillis());
        String email = key + "-message-test@arsdigita.com";
        String first = key + "-message-test-given-name";
        String last  = key + "-message-test-family-name";

        User user = new User();
        user.setPrimaryEmail(new EmailAddress(email));

        PersonName name = user.getPersonName();
        name.setGivenName(first);
        name.setFamilyName(last);

        user.save();

        return user;
    }

    private void populateThread() {
        ThreadedMessage[] msgs = new ThreadedMessage[11];

        msgs[0] = m_root;

        for (int i = 1 ; i <= 10 ; i++) {
            msgs[i] = msgs[i-1].replyTo(getUser(),"body");
            //System.out.println("ThreadTest: update time is " +
            //                   m_thread.getLatestUpdateDate().getTime());
            msgs[i].save();
            m_updated = msgs[i].getSentDate();
            m_postPopulateNumber++;
        }
    }

}

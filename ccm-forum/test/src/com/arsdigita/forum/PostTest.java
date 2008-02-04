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

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test cases for bboard posts.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 */

public class PostTest extends BaseTestCase {

    public static final String versionId = "$Id: PostTest.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:26:27 $";

    protected static final String SUBJECT = "This is the subject";
    protected static final String BODY    = "<p>This is the <b>body</b></p>";
    protected static final String TYPE    = "text/html";

    private User  m_user  = null;
    private Post  m_msg   = null;
    private Forum m_forum = null;

    public PostTest(String name) {
        super(name);
    }

    /**
     * Create a default forum with a single post.
     */

    public void setUp() {

        m_forum = Forum.create("bboard_message_test", "name", null);
        m_forum.setDescription("description");
        m_forum.save();

        m_user = BboardSuite.createUser();

        m_msg = Post.create(m_forum);
        m_msg.setSubject(SUBJECT);
        m_msg.setBody(BODY,TYPE);
        m_msg.setFrom(m_user);
        m_msg.save();
    }

    /**
     * Create a simple message using the various set methods.
     */


    public void testMessageCreate() {
        assertEquals(SUBJECT,m_msg.getSubject());
        assertEquals(BODY,m_msg.getBody());
        assertEquals(TYPE,m_msg.getBodyType());
        assertEquals(m_forum.getID(),m_msg.getRefersTo());
    }

    /**
     * Create a simple message and save it to the database.
     */

    public void testMessageRetrieve() {
        // Retrieve a copy of the message and verify its contents
        Post msg2 = null;
        try {
            msg2 = new Post(m_msg.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Couldn't retrieve bboard message after we saved it");
        }

        assertEquals(m_msg.getSubject(), msg2.getSubject());
        assertEquals(m_msg.getBody(), msg2.getBody());
        assertEquals(m_msg.getBodyType(), msg2.getBodyType());

        assertEquals(m_msg.getRefersTo(), msg2.getRefersTo());
    }

    /* Tests basic retrieve all. Adds to rows and retrieves
     * them in reverse order. This also checks setting, storing
     * and getting the refersTo attribute.
     */
    public void testCreateThread004() {
        Post msg2 = Post.create(m_forum);
        msg2.setSubject(SUBJECT);
        msg2.setBody(BODY,TYPE);
        msg2.setFrom(m_user);
        msg2.save();


        Session session = SessionManager.getSession();
        DataCollection msgThread =
            session.retrieve("com.arsdigita.messaging.ThreadedMessage");
        msgThread.addEqualsFilter("objectID", m_forum.getID());
        msgThread.addOrder("id desc");

        int x = 1;

        while (msgThread.next()) {
            if (x==1) {
                assertEquals(msgThread.get("id"), msg2.getID());
                assertEquals(msgThread.get("objectID"), m_msg.getRefersTo());
            } else if (x==2) {
                assertEquals(msgThread.get("id"), m_msg.getID());
            }

            x++;
        }

    }

    /**
     * Create a post and categorize it.
     */

    public void testCategorizedPost() {

        Category root = m_forum.getRootCategory();
        ForumTest.buildSubCategories(root);

        // Retrieve a child category to use for the post


        CategoryCollection categories = root.getChildren();
        assertTrue("has at least one category", categories.next());
        Category postCategory = categories.getCategory();
        categories.close();

        // Create a new post and categorize it

        Post post = Post.create(m_forum);
        post.setSubject(SUBJECT);
        post.setBody(BODY,TYPE);
        post.setFrom(m_user);

        // Verify that the post cannot be categorized until we save it.

        try {
            post.mapCategory(postCategory);
            fail("Categorized an unsaved post");
        } catch (PersistenceException ex) {
            // correct
        }

        post.save();
        post.mapCategory(postCategory);

        // Verify that the category now contains one child object.
        assertEquals(1, postCategory.getNumberOfChildObjects());

        CategoryCollection result = post.getCategories();
        assertTrue("has at least one category", result.next());
        assertEquals(postCategory, result.getCategory());
        result.close();
    }


    public void testReplyTo() {
        Post reply = (Post)m_msg.replyTo();
        reply.setSubject(SUBJECT);
        reply.setBody(BODY, TYPE);
        reply.setFrom(BboardSuite.createUser());
        reply.save();
    }

    public void testSendNotifications() {
        m_msg.sendNotifications();
    }

    public void testSubscriptionCreation() {
        m_msg.createThreadSubscription();
        assertNotNull(ThreadSubscription.getThreadSubscription(
                                                               m_msg.getThread()));
    }
    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(PostTest.class);
    }

}

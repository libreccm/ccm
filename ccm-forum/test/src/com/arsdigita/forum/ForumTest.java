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

import com.arsdigita.forum.ui.Constants;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

/**
 * Test cases for Forums
 *
 * @author <a href="mailto:manu.nath@devlogics.com">Manu R Nath</a>
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 * @since ACS 4.6.5
 */

public class ForumTest extends BaseTestCase {

    // Forum details

    private static final String NAME =
        "This is the Name";
    private static final String DESCRIPTION =
        "<p>This is the <b>Description</b></p>";

    private Forum m_forum;

    public ForumTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_forum = Forum.create("bboard_test", NAME, null);
        m_forum.setDescription(DESCRIPTION);
        m_forum.save();
        DataCollection subs = m_forum.getSubscriptions();
        // all forums are create()d with two subscriptions
        System.out.println("Subs size:  " + subs.size());
        subs.close();

    }

    protected void tearDown() throws Exception {
        //  m_forum.delete();
        super.tearDown();
    }

    public void testForumCreate() {
        assertEquals(NAME, m_forum.getTitle());
        assertEquals(DESCRIPTION, m_forum.getDescription());
    }

    /**
     * Verifies that Forum.create() produces a root category for a
     * forum.
     */

    public void testForumRootCategory() {
        assertNotNull(m_forum.getRootCategory());
    }

    /**
     * Create a simple forum and save it to the database
     * using its various mutator methods. Then create a copy
     * of the Forum and verify the property of both the
     * objects using the accessor methods.
     */

    public void testForumRetrieve() {
        Forum forum = null;
        try {
            forum = new Forum(m_forum.getID());
        } catch (DataObjectNotFoundException e) {
            fail("Couldn't retrieve Forum object after creating " +
                 "and saving it.");
        }

        assertNotNull(forum);
        assertEquals(m_forum.getTitle(), forum.getTitle());
        assertEquals(m_forum.getDescription(), forum.getDescription());
    }


    /**
     * Tests basic retrieve all.
     */
    public void testForumRetrieveAll() {
        DataCollection forums = SessionManager.getSession().retrieve
            ("com.arsdigita.forum.Forum");
        forums.addEqualsFilter("id", m_forum.getID());

        boolean found = false;
        while (forums.next()) {
            assertEquals(forums.get("id"), m_forum.getID());
            found = true;
        }
        assertTrue(found);
    }

    public void testForumUpdate() {
        String name = "new name";
        String desc = "new description";

        m_forum.setTitle(name);
        m_forum.setDescription(desc);

        assertEquals(name, m_forum.getTitle());
        assertEquals(desc, m_forum.getDescription());

        m_forum.save();

        Forum forum = null;
        try {
            forum = new Forum(m_forum.getID());
        } catch (DataObjectNotFoundException e) {
            fail("Couldn't retrieve Forum object after creating " +
                 "and saving it.");
        }

        assertNotNull(forum);
        assertEquals(name, forum.getTitle());
        assertEquals(desc, forum.getDescription());
    }


    public void testMessageList() {
        m_forum.getPosts();
    }

    public void testGetThreads() {
        Post post1 = createPost(BboardSuite.createUser());
        Post post2 = createPost(BboardSuite.createUser());

        // for testing thread in categories vs. threads uncategorized
        Category root = m_forum.getRootCategory();
        Category child = new Category("categoryTest1","categoryTest1");
        child.save();
        root.addChild(child);
        root.save();
        Post postCategorized1 = createPost(BboardSuite.createUser());
        postCategorized1.mapCategory(child);

        Post reply1 = (Post)post1.replyTo();
        reply1.setFrom(BboardSuite.createUser());
        reply1.setSubject(PostTest.SUBJECT);
        reply1.setBody(PostTest.BODY, PostTest.TYPE);
        reply1.save();

        DomainCollection threads = m_forum.getThreads();
        boolean hasPost1 = false;
        boolean hasPost2 = false;
        boolean hasReply1 = false;

        while (threads.next()) {
            MessageThread thread = (MessageThread) threads.getDomainObject();
            ThreadedMessage test = thread.getRootMessage();
            if (test.equals(post1)) { hasPost1 = true; }
            else if (test.equals(post2)) { hasPost2 = true; }
            else if (test.equals(reply1)) { hasReply1 = true; }
        }

        assertTrue("Didn't find post1 in thread listing", hasPost1);
        assertTrue("Didn't find post2 in thread listing", hasPost2);
        assertTrue("Found a reply in listing of threads", !hasReply1);

        // finish testing thread in cat vs. thread uncategorized
        DomainCollection categorizedThreads = m_forum.getThreads(child.getID());
        hasPost1 = false;
        hasPost2 = false;
        hasReply1 = false;
        boolean hasPostCategorized1 = false;

        while (categorizedThreads.next()) {
            MessageThread thread = (MessageThread)
                categorizedThreads.getDomainObject();
            ThreadedMessage test = thread.getRootMessage();
            if (test.equals(post1)) { hasPost1 = true; }
            else if (test.equals(post2)) { hasPost2 = true; }
            else if (test.equals(reply1)) { hasReply1 = true; }
            else if (test.equals(postCategorized1)) { hasPostCategorized1 = true; }
        }

        assertTrue("Found post1 in categorized thread listing", !hasPost1);
        assertTrue("Found post2 in categorized thread listing", !hasPost2);
        assertTrue("Found a reply in uncategorized listing of threads", !hasReply1);
        assertTrue("Didn't find categorizedPost1 in thread listing",
                   hasPostCategorized1);

        DomainCollection uncategorizedThreads
            = m_forum.getThreads(Constants.TOPIC_NONE);
        hasPost1 = false;
        hasPost2 = false;
        hasReply1 = false;
        hasPostCategorized1 = false;

        while (uncategorizedThreads.next()) {
            MessageThread thread = (MessageThread)
                uncategorizedThreads.getDomainObject();
            ThreadedMessage test = thread.getRootMessage();
            if (test.equals(post1)) { hasPost1 = true; }
            else if (test.equals(post2)) { hasPost2 = true; }
            else if (test.equals(reply1)) { hasReply1 = true; }
            else if (test.equals(postCategorized1)) { hasPostCategorized1 = true; }
        }

        assertTrue("Didn't find post1 in uncategorized thread listing", hasPost1);
        assertTrue("Didn't find post2 in uncategorized thread listing", hasPost2);
        assertTrue("Found a reply in uncategorized listing of threads", !hasReply1);
        assertTrue("Found categorized post in uncategorized thread listing",
                   !hasPostCategorized1);

    }

    public void testSubscriptionList() {
        DataCollection subs = m_forum.getSubscriptions();
        // all forums are create()d with two subscriptions
        assertEquals(2L, subs.size());
        subs.close();
    }

    /**
     * Test for retrieving child categories of a Forum
     */

    public void testCategoryRetrieve() {

        String names[] = {
            "cat0",
            "cat1",
            "cat2"
        };

        Category root = m_forum.getRootCategory();
        buildSubCategories(root,names);

        // Retrieve the children and verify that the correct number
        // were created.
        assertEquals(names.length, root.getChildren().size());
    }

    /**
     * Create sub categories and post messages to them.  Verify that
     * the summary information is generated correctly for both
     * categorized and uncategorized messages.
     */

    public void testCategorizationSummary() {
        Category root = m_forum.getRootCategory();
        buildSubCategories(root);

        // Generate a collection of posts for each category
        int numPosts = 2;

        User user = BboardSuite.createUser();

        CategoryCollection categories = root.getChildren();
        while (categories.next()) {
            Category c = categories.getCategory();
            for (int i = 0; i < numPosts; i++) {
                Post post = createPost(user);
                post.mapCategory(c);
            }
        }

        // Create an equivalent number of uncategorized posts

        for (int i = 0; i < numPosts; i++) {
            createPost(user);
        }

        // In this test there is a one-to-one correspondence between
        // threads and posts.

        BigDecimal numThreads = BigDecimal.valueOf(numPosts);

        // Generate the listing of categorized postings for this forum
        // and verify that each category contains the correct number
        // of threads.

        DataQuery query = m_forum.getCategories();
        while (query.next()) {
            assertEquals(numThreads, query.get("numThreads"));
        }
        query.close();

        // Generate the listing of uncategorized postings for this forum
        query = m_forum.getUnCategory();
        query.next();
        assertEquals(numThreads, query.get("numThreads"));
        query.close();
    }

    /**
     * Create a persistent post to the test forum.
     */

    private Post createPost(User user) {
        Post post = Post.create(m_forum);
        post.setFrom(user);
        post.setSubject(PostTest.SUBJECT);
        post.setBody(PostTest.BODY, PostTest.TYPE);
        post.save();
        return post;
    }

    /**
     * Create sub categories (for a forum).  One sub category will be
     * created for each element of the names[] array, and its
     * description will be set to the same string (name == description).
     */

    protected static void buildSubCategories(Category root, String names[]) {
        for (int i = 0; i < names.length; i++) {
            Category child = new Category(names[i],names[i]);
            child.save();
            root.addChild(child);
            root.save();
        }
    }

    // Convenience method (if you don't care what the names are)

    protected static void buildSubCategories(Category root) {
        String names[] = {
            "cat1",
            "cat2",
            "cat3"
        };
        buildSubCategories(root,names);
    }

    /**
     * Main method required to make this test runnable.
     */
    public static void main(String args[]) {
        junit.textui.TestRunner.run(ForumTest.class);
    }

}

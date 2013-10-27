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
package com.arsdigita.forum.ui;

import com.arsdigita.forum.util.GlobalizationUtil;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ThreadSubscription;
import com.arsdigita.forum.ui.admin.RejectionForm;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormCancelListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.messaging.MessageThread;

import org.apache.log4j.Logger;

/**
 * A Bebop component which provides the dynamic parts of the UI for looking at
 * a single thread of the forum and posting new messages as well as replying to it.
 * (The invoking class forum.ThreadPageBuilder creates the static parts as the 
 * title of the forum and the forum introcuction text).
 * 
 * The dynamic part of the discussion page contains three components: 
 * a MessageList, a ThreadEdit component, and a ThreadReply component.
 *
 * Contains MessagesComponent, EditPostForm, and ReplyToPostForm
 * @see com.arsdigita.forum.ui.MessagesComponent
 * @see com.arsdigita.forum.ui.EditPostForm
 * @see com.arsdigita.forum.ui.ReplyToPostForm
 */

public class DiscussionThreadSimpleView extends ModalContainer implements Constants {

    /** Private logger instance for debugging purpose. */
    private static final Logger s_log
        = Logger.getLogger(DiscussionThreadSimpleView.class);

    // References to sub-components for event access.
    /** The message list component */
    private Container m_threadMessagesPanel;
    /** The post component for a root mesage */
    private PostForm m_rootForm;
    /** The post component for a reply mesage */
    private PostForm m_replyForm;
    /** A message component for moderators */
    private Form m_rejectForm;

    /** */
    private ACSObjectSelectionModel m_postModel;

    /**
     * Default Constructor creates the component of the discussion view.
     * The discussion page contains three components: a MessageList,
     * a ThreadEdit component, and a ThreadReply component.
     * 
     */
    public DiscussionThreadSimpleView() {
        // Create a modal container: shows only one containee at a time.
        m_postModel = new ACSObjectSelectionModel("post");

        initComponents();
    }

    /**
     * Internal helper method for constructor.
     * Add the thread components to the modal container and maintain
     * references for event manipulation purposes.
     */
    private void initComponents() {

        s_log.debug("creating edit post form");
        m_rootForm = new RootPostForm(m_postModel);
        s_log.debug("creating reply to post form");
        m_replyForm = new ReplyToPostForm(m_postModel);
        s_log.debug("creating reject form");
        m_rejectForm = new RejectionForm(m_postModel);
        
        addForm(m_rootForm);
        addForm(m_replyForm);
        addForm(m_rejectForm);

        m_threadMessagesPanel = new SimpleContainer();
        Container linksPanel = new SimpleContainer(FORUM_XML_PREFIX +
                                                   ":threadOptions",
                                                   Constants.FORUM_XML_NS);

        // Offer links to return to index or control alerts.
        Link returnLink = new Link(new Label(GlobalizationUtil.gz(
                                             "forum.ui.thread.viewAll")), 
                                   "index.jsp");
        returnLink.setClassAttr("actionLink");
        linksPanel.add(returnLink);

        SimpleContainer subLinks = new SimpleContainer();
        subLinks.add(createThreadSubscribeLink());
        subLinks.add(createThreadUnsubscribeLink());
        linksPanel.add(subLinks);

        // Add the panel to the view.
        m_threadMessagesPanel.add(linksPanel);
        m_threadMessagesPanel.add(new DiscussionPostsList(m_postModel, this));
        add(m_threadMessagesPanel);

        // The threadMessagesPanel is the default component.
        setDefaultComponent(m_threadMessagesPanel);
    }

    public void makeListViewVisible(PageState state) {
        setVisibleComponent(state, m_threadMessagesPanel);
    }

    public void makeEditFormVisible(PageState state) {
		s_log.debug("making edit form visible");
        Post post = (Post)m_postModel.getSelectedObject(state);
        if (post.getRoot() == null) {
        	m_rootForm.setContext(state, ReplyToPostForm.EDIT_CONTEXT);
			setVisibleComponent(state, m_rootForm);
        } else {
        	m_replyForm.setContext(state, ReplyToPostForm.EDIT_CONTEXT);
        	setVisibleComponent(state, m_replyForm);
        }
        
    }

    public void makeReplyFormVisible(PageState state) {
        s_log.debug("making reply form visible");
        m_replyForm.setContext(state, PostForm.REPLY_CONTEXT);
        setVisibleComponent(state, m_replyForm);
    }

    public void makeRejectFormVisible(PageState state) {
        setVisibleComponent(state, m_rejectForm);
    }
    
    /**
     * Creates the component for viewing a thread.
     */
    private final void addForm(final Form form) {
        add(form);
        form.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    s_log.debug("FORM ACTION COMPLETED");
					PageState ps = e.getPageState();
					// ps.reset(form);
                    makeListViewVisible(ps);
                }
                });

        form.addCancelListener(new FormCancelListener() {
                public void cancel(FormSectionEvent e) {
                    s_log.debug("fire cancel listener");
                    PageState ps = e.getPageState();
                   // ps.reset(form);
                    makeListViewVisible(ps);
                }
            });
    }



    /**
     * 
     * @return 
     */
    private Component createThreadSubscribeLink() {
        ActionLink subscribeLink = new ActionLink(
            new Label(GlobalizationUtil.gz("forum.ui.thread.subscribe"))) {
                public boolean isVisible(PageState s) {
                    Party party = Kernel.getContext().getParty();

                    if ( party != null ) {
                        ThreadSubscription sub = ThreadSubscription
                            .getThreadSubscription(getCurrentMessage(s));
                        return !sub.isSubscribed(party);
                    } else {
                        return false;
                    }
                }
            };
        subscribeLink.setClassAttr("actionLink");

        subscribeLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    Party party = Kernel.getContext().getParty();

                    ThreadSubscription sub = ThreadSubscription
                        .getThreadSubscription(getCurrentMessage(s));
                    if (!sub.isSubscribed(party)) {
                        sub.subscribe(party);
                    }
                    sub.save();
                }
            });
        return subscribeLink;
    }

    /**
     * 
     * @return 
     */
    private Component createThreadUnsubscribeLink() {
        ActionLink unsubscribeLink = new ActionLink(
            new Label(GlobalizationUtil.gz("forum.ui.thread.unsubscribe"))) {
                @Override
                public boolean isVisible(PageState s) {
                    Party party = Kernel.getContext().getParty();

                    if ( party != null ) {
                        ThreadSubscription sub = ThreadSubscription
                            .getThreadSubscription(getCurrentMessage(s));
                        return sub.isSubscribed(party);
                    } else {
                        return false;
                    }
                }
            };
        unsubscribeLink.setClassAttr("actionLink");

        unsubscribeLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    Party party = Kernel.getContext().getParty();


                    ThreadSubscription sub = ThreadSubscription.
                        getThreadSubscription(getCurrentMessage(s));

                    if (sub.isSubscribed(party)) {
                        sub.unsubscribe(party);
                    }
                    sub.save();
                }
            });
        return unsubscribeLink;
    }

    /**
     * 
     * @return 
     */
    private final ACSObjectSelectionModel getPostModel() {
        return m_postModel;
    }

    /**
     * 
     * @param state
     * @return 
     */
    private MessageThread getCurrentMessage(PageState state) {
        return ForumContext.getContext(state).getMessageThread();
    }
}

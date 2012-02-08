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
package com.arsdigita.forum.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
// import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.toolbox.ui.SecurityContainer;

import org.apache.log4j.Logger;

/**
 * A reusable Bebop component to display and maintain threads in a given forum
 * instance. Currently used as a page component in ForumUserCompactView but may
 * be used (in future) by other styles of forum display or even standalone as
 * part of other pages as well. In any case it is the main user view and
 * working area of a forum.
 *
 * It consists of two (sub) <i>components</i> which provide
 * (a) a list of existing threads including author, number of replies and date
 *     of last  post for each, and a link to create a new thread. (By createing
 *     a new root post, i.e. including specifying a subject line).
 * (b) an add new post form including an editable subject line to make up a new
 *     thread.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @version $Revision: 1.8 $ $Author: chrisg23 $ $DateTime: 2004/08/17 23:26:27 $
 * @version $Id: $
 */
public class ThreadsPanel extends SimpleContainer
                          implements Constants {

    /** Private logger instance for debugging purpose. */
    private static Logger s_log = Logger.getLogger(ThreadsPanel.class);


    /** Modal container for components, only one of its children can be visibal */
    private ModalContainer m_mode;
    /** Bebop Component, list of threads along with some othere elements, will
     * be one child of ModalContainer */
    private Component m_threadsListing;
    /** Bebop Component, add new post to a thread form. Will be another child of
     * ModalContainer  */
    private PostForm m_postComponent;
    /** Reusable Bebop component. Link providing access to new thread form   */
    private ToggleLink m_newThreadLink;
    /** Switch from threadsComponent to forum post  NOCH ÜBERPRÜFEN! */
    private StringParameter m_newPostParam;

    /**
     * Default Constructor creates the containing components.
     *
     * The threads panel contains two components: a threads List and a New Post
     * input form.
     */
    public ThreadsPanel() {

        // Create a modal container: shows only one component at a time.
        m_mode = new ModalContainer();
        add(m_mode);

        m_threadsListing = createThreadsListing();
        m_postComponent = createNewThreadForm();

        m_mode.add(m_threadsListing);
        m_mode.add(m_postComponent);

        m_mode.setDefaultComponent(m_threadsListing);

    }

    /**
     * 
     * @param p
     */
    @Override
    public void register(Page p) {

        super.register(p);
        // XXX new post param
        /*
        m_newPostParam = new StringParameter(
            Constants.NEW_POST_PARAM_NAME);
        p.addGlobalStateParam(m_newPostParam);
        */
        p.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                s_log.debug("create link pressed");

                PageState s = e.getPageState();
                // switch between modes (components to display)
                if (m_newThreadLink.isSelected(s)) {
                    m_postComponent.setContext(s, PostForm.NEW_CONTEXT);
                    m_mode.setVisibleComponent(s, m_postComponent);
                } else {
                    m_mode.setVisibleComponent(s, m_threadsListing);
                }
            }
        });
        
    }

    /**
     * This method gets the top level threads for this forum, along
     * with author, # of responses, etc. Filtered for approved
     * messages if the forum is moderated.
     */
    private Component createThreadsListing() {

        Container forums = new SimpleContainer();

        Container forumOptions = new SimpleContainer(
                FORUM_XML_PREFIX + ":forumOptions", Constants.FORUM_XML_NS);
        // XXX APLAWS standard theme currently (2010-09) does not use the label!
        m_newThreadLink = new ToggleLink(new Label(Text.gz("forum.ui.thread.newPost")));
        m_newThreadLink.setClassAttr("actionLink");

        // chris.gilbert@westsussex.gov.uk - security container added
		SecurityContainer sc = new SecurityContainer(m_newThreadLink) {

			protected boolean canAccess(Party party, PageState state) {
				Forum forum = ForumContext.getContext(state).getForum();
				PermissionDescriptor createThread = new PermissionDescriptor(
                        PrivilegeDescriptor.get(Forum.CREATE_THREAD_PRIVILEGE),
                        forum,
                        party);
				return PermissionService.checkPermission(createThread);
				
			}
		};
        
        forumOptions.add(sc);
        forums.add(forumOptions);

        // list of topics (if one or more actually available)
        TopicSelector topics = new TopicSelector();
        forums.add(topics);

        ThreadsList threads
            = new ThreadsList();

        forums.add(threads);

        return forums;
    }

    /**
     * Provides a Form to create a new Thread. It is accomplished by constructing
     * a RootPostForm to create a new post which contains a subject line and
     * a selection box for a topic the new thread should be associated to.
     * 
     * @return a form to create the first post of a new thread which implicitly
     *         creates a new thread.
     */
    private PostForm createNewThreadForm() {
        PostForm editForm = new RootPostForm();
        editForm.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ThreadsPanel.this
                        .editPageStateCleanup(e.getPageState());
                }
            });

        return editForm;
    }

    /**
     *
     * @param state
     */
    private void editPageStateCleanup(PageState state) {
        m_newThreadLink.setSelected(state, false);
        ForumContext.getContext(state).setCategorySelection
            (Constants.TOPIC_ANY);
    }
}

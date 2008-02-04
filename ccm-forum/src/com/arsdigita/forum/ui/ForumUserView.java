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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.ForumContext;

import org.apache.log4j.Logger;

/**
 * A reusable Bebop component to display the user view on a Forum
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 *
 * @version $Revision: #8 $ $Author: sskracic $ $DateTime: 2004/08/17 23:26:27 $
 */
public class ForumUserView extends SimpleContainer
    implements Constants {

    private static Logger s_log = Logger.getLogger(ForumUserView.class);

    private Component m_forumView;
    private Component m_forumPost;

    private ModalContainer m_mode;

    private ToggleLink m_newTopicLink;
    private StringParameter m_newPostParam;

    public ForumUserView() {
        m_mode = new ModalContainer();
        add(m_mode);

        m_forumView = createForumView();
        m_forumPost = createForumPost();

        m_mode.add(m_forumView);
        m_mode.add(m_forumPost);

        m_mode.setDefaultComponent(m_forumView);

    }

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
                    PageState s = e.getPageState();
                    /*
                    if ("t".equals((String)s.getValue(m_newPostParam))) {
                        m_newTopicLink.setSelected(s, true);
                        s.setValue(m_newPostParam, null);
                    }
                    */

                    if (m_newTopicLink.isSelected(s)) {
                        m_mode.setVisibleComponent(s, m_forumPost);
                    } else {
                        m_mode.setVisibleComponent(s, m_forumView);
                    }
                }
            });
        
    }

    /**
     * This method gets the top level threads for this forum, along
     * with author, # of responses, etc. Filtered for approved
     * messages if the forum is moderated.
     */

    private Component createForumView() {        
        Container forums = new SimpleContainer();

        Container forumOptions = new SimpleContainer(
            "forum:forumOptions", Constants.FORUM_XML_NS);
        m_newTopicLink = new ToggleLink(new Label(Text.gz("forum.ui.newPost")));
        m_newTopicLink.setClassAttr("actionLink");
        forumOptions.add(m_newTopicLink);
        forums.add(forumOptions);

        // list of categories
        TopicSelector topics = new TopicSelector();
        forums.add(topics);

        ThreadList threads
            = new ThreadList();

        forums.add(threads);

        return forums;
    }

    private Component createForumPost() {
        Form editForm = new NewPostForm();
        editForm.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ForumUserView.this
                        .editPageStateCleanup(e.getPageState());
                }
            });

        return editForm;
    }

    private void editPageStateCleanup(PageState state) {
        m_newTopicLink.setSelected(state, false);
        ForumContext.getContext(state).setCategorySelection
            (Constants.TOPIC_ANY);
    }
}

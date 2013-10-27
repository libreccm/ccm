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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;

import org.apache.log4j.Logger;


/** 
 * Reusable Bebop UI component (container) to display and maintain topics a given
 * forum instance is dealing with. It consists of two (sub) <i>components</i>
 * which provide
 * (a) a list of existing topics including number of assoziated threads and date
 *     of last  post for each topic, and a link to create a new topic.
 * (b) an add new topic form to add a new topic.
 *
 * Used eg. by ForumUserCompactView as a component (sub-panel) to maintain
 * topics. May be used (in future) by other styles of forum display or even
 * standalone as part of other pages.
 * 
 * XXX: Forum knows about <i>threads</i> which groups a set of posts to the same
 * subject, and <i>topics</i> which group a set of threads about the same general
 * theme. Currently Forum uses <i>catgegory</i> as synonym for topic, which may be
 * misleading in some contexts, because there is <i>forum-categorized</i> which
 * uses category in the usual CMS way, esp. navigation categories. We use topic
 * here where possible.
 */
public class TopicsPanel extends SimpleContainer
                         implements ActionListener {

    /** Private logger instance for debugging purpose. */
    private static Logger s_log = Logger.getLogger(TopicsPanel.class);

    /** Modal container for components, only one of its children can be visibal */
    private ModalContainer m_mode;
    /** Bebop Component, list of topics. Will be one child of ModalContainer */
    private Component m_topicslist;
    /** Bebop Component, add new topic form. Will be another child of
     * ModalContainer  */
    private Component m_addTopicForm;
    /** Link providing access to new topic form from topic list  */
    private ToggleLink m_addNewTopicLink;


    /**
     * Default Constructor creates the containing components.
     *
     * The topic panel contains two components: a Topics List and a Add New Topic
     * input form.
     * 
     */
    public TopicsPanel() {

        // Create a modal container: shows only one component at a time.
        m_mode = new ModalContainer();
        add(m_mode);

        // References to sub-components for event access.
        m_topicslist = createTopicsList();
        m_addTopicForm = createAddTopicForm();

        m_mode.add(m_topicslist);
        m_mode.add(m_addTopicForm);

        m_mode.setDefaultComponent(m_topicslist);
    }

    /**
     * 
     * @param p
     */
    public void register(Page p) {
        super.register(p);
        p.addActionListener(this);
    }

    /**
     * Switch between the two available components, only one may be visible at 
     * a time.
     *
     * @param e action event.
     */
    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();

        if (m_addNewTopicLink.isSelected(s)) {
            m_mode.setVisibleComponent(s, m_addTopicForm);
        } else {
            m_mode.setVisibleComponent(s, m_topicslist);
        }
    }

    /**
     * Creates the component TopicsList. It consists of a link to add a new
     * topic and a list of currently defined (available) topics.
     * @return
     */
    private Container createTopicsList() {

        Container topicslist = new SimpleContainer();

        // Create the Add Topic Link.
        Container linksPanel = new SimpleContainer(
                                   Constants.FORUM_XML_PREFIX + ":topicOptions",
                                   Constants.FORUM_XML_NS);
        m_addNewTopicLink = new ToggleLink(
                                new Label(GlobalizationUtil.gz("forum.ui.topic.newTopic")));
        m_addNewTopicLink.setClassAttr("actionLink");
        linksPanel.add(m_addNewTopicLink);
        // add to component
        topicslist.add(linksPanel);

        // create and add topics list
        topicslist.add(new TopicsList());  // separate class

        return topicslist;
    }

    /**
     * Creates the component AddTopicForm. It consists of a name field and a
     * description field.
     * @return
     */
    private Component createAddTopicForm() {
        Form addForm = new TopicAddForm();  //separate class of package
        addForm.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    m_addNewTopicLink.setSelected(s, false);
                    m_mode.setVisibleComponent(s, m_topicslist);
                }
            });
        return addForm;
    }
}

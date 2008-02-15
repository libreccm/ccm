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


public class CategoryView extends SimpleContainer
    implements ActionListener {

    private static Logger s_log = Logger.getLogger(CategoryView.class);

    private ModalContainer m_mode;
    private Component m_categories;
    private Component m_addForm;
    private ToggleLink m_addCategoryLink;

    public CategoryView() {
        m_mode = new ModalContainer();
        add(m_mode);

        m_categories = createCategoryView();
        m_addForm = createAddCategoryForm();

        m_mode.add(m_categories);
        m_mode.add(m_addForm);

        m_mode.setDefaultComponent(m_categories);
    }

    public void register(Page p) {
        super.register(p);
        p.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();

        if (m_addCategoryLink.isSelected(s)) {
            m_mode.setVisibleComponent(s, m_addForm);
        } else {
            m_mode.setVisibleComponent(s, m_categories);
        }
    }

    private Container createCategoryView() {

        Container categories = new SimpleContainer();

        Container linksPanel = new SimpleContainer(Constants.FORUM_XML_PREFIX + ":topicOptions", 
                                                   Constants.FORUM_XML_NS);
        m_addCategoryLink = new ToggleLink(new Label(Text.gz("forum.ui.newTopic")));
        m_addCategoryLink.setClassAttr("actionLink");
        linksPanel.add(m_addCategoryLink);

        categories.add(linksPanel);
        categories.add(new TopicList());

        return categories;
    }

    private Component createAddCategoryForm() {
        Form addForm = new CategoryAddForm();
        addForm.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    m_addCategoryLink.setSelected(s, false);
                    m_mode.setVisibleComponent(s, m_categories);
                }
            });
        return addForm;
    }
}

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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.Assert;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;
import java.util.ArrayList;

/**
 * Main panel for the admin section.
 *
 * @author David Dao
 * @version $Id: AdminSplitPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */

class AdminSplitPanel extends BoxPanel implements ChangeListener {

    private List m_list;
    private ArrayList m_componentList;
    private ArrayList m_keys;
    private GlobalizedMessage m_title;

    /**
     * Constructor
     */

    public AdminSplitPanel (GlobalizedMessage title) {

        m_title = title;
        setClassAttr("sidebarNavPanel");
        m_componentList = new ArrayList();
        m_keys = new ArrayList();
    }

    /**
     *
     * @pre label != null && c != null
     */

    public void addTab (Label label, Component c) {
        Assert.isUnlocked(this);
        m_componentList.add(c);
        c.setClassAttr("main");
        add(c);
        m_keys.add(label);
    }

    public void register(Page p) {
        Assert.isUnlocked(this);

        m_list = new List(new GlobalizedTabModelBuilder());
        m_list.addChangeListener(this);
        m_list.setClassAttr("navbar");
        add(m_list);

        for (int i = 0; i < m_componentList.size(); i++) {
            p.setVisibleDefault((Component) m_componentList.get(i), false);
        }
    }

    public void stateChanged (ChangeEvent e) {
        PageState ps = e.getPageState();
        int selectedIndex = Integer.parseInt((String) m_list.getSelectedKey(ps));
        setTab(selectedIndex, ps);
    }

    public void setTab (int index, PageState ps) {
        m_list.setSelectedKey(ps, String.valueOf(index));
        for (int i = 0; i < m_componentList.size(); i++) {
            if (i == index) {
                ((Component) m_componentList.get(i)).setVisible(ps, true);
                ((Resettable) m_componentList.get(i)).reset(ps);
            } else {
                ((Component) m_componentList.get(i)).setVisible(ps, false);
            }
        }
    }

    public void generateXML(PageState ps, Element parent) {
        super.generateXML(ps, parent);

        /**
         * Globalized navbar title.
         * Why did I override generateXML method to globalize the title?
         * Because CMS put title bar as an attribute of element. This
         * is the only method I could come up with.
         */

        Element child = (Element) parent.getChildren().get(0);
        child.addAttribute ("navbar-title",
                            (String) m_title.localize(ps.getRequest()));
    }

    private class GlobalizedTabModelBuilder extends LockableImpl
        implements ListModelBuilder
    {
        public ListModel makeModel(List l, PageState state) {
            return new TabNameListModel(state);
        }
    }

    private class TabNameListModel implements ListModel {
        private int m_index = -1;
        private PageState m_pageState;

        public TabNameListModel(PageState ps) {
            m_pageState = ps;
        }

        public Object getElement() {
            return ((Label) m_keys.get(m_index)).getLabel(m_pageState);
        }

        public String getKey() {
            return String.valueOf(m_index);
        }

        public boolean next() {
            return (m_index++ < m_keys.size() - 1);
        }
    }
}

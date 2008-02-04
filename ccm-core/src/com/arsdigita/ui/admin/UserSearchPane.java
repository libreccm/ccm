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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import java.math.BigDecimal;

/**
 * User search panel.
 *
 * @author David Dao
 */

class UserSearchPane extends SegmentedPanel
    implements AdminConstants,
               Resettable,
               ChangeListener
{
    private SearchAndList m_searchAndList;
    private AdminSplitPanel m_splitPanel;
    private UserBrowsePane m_userBrowsePane;

    /**
     * Constructor
     */

    public UserSearchPane (AdminSplitPanel panel,
                           UserBrowsePane  userBrowsePane)
    {
        m_splitPanel     = panel;
        m_userBrowsePane = userBrowsePane;
        m_searchAndList  = makeUserSearch();

        SimpleContainer body = new SimpleContainer();
        body.add(m_searchAndList);

        addSegment(SEARCH_PANEL_HEADER, body);
    }

    public void reset (PageState ps) {
        m_searchAndList.reset(ps);
    }

    private SearchAndList makeUserSearch() {
        SearchAndList s = new SearchAndList("user_search");
        s.addChangeListener(this);
        s.setListModel(new UserSearchAndListModel());
        s.setResultCellRenderer(new ListCellRenderer() {
                public Component getComponent(List list,
                                              PageState state,
                                              Object value,
                                              String key, int index,
                                              boolean isSelected) {
                    return new ControlLink(value.toString());
                }
            });

        return s;
    }

    /**
     * Display user information panel when click on search result.
     */

    public void stateChanged (ChangeEvent e) {
        PageState ps = e.getPageState();
        m_splitPanel.setTab(USER_TAB_BROWSE_INDEX, e.getPageState());

        String id = (String) m_searchAndList.getSelectedKey(ps);
        if (id != null) {
            ps.setValue(USER_ID_PARAM, new BigDecimal(id));
            m_userBrowsePane.displayUserInfoPanel(ps);
        } else {
            reset(ps);
        }
    }

}

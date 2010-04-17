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


import com.arsdigita.ui.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import java.math.BigDecimal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
/**
 *
 * @author David Dao
 * @version $Id: UserSummaryPane.java 1486 2007-03-18 16:47:26Z apevec $
 */
class UserSummaryPane extends SegmentedPanel implements AdminConstants,
                                                        Resettable {

    private UserSummaryBodyPane m_body;

    public UserSummaryPane(AdminSplitPanel panel, UserBrowsePane userBrowsePane) {
        m_body = new UserSummaryBodyPane(panel, userBrowsePane);

        addSegment(SUMMARY_PANEL_HEADER, m_body);

    }

    public void reset(PageState ps) {
        m_body.reset(ps);
    }
}

class UserSummaryBodyPane extends BoxPanel implements AdminConstants,
                                                      ChangeListener,
                                                      ActionListener,
                                                      Resettable {

    private static final String EXCLUDE_GROUP_ID = "excludeGroupId";

    private AdminSplitPanel m_splitPanel;
    private SearchAndList m_searchAndList;
    private UserBrowsePane m_userBrowsePane;

    public UserSummaryBodyPane(AdminSplitPanel panel, UserBrowsePane userBrowsePane) {
        super(BoxPanel.VERTICAL);

        m_splitPanel = panel;
        m_userBrowsePane = userBrowsePane;

        add(createTotalUsersLabel());

        m_searchAndList = makeUserSearch();
        add(m_searchAndList);

        ActionLink createLink = new ActionLink(CREATE_USER_LABEL);
        createLink.setClassAttr("actionLink");
        createLink.addActionListener(this);
        add(createLink);

    }

    private Component createTotalUsersLabel() {
        BoxPanel p = new BoxPanel(BoxPanel.HORIZONTAL);


        p.add(TOTAL_USERS_LABEL);

        Label nResults = new Label(GlobalizationUtil.globalize("ui.admin.nusers"));
        nResults.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    DataQuery query =
                        SessionManager.getSession().retrieveQuery(
                                                                  "com.arsdigita.kernel.RetrieveUsers"
                                                                  );
                    // include all groups
                    query.setParameter(EXCLUDE_GROUP_ID, new BigDecimal(0));
                    // XXX NOT EXISTS ( ... g.group_id  = null ) 
                    // 10g evaluates to null
                    long nUsers = query.size();
                    Label l = (Label) e.getTarget();
                    l.setLabel(Long.toString(nUsers));
                }
            });

        ActionLink nUsersLink = new ActionLink(nResults);
        nUsersLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_splitPanel.setTab(USER_TAB_BROWSE_INDEX, e.getPageState());
                }
            });

        p.add(nUsersLink);

        return p;
    }

    public void actionPerformed(ActionEvent e) {
        m_splitPanel.setTab(USER_TAB_CREATE_USER_INDEX, e.getPageState());
    }


    private SearchAndList makeUserSearch() {
        SearchAndList s = new SearchAndList("user_search");
        s.addChangeListener(this);
        s.setResultCellRenderer(new ListCellRenderer() {
                public Component getComponent(List list,
                                              PageState state,
                                              Object value,
                                              String key, int index,
                                              boolean isSelected) {
                    ControlLink userLink = new ControlLink(value.toString());
                    return userLink;

                }
            });

        s.setListModel(new UserSearchAndListModel());

        return s;
    }

    /**
     * Display user information panel when click on search result.
     */
    public void stateChanged(ChangeEvent e) {
        PageState ps = e.getPageState();
        m_splitPanel.setTab(USER_TAB_BROWSE_INDEX, e.getPageState());
        /* SDM #204846. Contemplate a situation where no user is actually selected */
        String sk = (String) m_searchAndList.getSelectedKey(ps);
        if (null != sk) {
            ps.setValue(USER_ID_PARAM, new BigDecimal(sk));
            m_userBrowsePane.displayUserInfoPanel(e.getPageState());
        }
    }

    public void reset(PageState ps) {
        m_searchAndList.reset(ps);
    }
}

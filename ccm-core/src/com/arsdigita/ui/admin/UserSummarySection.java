/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import static com.arsdigita.ui.admin.AdminConstants.TOTAL_USERS_LABEL;
import static com.arsdigita.ui.admin.AdminConstants.USER_TAB_BROWSE_INDEX;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class UserSummarySection extends BoxPanel implements AdminConstants, Resettable {

    private static final String EXCLUDE_GROUP_ID = "excludeGroupId";
    private final UserAdministrationTab parent;
    private final SearchAndList searchAndList;
    private final UserBrowsePane userBrowsePane;

    public UserSummarySection(final UserAdministrationTab parent, final UserBrowsePane userBrowsePane) {
        super();

        this.parent = parent;
        this.userBrowsePane = userBrowsePane;

        add(createTotalUsersLabel());

        searchAndList = new SearchAndList("user_search");
        searchAndList.addChangeListener(new UserSearchChangeListener());
        searchAndList.setResultCellRenderer(new ListCellRenderer() {
            public Component getComponent(final List list,
                                          final PageState state,
                                          final Object value,
                                          final String key,
                                          final int index,
                                          final boolean isSelected) {
                return new ControlLink(value.toString());
            }

        });

        searchAndList.setListModel(new UserSearchAndListModel());
        add(searchAndList);

        final ActionLink createLink = new ActionLink(CREATE_USER_LABEL);
        createLink.setClassAttr("actionLink");
        createLink.addActionListener(new UserCreateActionListener());
        add(createLink);
    }

    private Component createTotalUsersLabel() {
        final BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);
        panel.add(TOTAL_USERS_LABEL);

        final Label nResults = new Label(com.arsdigita.ui.util.GlobalizationUtil.globalize("ui.admin.nusers"));
        nResults.addPrintListener(new PrintListener() {
            public void prepare(final PrintEvent event) {
                final DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.kernel.RetrieveUsers");
                // include all groups
                query.setParameter(EXCLUDE_GROUP_ID, BigDecimal.ZERO);
                // XXX NOT EXISTS ( ... g.group_id  = null ) 
                // 10g evaluates to null
                final long nUsers = query.size();
                final Label target = (Label) event.getTarget();
                target.setLabel(Long.toString(nUsers));
            }

        });

        final ActionLink nUsersLink = new ActionLink(nResults);
        nUsersLink.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                parent.setSection(USER_TAB_BROWSE_INDEX, event.getPageState());
            }

        });

        panel.add(nUsersLink);

        return panel;
    }

    public void reset(final PageState state) {
        searchAndList.reset(state);
    }
 
    private class UserCreateActionListener implements ActionListener {

        public UserCreateActionListener() {
            //Nothing
        }

        public void actionPerformed(final ActionEvent event) {
            parent.setSection(USER_TAB_CREATE_USER_INDEX, event.getPageState());
        }

    }

    private class UserSearchChangeListener implements ChangeListener {

        public UserSearchChangeListener() {
            //Nothing
        }

        public void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();
            parent.setSection(USER_TAB_BROWSE_INDEX, event.getPageState());
            /* SDM #204846. Contemplate a situation where no user is actually selected */
            final String selectedKey = (String) searchAndList.getSelectedKey(state);
            if (null != selectedKey) {
                state.setValue(USER_ID_PARAM, new BigDecimal(selectedKey));
                userBrowsePane.displayUserInfoPanel(event.getPageState());
            }
        }

    }
}

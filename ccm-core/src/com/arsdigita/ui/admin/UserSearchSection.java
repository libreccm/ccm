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
import static com.arsdigita.ui.admin.AdminConstants.USER_ID_PARAM;
import static com.arsdigita.ui.admin.AdminConstants.USER_TAB_BROWSE_INDEX;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class UserSearchSection extends SegmentedPanel implements AdminConstants, Resettable {

    private final SearchAndList searchAndList;
    private final UserAdministrationTab parent;
    private final UserBrowsePane browsePane;

    public UserSearchSection(final UserAdministrationTab parent, final UserBrowsePane browsePane) {
        super();
        
        this.parent = parent;
        this.browsePane = browsePane;

        searchAndList = new SearchAndList("user_search");
        searchAndList.addChangeListener(new UserSearchChangeListener());
        searchAndList.setListModel(new UserSearchAndListModel());
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

        final SimpleContainer body = new SimpleContainer();
        body.add(searchAndList);

        addSegment(SEARCH_PANEL_HEADER, body);
    }

    public void reset(final PageState state) {
        searchAndList.reset(state);
    }

    private class UserSearchChangeListener implements ChangeListener {

        public void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();
            parent.setSection(USER_TAB_BROWSE_INDEX, event.getPageState());

            final String userId = (String) searchAndList.getSelectedKey(state);
            if (userId == null) {
                reset(state);                
            } else {
                state.setValue(USER_ID_PARAM, new BigDecimal(userId));
                browsePane.displayUserInfoPanel(state);
            }
        }

    }
}

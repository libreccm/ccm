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

import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.List;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.Group;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.kernel.User;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;

/**
 *
 *
 * @author David Dao
 *
 */
class SubMemberPanel extends BoxPanel
    implements AdminConstants {

    private List m_memberList;


    private GroupAdministrationTab m_mainTab;

    public SubMemberPanel(GroupAdministrationTab tab) {
        m_mainTab = tab;
        m_memberList = new List(new SubMemberListModelBuilder(tab));
        m_memberList.setCellRenderer(new ListCellRenderer() {
                public Component getComponent(List list, PageState state, Object value,
                                              String key, int index, boolean isSelected) {
                    BoxPanel p = new BoxPanel(BoxPanel.HORIZONTAL);

                    Label l = new Label(((User) value).getDisplayName());
                    p.add(l);

                    ControlLink removeLink = new ControlLink(REMOVE_SUBMEMBER_LABEL);
                    removeLink.setClassAttr("actionLink");

                    p.add(removeLink);
                    return p;
                }
            });
        m_memberList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();

                    String key = (String) m_memberList.getSelectedKey(ps);

                    if (key != null) {
                        BigDecimal userID = new BigDecimal(key);

                        try {
                            User user = User.retrieve(userID);
                            Group group = m_mainTab.getGroup(ps);
                            if (group != null) {
                                group.removeMember(user);
                                group.save();
                            }
                        } catch (DataObjectNotFoundException exc) {
                        }
                    }

                }
            });
        add(m_memberList);
    }
}

class SubMemberListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    private GroupAdministrationTab m_mainTab;
    public SubMemberListModelBuilder(GroupAdministrationTab tab) {
        m_mainTab = tab;
    }

    public ListModel makeModel(List l, PageState ps) {

        Group group = m_mainTab.getGroup(ps);
        UserCollection coll = null;
        if (group != null) {
            // Get only direct members.
            coll = group.getMemberUsers();
            // order according to the way the name is displayed
            // as defined by toString() of PersonName
            coll.addOrder("lower(name.givenName)");
            coll.addOrder("lower(name.familyName)");

        }
        return new SubMemberListModel(coll);


    }
}

class SubMemberListModel implements ListModel {

    private UserCollection m_coll;

    public SubMemberListModel(UserCollection coll) {
        m_coll = coll;
    }

    public Object getElement() {
        return m_coll.getUser();
    }

    public String getKey() {
        return m_coll.getUser().getID().toString();
    }

    public boolean next() {
        if (m_coll == null) {
            return false;
        }

        return m_coll.next();
    }
}

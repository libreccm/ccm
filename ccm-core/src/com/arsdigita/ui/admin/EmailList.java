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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.User;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Used to display and manage the list of additional email addresses
 * for a user.
 */

class EmailList extends List
    implements ListCellRenderer,
               AdminConstants,
               ActionListener
{

    /**
     * Constructor
     */

    public EmailList() {
        setModelBuilder(new EmailListModelBuilder());
        setCellRenderer(this);
        addActionListener(this);
    }

    public Component getComponent(List list,
                                  PageState state,
                                  Object value,
                                  String key,
                                  int index,
                                  boolean isSelected)
    {
        SimpleContainer c = new SimpleContainer();

        if (value != null) {
            ControlLink link =
                new ControlLink(USER_FORM_DELETE_ADDITIONAL_EMAIL);
            link.setClassAttr("deleteLink");

            c.add(new Label(value.toString()));
            c.add(link);
        }

        return c;
    }

    /**
     * This actionlister is executed when the user clicks the "delete"
     * link next to an email address.
     */

    public void actionPerformed (ActionEvent e) {
        PageState ps = e.getPageState();

        BigDecimal userID = (BigDecimal) ps.getValue(USER_ID_PARAM);
        if (userID != null) {
            User user = null;
            try {
                user = User.retrieve(userID);
            } catch (DataObjectNotFoundException exc) {
                // Ignore this exception
                return;
            }

            String email = (String) getSelectedKey(ps);
            if (email != null) {
                EmailAddress addr = new EmailAddress(email);
                user.removeEmailAddress(addr);
                user.save();
            }
        }
    }
}

class EmailListModelBuilder extends LockableImpl
    implements ListModelBuilder,
               AdminConstants
{

    private class EmailListModel implements ListModel {
        private Iterator m_emails;
        private EmailAddress m_currentEmail;

        public EmailListModel(Iterator emails) {
            m_emails = emails;
        }

        public boolean next() {
            if (m_emails.hasNext()) {
                m_currentEmail = (EmailAddress) m_emails.next();
                return true;
            } else {
                return false;
            }
        }

        public String getKey() {
            return m_currentEmail.getEmailAddress();
        }

        public Object getElement() {
            return m_currentEmail.getEmailAddress();
        }
    }

    public ListModel makeModel(List l, PageState state) {

        BigDecimal userID = (BigDecimal)state.getValue(USER_ID_PARAM);

        if (userID == null) {
            return null;
        }

        User user = User.retrieve(userID);
        // Generate the list of non-primary addresses.
        return new EmailListModel(user.getAlternateEmails());
    }
}

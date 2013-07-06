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

import javax.mail.internet.InternetAddress;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.security.SecurityConfig;

/**
 * Form used to add a new user to the system.
 *
 * @version $Id: UserAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated Replaced by {@link UserCreateSection}. Will be removed soon.
 */
class UserAddForm extends UserForm
        implements FormProcessListener,
                   FormInitListener,
                   AdminConstants {

    private SecurityConfig securityConfig = SecurityConfig.getConfig();
    private AdminSplitPanel m_adminPanel;

    /**
     * Default constructor.
     */
    public UserAddForm(AdminSplitPanel adminPanel) {
        super(USER_FORM_ADD);
        m_adminPanel = adminPanel;

        addInitListener(this);
        addProcessListener(this);

        if (securityConfig.getEnableQuestion()) {
            // Add validation listeners for required parameters        
            // but only if SecurityConfig.getEnableQuestion is true (jensp 2011-10-05)
            m_question.addValidationListener(new NotEmptyValidationListener());
        }
    }

    /**
     *  Initialize the form
     */
    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        showSecurityInfo(state);

        USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST.setVisible(state, false);
        m_emailList.setVisible(state, false);
    }

    /**
     * Process the form.
     */
    public void process(FormSectionEvent e)
            throws FormProcessException {
        PageState state = e.getPageState();

        User user = new User();

        String email =
               ((InternetAddress) m_primaryEmail.getValue(state)).getAddress();

        user.setPrimaryEmail(new EmailAddress(email));
        user.setScreenName((String) m_screenName.getValue(state));

        PersonName name = user.getPersonName();
        name.setGivenName((String) m_firstName.getValue(state));
        name.setFamilyName((String) m_lastName.getValue(state));

        // Check to see if the value has changed from the
        // default.  If not just leave this set to null.

        String uri = (String) m_url.getValue(state);
        if (!uri.equals(USER_FORM_INPUT_URL_DEFAULT) && !uri.equals("")) {
            user.setURI(uri);
        }

        // Add optional additional email address

        InternetAddress additional =
                        (InternetAddress) m_additionalEmail.getValue(state);
        if (additional != null) {
            user.addEmailAddress(new EmailAddress(additional.getAddress()));
        }

        // Make new user persistent

        user.save();

        // Save user authentication credentials.

        UserAuthentication auth =
                           UserAuthentication.createForUser(user);

        auth.setPassword((String) m_password.getValue(state));
        if (securityConfig.getEnableQuestion()) {
            auth.setPasswordQuestion((String) m_question.getValue(state));
            auth.setPasswordAnswer((String) m_answer.getValue(state));
        }
        auth.save();

        // Switch to browse tab.
        m_adminPanel.setTab(USER_TAB_BROWSE_INDEX, state);
    }
}

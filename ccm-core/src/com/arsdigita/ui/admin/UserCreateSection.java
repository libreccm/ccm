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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.security.SecurityConfig;
import static com.arsdigita.ui.admin.AdminConstants.USER_FORM_INPUT_URL_DEFAULT;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class UserCreateSection extends UserForm implements AdminConstants, Resettable {

    private final SecurityConfig securityConfig = SecurityConfig.getConfig();
    private final UserAdministrationTab parent;

    public UserCreateSection(final UserAdministrationTab parent) {
        super(USER_FORM_ADD);

        this.parent = parent;

        addInitListener(new UserCreateInitListener());
        addProcessListener(new UserCreateProcessListener());

        if (securityConfig.getEnableQuestion()) {
            // Add validation listeners for required parameters        
            // but only if SecurityConfig.getEnableQuestion is true (jensp 2011-10-05)
            m_question.addValidationListener(new NotEmptyValidationListener());
        }

    }

    public void reset(final PageState state) {
        state.setValue(USER_ID_PARAM, null);
    }

    private class UserCreateInitListener implements FormInitListener {

        public UserCreateInitListener() {
            //Nothing
        }

        public void init(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            showSecurityInfo(state);

            USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST.setVisible(state, false);
            m_emailList.setVisible(state, false);
        }

    }

    private class UserCreateProcessListener implements FormProcessListener {

        public UserCreateProcessListener() {
            //Nothing
        }

        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            final User user = new User();

            final String email = ((InternetAddress) m_primaryEmail.getValue(state)).getAddress();

            user.setPrimaryEmail(new EmailAddress(email));
            user.setScreenName((String) m_screenName.getValue(state));

            final PersonName personName = user.getPersonName();
            personName.setGivenName((String) m_firstName.getValue(state));
            personName.setFamilyName((String) m_lastName.getValue(state));

            // Check to see if the value has changed from the
            // default.  If not just leave this set to null.
            final String uri = (String) m_url.getValue(state);
            if (!USER_FORM_INPUT_URL_DEFAULT.equals(uri) && !"".equals(uri)) {
                user.setURI(uri);
            }
            // Add optional additional email address
            final InternetAddress additional = (InternetAddress) m_additionalEmail.getValue(state);
            if (additional != null) {
                user.addEmailAddress(new EmailAddress(additional.getAddress()));
            }

            // Make new user persistent
            user.save();

            // Save user authentication credentials.
            final UserAuthentication auth = UserAuthentication.createForUser(user);

            auth.setSSOlogin((String) m_ssoLogin.getValue(state));
            
            auth.setPassword((String) m_password.getValue(state));
            if (securityConfig.getEnableQuestion()) {
                auth.setPasswordQuestion((String) m_question.getValue(state));
                auth.setPasswordAnswer((String) m_answer.getValue(state));
            }
            auth.save();

            // Switch to browse tab.
            parent.setSection(USER_TAB_BROWSE_INDEX, state);
        }

    }
}

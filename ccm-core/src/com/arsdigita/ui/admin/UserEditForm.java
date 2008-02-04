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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import javax.mail.internet.InternetAddress;
import org.apache.log4j.Logger;

/**
 * Form used to edit the information for a user.
 *
 * @version $Id: UserEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */

class UserEditForm extends UserForm
    implements FormInitListener,
               FormProcessListener,
               AdminConstants
{
    // Logging

    private static final Logger s_log = Logger.getLogger(UserEditForm.class);

    private UserBrowsePane m_browsePane;

    /**
     * Constructor
     */

    public UserEditForm(UserBrowsePane browsePane) {
        super(USER_FORM_EDIT);
        m_browsePane = browsePane;

        addInitListener(this);
        addProcessListener(this);
    }

    /**
     * Initialize the form
     */

    public void init(FormSectionEvent e) {

        PageState state = e.getPageState();

        hideSecurityInfo(state);

        User user = m_browsePane.getUser(state);

        PersonName name = user.getPersonName();
        m_firstName.setValue(state, name.getGivenName());
        m_lastName.setValue(state, name.getFamilyName());

        m_primaryEmail.setValue(state, user.getPrimaryEmail().toString());

        if (user.getURI() != null && !user.getURI().equals("")) {
            m_url.setValue(state, user.getURI());
        }

        m_screenName.setValue(state, user.getScreenName());

        USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST.setVisible(state, true);
        m_emailList.setVisible(state, true);
    }


    /**
     * Process the form
     */

    public void process (FormSectionEvent e)
        throws FormProcessException
    {
        PageState state = e.getPageState();

        User user = m_browsePane.getUser(state);

        PersonName name = user.getPersonName();
        name.setGivenName((String) m_firstName.getValue(state));
        name.setFamilyName((String) m_lastName.getValue(state));

        // Check to see if the value has changed from the default.  If
        // not just leave this set to null.

        String uri = (String) m_url.getValue(state);
        if (!uri.equals(USER_FORM_INPUT_URL_DEFAULT) && !uri.equals("")) {
            user.setURI(uri);
        }

        user.setScreenName((String) m_screenName.getValue(state));

        InternetAddress additional =
            (InternetAddress) m_additionalEmail.getValue(state);
        if (additional != null) {
            user.addEmailAddress
                (new EmailAddress(additional.getAddress()));
        }

        // Check to see if the primary email address has changed, and
        // if so set it to the new value and delete the association
        // with the old.  If it hasn't change don't do anything.

        EmailAddress oaddr = user.getPrimaryEmail();
        EmailAddress naddr = new EmailAddress(m_primaryEmail.getValue(state).toString());

        if (!oaddr.equals(naddr)) {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Changing primary email " + oaddr + " to " +
                             naddr );
            }

            user.setPrimaryEmail(naddr);
            //user.save();
            user.removeEmailAddress(oaddr);
        }

        user.save();

        m_browsePane.displayUserInfoPanel(state);
    }
}

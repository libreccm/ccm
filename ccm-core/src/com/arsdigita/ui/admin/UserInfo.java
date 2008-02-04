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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;

import com.arsdigita.xml.Element;

import java.util.Iterator;

/**
 * A Bebop component that holds information on one user. The XML
 * format for this element is:
 *
 * <pre>
 * &lt;userInfo
 *     id
 *     name
 *     email
 *     screenName
 *     URI
 *     memberState&gt;
 *     &lt;additionalEmails&gt;...&lt;/additionalEmails&gt;
 * &lt;/userInfo&gt;
 * </pre>
 *
 * @version $Id: UserInfo.java 1169 2006-06-14 13:08:25Z fabrice $
 */

class UserInfo extends SimpleComponent implements AdminConstants {

    /**
     * The element represent by this component.
     */

    private Element m_elmt;

    private UserBrowsePane m_parent;

    public UserInfo(UserBrowsePane parent) {
        m_parent = parent;
    }

    /**
     * generates userInfo element.
     *
     * @param state The current page state
     * @param parent The parent element in the DOM that we add this element to
     *
     * @author Kevin Scaldeferri 
     */
    public void generateXML(PageState state, Element parent) {

        m_elmt = new Element("admin:userInfo", ADMIN_XML_NS);
        parent.addContent(m_elmt);

        User user = m_parent.getUser(state);

        m_elmt.addAttribute("id", user.getID().toString());
        m_elmt.addAttribute("name", user.getDisplayName());
        m_elmt.addAttribute("screenName", user.getScreenName());
        m_elmt.addAttribute("URI", user.getURI());

        // Add the member state (requires some additional checking)

        addMemberState(user);

        // Build the list of email addresses for this user.  We set a
        // special flag for the primary email address.

        EmailAddress primary = user.getPrimaryEmail();
        addEmail(primary,true);

        Iterator iter = user.getAlternateEmails();
        while (iter.hasNext()) {
            addEmail((EmailAddress) iter.next(), false);
        }

    }

    /**
     * Helper method to generate an email element and add it to the
     * UserInfo element.
     */

    private void addEmail (EmailAddress email,
                           boolean isPrimary)
    {
        Element elmt = new Element("admin:email", ADMIN_XML_NS);
        elmt.addAttribute("address", email.getEmailAddress());

        if (isPrimary) {
            elmt.addAttribute("primary", "t");
        }

        m_elmt.addContent(elmt);
    }

    /**
     * Helper method to add the correct member state to the UserInfo
     * element.  Sets it to one of the following:
     *
     * <ul>
     * <li><b>approved</b> if the user has a valid authentication
     * record</li>
     * </li><b>unauthorized</b> if the user does not have a valid
     * authentication record</li>
     * </ul>
     *
     * <p><b>Note</b>: when user states are implemented this code will
     * change to do a simple lookup of the current user state.
     * 
     * TODO - Should these values (approved/unauthorised/banned) be generated from a resource file
     * 
     */
    private void addMemberState (User user) {

        String state = "approved";

        try {
            UserAuthentication.retrieveForUser(user);
        } catch (DataObjectNotFoundException ex) {
            state = "unauthorized";
        }

		if (user.isBanned()) {
			state = "banned";
		}

        m_elmt.addAttribute("memberState", state);
    }

}

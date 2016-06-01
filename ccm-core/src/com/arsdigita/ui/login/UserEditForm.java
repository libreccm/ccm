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
package com.arsdigita.ui.login;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
// import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.ui.UI;
import com.arsdigita.web.URL;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.util.UncheckedWrapperException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Edits a user.  If returnURL is passed in to the form, then redirects to
 * that URL; otherwise redirects to the user workspace.
 *
 *
 * @author Sameer Ajmani
 *
 * @version $Id: UserEditForm.java 738 2005-09-01 12:36:52Z sskracic $
 *
 **/
public class UserEditForm extends UserForm
    implements FormProcessListener
{

    private static final Logger s_log =
        Logger.getLogger(UserEditForm.class);

    private UserAuthenticationListener m_listener =
        new UserAuthenticationListener();
    private Hidden m_returnURL;
    private RequestLocal m_user = new RequestLocal() {
            public Object initialValue(PageState ps) {
                User result;
                try {
                    result = User.retrieve(m_listener.getUser(ps).getOID());
                } catch (DataObjectNotFoundException e) {
                    result = null;
                }
                return result;
            }
        };

    public UserEditForm() {
        super("user-edit", new ColumnPanel(2), false);

        addProcessListener(this);

        // exportUsers return URL
        m_returnURL = new Hidden(new URLParameter
                                 (LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);
    }

    public void register(Page p) {
        super.register(p);
        p.addRequestListener(m_listener);
    }

    protected User getUser(PageState state)
        throws DataObjectNotFoundException {
        return (User) m_user.get(state);
    }

    public void process(FormSectionEvent event)
        throws FormProcessException {
        FormData  data  = event.getFormData();
        PageState state = event.getPageState();

        User user;
        try {
            user = getUser(state);
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }

        PersonName name = user.getPersonName();
        name.setGivenName((String) m_firstName.getValue(state));
        name.setFamilyName((String) m_lastName.getValue(state));

        user.setURI((String) m_url.getValue(state));
        user.setScreenName((String) m_screenName.getValue(state));

        // TODO: support list of email addresses (see admin UI)
        //InternetAddress additional =
        //    (InternetAddress) m_additional.getValue(state);
        //if (additional != null) {
        //    user.addEmailAddress
        //        (new EmailAddress(additional.getAddress()));
        //}

        // Bug #166274: Unexpected behavior when editing
        // primary email.
        //
        // Check to see if the primary email address has
        // changed, and if so set it to the new value and
        // delete the association with the old.  If it
        // hasn't change don't do anything.

        EmailAddress oaddr = user.getPrimaryEmail();
        EmailAddress naddr = new EmailAddress(data.get(FORM_EMAIL).toString());
        if (!oaddr.equals(naddr)) {
            user.setPrimaryEmail(naddr);
            user.save();
            user.removeEmailAddress(oaddr);
        }

        user.save();

        // redirect to workspace or return URL, if specified

        final HttpServletRequest req = state.getRequest();

//      final String path = LegacyInitializer.getFullURL
//          (LegacyInitializer.WORKSPACE_PAGE_KEY, req);
        final String path = UI.getWorkspaceURL();

        final URL fallback = com.arsdigita.web.URL.there(req, path);

        throw new ReturnSignal(req, fallback);
    }
}

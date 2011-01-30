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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.persistence.PersistenceException;

import javax.mail.internet.InternetAddress;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Creates a new user.  Collects user's basic info, such as email, password,
 * first name, last name, etc; then tries to create the user in the
 * database.  If returnURL is passed in to the form, then redirects to that
 * URL; otherwise redirects to the user workspace.
 *
 *
 * @author Michael Bryzek
 * @author Roger Hsueh
 * @author Sameer Ajmani
 *
 * @version $Id: UserNewForm.java 738 2005-09-01 12:36:52Z sskracic $
 *
 **/
public class UserNewForm extends UserForm
        implements FormInitListener,
        FormProcessListener,
        FormValidationListener
{
    public static final String versionId = "$Id: UserNewForm.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
            Logger.getLogger(UserNewForm.class);

    static final String FORM_NAME = "user-new";

    private Hidden m_loginName;
    private Hidden m_returnURL;
    private Hidden m_persistent;

    public UserNewForm() {
        this(new ColumnPanel(2));
    }

    protected User getUser(PageState state)
            throws DataObjectNotFoundException {
        return null; // don't load any data into form
    }

    public UserNewForm(Container panel) {
        super(FORM_NAME, panel, true);

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        // save return URL
        m_returnURL = new Hidden(new URLParameter
                (LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        // save email address or screen name
        m_loginName = new Hidden(new StringParameter(FORM_LOGIN));
        m_loginName.setPassIn(true);
        add(m_loginName);

        // save persistent flag
        ArrayParameter cookieP = new ArrayParameter(FORM_PERSISTENT_LOGIN_P);
        m_persistent = new Hidden(cookieP);
        m_persistent.setPassIn(true);
        add(m_persistent);
    }

    public void init(FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();
        // clear passwords from form data
        m_password.setValue(state, "");
        m_confirm.setValue(state, "");
        String loginName = (String)m_loginName.getValue(state);
        if (loginName != null) {
            if (KernelHelper.emailIsPrimaryIdentifier()) {
                m_email.setValue(state, loginName);
            } else {
                m_screenName.setValue(state, loginName);
            }
        }
    }

    public void process(FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();


        final InternetAddress address = (InternetAddress)m_email.getValue(state);
        final String email = address.getAddress();

        // TODO: set additional emails
        final String password = (String)m_password.getValue(state);
        final String question = (String)m_question.getValue(state);
        final String answer = (String)m_answer.getValue(state);
        final String firstName = (String)m_firstName.getValue(state);
        final String lastName = (String)m_lastName.getValue(state);
        String sn = null;
        if (!KernelHelper.emailIsPrimaryIdentifier()) {
            sn = (String)m_screenName.getValue(state);
        }
        final String screenName = sn;
        String url = (String)m_url.getValue(state);
        if ("http://".equals(url)) {
            url = null;
        }

        final String urlFinal = url;
        final Exception[] formExceptions = new Exception[] { null };

        KernelExcursion rootExcursion = new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                try {
                    // create the user
                    User user = new User();
                    user.getPersonName().setGivenName(firstName);
                    user.getPersonName().setFamilyName(lastName);
                    user.setPrimaryEmail(new EmailAddress(email));
                    if (!KernelHelper.emailIsPrimaryIdentifier()) {
                        user.setScreenName(screenName);
                    }
                    user.setURI(urlFinal);
                    user.save();

                    PermissionService.grantPermission(new PermissionDescriptor
                            (PrivilegeDescriptor.ADMIN,
                                    user,
                                    user));

                    // create user's authentication information
                    UserAuthentication auth = null;
                    auth = UserAuthentication.createForUser(user);
                    auth.setPassword(password);
                    auth.setPasswordQuestion(question);
                    auth.setPasswordAnswer(answer);
                    auth.save();
                } catch (PersistenceException e) {
                    // problem with creating new User or retrieving
                    // UserAuthentication object
                    formExceptions[0] = e;
                }
            }};

        rootExcursion.run();

        if (formExceptions[0] != null) {
            throw new FormProcessException(formExceptions[0]);
        }


        try {
            // finally log the user in (sets the
            // appropriate session or permanent cookie)
            String loginName = email;
            if (!KernelHelper.emailIsPrimaryIdentifier()) {
                loginName = screenName;
            }
            Web.getUserContext().login
                (email, password.toCharArray(),
                 getPersistentLoginValue(state, false));
        } catch (LoginException e) {
            // ERROR: login failed for new user
            s_log.error("login failed for new user", e);
            throw new FormProcessException(e);
        }

        // redirect to workspace or return URL, if specified
        final HttpServletRequest req = state.getRequest();

        url = LegacyInitializer.getFullURL
                (LegacyInitializer.WORKSPACE_PAGE_KEY, req);

        final URL fallback = com.arsdigita.web.URL.there(req, url);

        throw new ReturnSignal(req, fallback);
    }

    protected boolean getPersistentLoginValue
        (PageState state, boolean defaultValue) {
        // CheckboxGroup gets you a StringArray
        String[] values = (String[])m_persistent.getValue(state);
        if (values == null) {
            return defaultValue;
        }

        String persistentLoginValue = (String)values[0];
        return "1".equals(persistentLoginValue);
    }
}

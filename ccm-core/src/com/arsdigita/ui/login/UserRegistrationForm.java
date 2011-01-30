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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ElementComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.security.AccountNotFoundException;
import com.arsdigita.kernel.security.Credential;
import com.arsdigita.kernel.security.CredentialException;
import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import javax.mail.internet.InternetAddress;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * A Bebop form that accepts login and password from the user and attempts
 * to authenticate and then log in the user.
 *
 * @author Roger Hsueh
 * @author Michael Bryzek
 * @author Sameer Ajmani
 *
 * @version $Id: UserRegistrationForm.java 1230 2006-06-22 11:50:59Z apevec $
 *
 **/

public class UserRegistrationForm extends Form
        implements LoginConstants, FormInitListener,
        FormValidationListener, FormProcessListener
{

    public static final String versionId = "$Id: UserRegistrationForm.java 1230 2006-06-22 11:50:59Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
            Logger.getLogger(UserRegistrationForm.class);

    // package friendly static form name makes writing HttpUnitTest easier
    final static String FORM_NAME = "user-login";

    private CheckboxGroup m_isPersistent;
    private Hidden m_timestamp;
    private Hidden m_returnURL;
    private TextField m_loginName;
    private Password m_password;
    private boolean m_autoRegistrationOn;

    public UserRegistrationForm() {
        this(true);
    }

    public UserRegistrationForm(Container panel) {
        this(panel, true);
    }

    public UserRegistrationForm(boolean autoRegistrationOn) {
        this(new BoxPanel(), autoRegistrationOn);
    }

    public UserRegistrationForm(Container panel, boolean autoRegistrationOn) {
        super(FORM_NAME, panel);

        setMethod(Form.POST);
        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        m_autoRegistrationOn = autoRegistrationOn;

        m_timestamp = new Hidden(new StringParameter
                (FORM_TIMESTAMP));
        add(m_timestamp);

        m_returnURL = new Hidden(new URLParameter
                (LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        setupLogin();

        add(new Label(LoginHelper.getMessage
                ("login.userRegistrationForm.password")));
        m_password = new Password(new StringParameter(FORM_PASSWORD));
        // Since new users should not enter a password, allow null.
        //m_password.addValidationListener(new NotNullValidationListener());
        add(m_password);

        SimpleContainer cookiePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        m_isPersistent =
            new CheckboxGroup(FORM_PERSISTENT_LOGIN_P);
        Label optLabel =
                new Label(LoginHelper.getMessage
                ("login.userRegistrationForm.cookieOption"));
        Option opt = new Option(FORM_PERSISTENT_LOGIN_P_DEFAULT, optLabel);
        m_isPersistent.addOption(opt);
        if (Kernel.getConfig().isLoginRemembered()) {
            m_isPersistent.setOptionSelected(FORM_PERSISTENT_LOGIN_P_DEFAULT);
        }
        cookiePanel.add(m_isPersistent);
        cookiePanel.add(new DynamicLink
                ("login.userRegistrationForm.explainCookieLink",
                        LegacyInitializer.COOKIES_PAGE_KEY));
        add(cookiePanel);

        add(new Submit(SUBMIT), ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        add(new DynamicLink("login.userRegistrationForm.forgotPasswordLink",
                LegacyInitializer.RECOVER_PAGE_KEY));

        if (m_autoRegistrationOn) {
            add(new DynamicLink("login.userRegistrationForm.newUserRegister",
                                LegacyInitializer.NEWUSER_PAGE_KEY));
        }

        add(new ElementComponent("subsite:promptToEnableCookiesMsg",
                SubsiteDispatcher.SUBSITE_NS_URI));
    }


    /**
     * Sets up the login form parameters
     */
    private void setupLogin() {
        SimpleContainer loginMessage =
                new SimpleContainer("subsite:loginPromptMsg",
                        SubsiteDispatcher.SUBSITE_NS_URI);


        if (KernelHelper.emailIsPrimaryIdentifier()){
            loginMessage.setClassAttr("email");
        } else {
            loginMessage.setClassAttr("screenName");
        }

        add(loginMessage);

        if (KernelHelper.emailIsPrimaryIdentifier()){
            add(new Label(LoginHelper.getMessage
                    ("login.userRegistrationForm.email")));
            m_loginName = new TextField(new EmailParameter(FORM_LOGIN));
            addInitListener(new EmailInitListener
                    ((EmailParameter)m_loginName.getParameterModel()));
        } else {
            add(new Label(LoginHelper.getMessage
                    ("login.userRegistrationForm.screenName")));
            m_loginName = new TextField(new StringParameter(FORM_LOGIN));
            addInitListener(new ScreenNameInitListener
                    ((StringParameter)m_loginName.getParameterModel()));
        }
        m_loginName.addValidationListener(new NotNullValidationListener());
        add(m_loginName);
    }

    public void init(FormSectionEvent event)
            throws FormProcessException {
        s_log.info( "In init" );
        if (Kernel.getConfig().isSSOenabled()) {
            // try SSO login
            s_log.info("trying SSO");
            try {
                Web.getUserContext().loginSSO();
                s_log.info("loginSSO ok, now processing redirect_url");
                process(event);
                return;
            } catch (LoginException le) {
                // continue with standard form-based login
                s_log.debug("SSO failed",le);
            }
        }
        try {
            // create timestamp
            String value = Credential
                    .create(FORM_TIMESTAMP, 1000 * TIMESTAMP_LIFETIME_SECS)
                    .toString();
            m_timestamp.setValue(event.getPageState(), value);
        } catch (CredentialException e) {
            s_log.debug("Could not create timestamp", e);
            throw new FormProcessException
                    ("Could not create timestamp", e);
        }
    }

    public void validate(FormSectionEvent event)
            throws FormProcessException  {

        s_log.debug( "In validate" );

        FormData data = event.getFormData();
        PageState state = event.getPageState();
        try {
            // check timestamp
            try {
                Credential.parse((String)m_timestamp.getValue(state));
            } catch (CredentialException e) {
                s_log.info( "Invalid credential" );

                final String path = LegacyInitializer.getFullURL
                        (LegacyInitializer.EXPIRED_PAGE_KEY, state.getRequest());

                final URL url = com.arsdigita.web.URL.there
                        (state.getRequest(), path);

                throw new RedirectSignal(url, false);
            }
            // log in the user
            if (m_loginName.getValue(state) != null) {
                loginUser(event);
            }
        } finally {
            if (!data.isValid()) {
                // clear password from form data
                m_password.setValue(state, "");
            }
        }
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        s_log.debug( "In process" );

        final PageState state = event.getPageState();
        final HttpServletRequest req = state.getRequest();
        
	// Redirect to workspace or return URL, if specified.
        final String path = LegacyInitializer.getFullURL
                (LegacyInitializer.LOGIN_REDIRECT_PAGE_KEY, req);

        final URL url = com.arsdigita.web.URL.there(req, path);

        throw new ReturnSignal(req, url);
    }

    /**
     * Logs in the user using the username, password, and "Remember this
     * login?" request stored in the given form event.  Subclasses can
     * override this method or just one of the specific case handlers
     * (onLoginSuccess, onBadPassword, onAccountNotFound, onLoginException).
     *
     * @throws FormProcessException if there is an unexpected login error
     **/
    protected void loginUser(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();

        try {
            UserContext ctx = Web.getUserContext();
            String username = null;
            if (KernelHelper.emailIsPrimaryIdentifier()) {
                username = ((InternetAddress) m_loginName.getValue(state))
                    .getAddress();
            } else {
                username = (String) m_loginName.getValue(state);
            }

            char[] password = ((String)m_password.getValue(state))
                .trim().toCharArray();
            boolean forever = getPersistentLoginValue(event.getPageState(), false);
            // attempt to log in user
            ctx.login(username, password, forever);
            onLoginSuccess(event);
        } catch (FailedLoginException e) {
            onLoginFail(event, e);
        } catch (AccountNotFoundException e) {
            if ( m_autoRegistrationOn) {
                onAccountNotFound(event, e);
            } else {
                onLoginFail(event, e);
            }
        } catch (LoginException e) {
            onLoginException(event, e);
        }
    }

    /**
     * Executed when login succeeds.  Default implementation does nothing.
     **/
    protected void onLoginSuccess(FormSectionEvent event)
            throws FormProcessException {
        // do nothing
    }


    protected void onBadPassword(FormSectionEvent event,
                                 FailedLoginException e)
        throws FormProcessException {
        onLoginFail(event, e);
    }

    /**
     * Executed when login fails with a bad password or when
     * autoLoginOn is set to false and the user doesn't exist.
     * Default implementation marks password parameter with an error
     * message.
     **/

    protected void onLoginFail(FormSectionEvent event,
                               LoginException e)
        throws FormProcessException {
        s_log.debug("Login fail");
        event.getFormData().addError
            ( (String)ERROR_LOGIN_FAIL
             .localize(event.getPageState().getRequest()));
    }

    /**
     * Executed when login fails for an unrecognized user.  Default
     * implementation sets a flag so that the client is redirected to the
     * new user page (see the process() method code).
     **/
    protected void onAccountNotFound(FormSectionEvent event,
                                     AccountNotFoundException e)
        throws FormProcessException {
        PageState state = event.getPageState();

        // no such user, so bring up form for new users
        s_log.debug("Account not found: " + m_autoRegistrationOn);

        if (m_autoRegistrationOn) {
            s_log.debug("autoRegOn");
            redirectToNewUserPage(state);
        } else {
            s_log.debug("About to goto loginFail");
            onLoginFail(event, e);
        }
    }

    /**
     * Executed when login fails for an unrecognized problem.  Default
     * implementation logs the error and throws FormProcessException.
     **/
    protected void onLoginException(FormSectionEvent event,
                                    LoginException e)
            throws FormProcessException {
        // unexpected error happened during login
        s_log.error("Login failed", e);
        throw new FormProcessException(e);
    }

    /**
     * Determines whether a persistent cookie is requested in the given
     * form.
     *
     * @return true if the specified formdata has a field named
     * FORM_PERSISTENT_LOGIN_P whose value is equal to "1". If there
     * is no such field in the form data, returns the specified default
     * value.
     **/
    protected boolean getPersistentLoginValue
        (PageState state, boolean defaultValue) {
        // CheckboxGroup gets you a StringArray
        String[] values = (String[])m_isPersistent.getValue(state);
        if (values == null) {
            return defaultValue;
        }

        String persistentLoginValue = (String)values[0];
        return "1".equals(persistentLoginValue);
    }

    protected void redirectToNewUserPage(PageState state) {
        String url = LegacyInitializer.getFullURL
            (LegacyInitializer.NEWUSER_PAGE_KEY, state.getRequest());
        
        ParameterMap map = new ParameterMap();
        map.setParameter(LoginHelper.RETURN_URL_PARAM_NAME,
                         m_returnURL.getValue(state));
        map.setParameter(FORM_PERSISTENT_LOGIN_P,
                         m_isPersistent.getValue(state));
        map.setParameter(FORM_EMAIL,
                         m_loginName.getValue(state));
        
        final URL dest = com.arsdigita.web.URL.there(
            state.getRequest(), url, map);
            
        throw new RedirectSignal(dest, true);
    }
}

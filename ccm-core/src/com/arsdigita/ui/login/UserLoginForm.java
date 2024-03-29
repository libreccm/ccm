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

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.AccountNotFoundException;
import com.arsdigita.kernel.security.Credential;
import com.arsdigita.kernel.security.CredentialException;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.ui.UI;
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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Bebop form that accepts login and password from the user and attempts to
 * authenticate and then log in the user.
 *
 * Depending on security configuration it may generate a link to a NewUser
 * registration form, where a new user may register itself. LoginServlet has to
 * ensure that this page is created appropriately and is available.
 *
 * According to documentation in r1230 Simple SSO implementation: /ccm/register
 * first tries to do SSO login, falling back to normal form-based login. Set
 * waf.sso_login=true only after you make sure webapp can *only* be accessed
 * through the frontend webserver doing the authentication.
 *
 * To make this work with Tomcat/mod_jk/Apache HTTPD: - use latest mod_jk
 * (tested with 1.2.15) - add attribute Connector@tomcatAuthentication="false"
 * to JK definition in server.xml
 *
 * @author Roger Hsueh
 * @author Michael Bryzek
 * @author Sameer Ajmani
 *
 * @version $Id: UserLoginForm.java 1230 2006-06-22 11:50:59Z apevec $
 */
public class UserLoginForm 
    extends Form
    implements LoginConstants,
               FormInitListener,
               FormValidationListener,
               FormProcessListener {

    private static final Logger s_log
                                    = Logger.getLogger(UserLoginForm.class);

    // package friendly static form name makes writing HttpUnitTest easier
    final static String FORM_NAME = "user-login";
    private CheckboxGroup m_isPersistent;
    private Hidden m_timestamp;
    private SaveCancelSection m_saveCancelSection;
    private Hidden m_returnURL;
    private TextField m_loginName;
    private Password m_password;
    private boolean m_autoRegistrationOn;
    private final SecurityConfig securityConfig = SecurityConfig.getConfig();
    
    private boolean ssoSuccessful = false;

    /**
     * Default constructor delegates to a constructor which creates a LoginForm
     * without a link to a newUserRegistrationForm.
     */
    public UserLoginForm() {
        this(true);
    }

    public UserLoginForm(Container panel) {
        this(panel, true);
    }

    public UserLoginForm(boolean autoRegistrationOn) {
        this(new BoxPanel(), autoRegistrationOn);
    }

    /**
     * Constructor which does the real work, other constructors delegate to it.
     *
     * @param panel
     * @param autoRegistrationOn
     */
    public UserLoginForm(Container panel, boolean autoRegistrationOn) {
        super(FORM_NAME, panel);

        setMethod(Form.POST);
        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);

        m_autoRegistrationOn = autoRegistrationOn;

        m_timestamp = new Hidden(new StringParameter(FORM_TIMESTAMP));
        add(m_timestamp);

        m_returnURL = new Hidden(new URLParameter(
            LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);

        setupLogin();

        add(new Label(LoginHelper.getMessage(
            "login.userRegistrationForm.password")));
        m_password = new Password(new StringParameter(FORM_PASSWORD));
        // Since new users should not enter a password, allow null.
        //m_password.addValidationListener(new NotNullValidationListener());
        add(m_password);

        SimpleContainer cookiePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        m_isPersistent = new CheckboxGroup(FORM_PERSISTENT_LOGIN_P);
        Label optLabel = new Label(LoginHelper.getMessage(
            "login.userRegistrationForm.cookieOption"));
        Option opt = new Option(FORM_PERSISTENT_LOGIN_P_DEFAULT, optLabel);
        m_isPersistent.addOption(opt);
        if (Kernel.getConfig().isLoginRemembered()) {
            m_isPersistent.setOptionSelected(FORM_PERSISTENT_LOGIN_P_DEFAULT);
        }
        cookiePanel.add(m_isPersistent);

        cookiePanel.add(new DynamicLink(
            "login.userRegistrationForm.explainCookieLink",
            LoginServlet.getCookiesExplainPageURL()));
        add(cookiePanel);

        //add(new Submit(SUBMIT), ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);
        m_saveCancelSection = new SaveCancelSection(new SimpleContainer());
        m_saveCancelSection.getSaveButton().setButtonLabel(LOGIN);
        add(m_saveCancelSection);

        if (securityConfig.getEnableQuestion()) {
            add(new DynamicLink("login.userRegistrationForm.forgotPasswordLink",
                                LoginServlet.getRecoverPasswordPageURL()));
        }

        if (m_autoRegistrationOn) {
            add(new DynamicLink("login.userRegistrationForm.newUserRegister",
                                LoginServlet.getNewUserPageURL()));
        }

        add(new ElementComponent("subsite:promptToEnableCookiesMsg",
                                 LoginServlet.SUBSITE_NS_URI));
    }

    /**
     * Sets up the login form parameters
     */
    private void setupLogin() {
        SimpleContainer loginMessage
                            = new SimpleContainer("subsite:loginPromptMsg",
                                                  LoginServlet.SUBSITE_NS_URI);

        if (Kernel.getConfig().emailIsPrimaryIdentifier()) {
            loginMessage.setClassAttr("email");
        } else {
            loginMessage.setClassAttr("screenName");
        }

        add(loginMessage);

        if (Kernel.getConfig().emailIsPrimaryIdentifier()) {
            add(new Label(LoginHelper.getMessage(
                "login.userRegistrationForm.email")));
            m_loginName = new TextField(new EmailParameter(FORM_LOGIN));
            addInitListener(new EmailInitListener((EmailParameter) m_loginName.
                getParameterModel()));
        } else {
            add(new Label(LoginHelper.getMessage(
                "login.userRegistrationForm.screenName")));
            m_loginName = new TextField(new StringParameter(FORM_LOGIN));
            addInitListener(new ScreenNameInitListener(
                (StringParameter) m_loginName.
                    getParameterModel()));
        }
        //m_loginName.addValidationListener(new NotNullValidationListener());
        add(m_loginName);
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void init(FormSectionEvent event)
        throws FormProcessException {
        s_log.info("In init");
        if (Kernel.getConfig().isSSOenabled()) {
            // try SSO login
            s_log.info("trying SSO");
            try {
                Web.getUserContext().loginSSO();
                s_log.info("loginSSO ok, now processing redirect_url");
                ssoSuccessful = true;
                process(event);
                return;
            } catch (LoginException le) {
                // continue with standard form-based login
                s_log.debug("SSO failed", le);
            }
        }
        if (securityConfig.getEnableSaml()) {
            // ToDo Try SAML login via OneLogin
        }
        try {
            // create timestamp
            String value = Credential.create(FORM_TIMESTAMP,
                                             1000 * TIMESTAMP_LIFETIME_SECS).
                toString();
            m_timestamp.setValue(event.getPageState(), value);
        } catch (CredentialException e) {
            s_log.debug("Could not create timestamp", e);
            throw new FormProcessException(LoginGlobalizationUtil.globalize(
                "login.userLoginForm.couldnt_create_timestamp"));
        }
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void validate(FormSectionEvent event)
        throws FormProcessException {

        s_log.debug("In validate");

        FormData data = event.getFormData();
        PageState state = event.getPageState();
        if (m_saveCancelSection.getSaveButton().isSelected(state)) {

            //check if an email adress has been entered at all
            final Object loginname = m_loginName.getValue(state);
            if (loginname == null) {
                data.addError(ERROR_NO_EMAIL);
            }

            try {
                // check timestamp
                try {
                    Credential.parse((String) m_timestamp.getValue(state));
                } catch (CredentialException e) {
                    s_log.info("Invalid credential");

                    final String path = LoginServlet.getLoginExpiredPageURL();
                    final URL url = com.arsdigita.web.URL.there(state
                        .getRequest(),
                                                                path);

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
    }

    /**
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void process(FormSectionEvent event) throws FormProcessException {
        s_log.debug("In process");

        final PageState state = event.getPageState();
        final HttpServletRequest req = state.getRequest();

        //Login:
        if (m_saveCancelSection.getSaveButton().isSelected(state)) {

            // Redirect to workspace or return URL, if specified.
            final String path = UI.getUserRedirectURL(req);

            final URL url = com.arsdigita.web.URL.there(req, path);

            throw new ReturnSignal(req, url);
        }
        //Cancel:
        if (m_saveCancelSection.getCancelButton().isSelected(state)
            || ssoSuccessful) {

            //redirect the user to the place they came from.
            try {
                String refererURI = new URI(req.getHeader("referer")).getPath();

                if (refererURI.equals("/ccm/register/")) {

                    final String path = UI.getRootPageURL(req);
                    throw new RedirectSignal(com.arsdigita.web.URL.there(req,
                                                                         path),
                                             true);
                }
                ssoSuccessful = false;
                throw new ReturnSignal(req, refererURI);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Logs in the user using the username, password, and "Remember this login?"
     * request stored in the given form event. Subclasses can override this
     * method or just one of the specific case handlers (onLoginSuccess,
     * onBadPassword, onAccountNotFound, onLoginException).
     *
     * @param event
     *
     * @throws FormProcessException if there is an unexpected login error
     *
     */
    protected void loginUser(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();

        try {
            UserContext ctx = Web.getUserContext();
            String username = null;
            if (Kernel.getConfig().emailIsPrimaryIdentifier()) {
                username = ((InternetAddress) m_loginName.getValue(state)).
                    getAddress();
            } else {
                username = (String) m_loginName.getValue(state);
            }

            char[] password = ((String) m_password.getValue(state)).trim().
                toCharArray();
            boolean forever = getPersistentLoginValue(event.getPageState(),
                                                      false);
            // attempt to log in user
            ctx.login(username, password, forever);
            onLoginSuccess(event);
        } catch (FailedLoginException e) {
            onLoginFail(event, e);
        } catch (AccountNotFoundException e) {
            if (m_autoRegistrationOn) {
                onAccountNotFound(event, e);
            } else {
                onLoginFail(event, e);
            }
        } catch (LoginException e) {
            onLoginException(event, e);
        }
    }

    /**
     * Executed when login succeeds. Default implementation does nothing.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    protected void onLoginSuccess(FormSectionEvent event)
        throws FormProcessException {
        // do nothing
    }

    /**
     *
     * @param event
     * @param e
     *
     * @throws FormProcessException
     */
    protected void onBadPassword(FormSectionEvent event,
                                 FailedLoginException e)
        throws FormProcessException {
        onLoginFail(event, e);
    }

    /**
     * Executed when login fails with a bad password or when autoLoginOn is set
     * to false and the user doesn't exist. Default implementation marks
     * password parameter with an error message.
     *
     * @param event
     * @param e
     *
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    protected void onLoginFail(FormSectionEvent event,
                               LoginException e)
        throws FormProcessException {
        s_log.debug("Login fail");
        event.getFormData().addError((String) ERROR_LOGIN_FAIL.localize(event.
            getPageState().getRequest()));
    }

    /**
     * Executed when login fails for an unrecognized user. Default
     * implementation sets a flag so that the client is redirected to the new
     * user page (see the process() method code).
     *
     */
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
     * Executed when login fails for an unrecognized problem. Default
     * implementation logs the error and throws FormProcessException.
     *
     */
    protected void onLoginException(FormSectionEvent event,
                                    LoginException e)
        throws FormProcessException {
        // unexpected error happened during login
        s_log.error("Login failed", e);
        throw new FormProcessException(e);
    }

    /**
     * Determines whether a persistent cookie is requested in the given form.
     *
     * @return true if the specified formdata has a field named
     *         FORM_PERSISTENT_LOGIN_P whose value is equal to "1". If there is
     *         no such field in the form data, returns the specified default
     *         value.
     *
     */
    protected boolean getPersistentLoginValue(PageState state,
                                              boolean defaultValue) {
        // Problem:
        // getValue(state) returns an Object of type StringArray, if the
        // Checkbox is marked.
        // It returns an object of type String if it is not marked / left empty.
        // Additionally, in some circumstances it may return null
        // ODD!!

        Object persistentLoginValue = m_isPersistent.getValue(state);

        String value;

        if (persistentLoginValue == null) {
            return defaultValue;
        }

        if (persistentLoginValue instanceof String[]) {
            value = ((String[]) persistentLoginValue)[0];
        } else if (persistentLoginValue instanceof String) {
            value = (String) persistentLoginValue;
        } else {
            value = "0";
        }

        return "1".equals(value);
    }

    /**
     *
     * @param state
     */
    protected void redirectToNewUserPage(PageState state) {

        String url = LoginServlet.getNewUserPageURL();

        ParameterMap map = new ParameterMap();
        map.setParameter(LoginHelper.RETURN_URL_PARAM_NAME,
                         m_returnURL.getValue(state));
        map.setParameter(FORM_PERSISTENT_LOGIN_P,
                         m_isPersistent.getValue(state));
        map.setParameter(FORM_EMAIL,
                         m_loginName.getValue(state));

        final URL dest = com.arsdigita.web.URL.there(state.getRequest(),
                                                     url,
                                                     map);

        throw new RedirectSignal(dest, true);

    }

}

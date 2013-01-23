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
package com.arsdigita.kernel.security;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
// import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
// import com.arsdigita.kernel.security.HTTPRequestCallback;
// import com.arsdigita.kernel.security.HTTPResponseCallback;
// import com.arsdigita.kernel.security.LifetimeCallback;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import java.io.IOException;
import java.net.URLEncoder;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Provides methods for logging in and logging out the current user and
 * accessing the user ID.
 *
 * @author Sameer Ajmani
 * @version $Id: UserContext.java 1498 2007-03-19 16:22:15Z apevec $
 */
public class UserContext {

    private static final Logger s_log = Logger.getLogger(UserContext.class);

    /**
     * Name of the per-request login context.
     */
    public static final String REQUEST_LOGIN_CONTEXT = "Request";

    /**
     * Name of the register login context.
     */
    public static final String REGISTER_LOGIN_CONTEXT = "Register";

    /**
     * Name of the register SSO login context.
     */
    public static final String REGISTER_SSO_LOGIN_CONTEXT = "RegisterSSO";

    // fields
    private HttpServletRequest m_req;
    private HttpServletResponse m_res;
    private User m_user;
    private BigDecimal m_userID;
    private boolean m_recovering;
    private Set m_params;
    private SessionContext m_session;

    /**
     * Clears the values cached in this <code>UserContext</code>.
     */
    private void clearValues() {
        m_user = null;
        m_userID = null;
        m_recovering = false;
        m_params = new HashSet();
    }

    /**
     * Loads the values for this <code>UserContext</code> from the given
     * Subject.
     */
    private void loadValues(Subject subject)
        throws LoginException {
        // read the user ID (m_user loaded in getUser)
        m_userID = getUserID(subject);
        s_log.debug("userID == "+m_userID);
        // check whether recovering from forgotten password
        m_recovering = RecoveryLoginModule
            .isRecovering(subject);
        s_log.debug("recovering == "+m_recovering);
        // read set of URL params provided by login modules
        m_params = subject.getPublicCredentials(ParameterData.class);
        s_log.debug("params.size == "+m_params.size());
    }

    /**
     * Creates a user context from an HTTP request.  Attempts to log in the
     * user automatically to load the user ID.  Code should access this
     * class using
     * <code>KernelHelper.getKernelRequestContext(req).getUserContext()</code>.
     *
     * @throws RedirectException if the user should be redirected to the
     * login page.
     */
    public UserContext(HttpServletRequest req,
                       HttpServletResponse res)
        throws RedirectException {
        m_req = req;
        m_res = res;
        m_session = new SessionContext(req, res);
        login();
    }

    /**
     * Logs in as the User with the given username.
     *
     * @param username the username of the target User
     *
     * @throws AccountNotFoundException if the target user does not exist.
     *
     * @throws LoginException if login(User) fails.
     */
    public void login(String username)
        throws LoginException {
        s_log.debug("START login(username)");
        try {
            login(UserAuthentication.retrieveForLoginName(username).getUser());
            s_log.debug("SUCCESS login(username)");
        } catch (DataObjectNotFoundException e) {
            throw new AccountNotFoundException
                ("user "+username+" does not exist", e);
        }
    }

    /**
     * Logs in as the User with the given user ID.
     *
     * @param userID the ID of the target User
     *
     * @throws AccountNotFoundException if the target user does not exist.
     *
     * @throws LoginException if login(User) fails.
     */
    public void login(BigDecimal userID)
        throws LoginException {
        s_log.debug("START login(userID)");
        try {
            login(User.retrieve(userID));
            s_log.debug("SUCCESS login(userID)");
        } catch (DataObjectNotFoundException e) {
            throw new AccountNotFoundException
                ("user "+userID+" does not exist", e);
        }
    }

    /**
     * Logs in as the given User.
     *
     * @param target the User to become
     *
     * @throws FailedLoginException if the current user is not logged in,
     * doesn't exist, or doesn't have admin privileges on the target user.
     *
     * @throws LoginException if an error occurs.
     */
    public void login(User target)
        throws LoginException {
        s_log.debug("START login(User)");
        if (!isLoggedIn()) {
            s_log.debug("FAILURE login(User): not logged in");
            throw new FailedLoginException("not logged in");
        }

        User user = getUser();

        if (user == null) {
            s_log.debug("FAILURE login(User): current user doesn't exist");
            throw new FailedLoginException("current user doesn't exist");
        }

        // Now we check whether the target user is banned.  If they are then
        // it is not possible to become this user.  This situation will be 
        // generally avoided by hiding the 'become user' link for banned users
        if(Kernel.getSecurityConfig().isUserBanOn() && target.isBanned()) {
            throw new LoginException("This user is currently banned");
        }

        PermissionDescriptor superuser = new PermissionDescriptor
            (PrivilegeDescriptor.ADMIN, target, user);

        if (!PermissionService.checkPermission(superuser)) {
            s_log.debug("FAILURE login(User): insufficient privileges");
            SecurityLogger.warn("user " + user.getID()
                                + " failed to log in as user "
                                + target.getID()
                                + " due to insufficient privileges");
            throw new FailedLoginException
                ("insufficient privileges to become target user");
        }

        // set the target user ID in the Subject and login
        Subject subject = new Subject();
        subject.getPrincipals().add(new PartyPrincipal(target.getID()));
        CallbackHandler handler = new RequestCallbackHandler();
        LoginContext context = new LoginContext
            (REQUEST_LOGIN_CONTEXT, subject, handler);
        clearValues();
        context.login();
        loadValues(context.getSubject());
        m_session.loadSessionID(handler);
        s_log.debug("SUCCESS login(User)");
    }

    /**
     * Returns the SessionContext associated with this UserContext.
     *
     * @return the current SessionContext.
     */
    public SessionContext getSessionContext() {
        return m_session;
    }

    /**
     * Determines whether the user is logged in.
     *
     * @return <code>true</code> if the user is logged in,
     * <code>false</code> otherwise.
     */
    public boolean isLoggedIn() {
        return (m_userID != null);
    }

    /**
     * Determines whether the user is recovering a forgotten password.
     *
     * @return <code>true</code> if the user is recovering,
     * <code>false</code> otherwise.
     */
    public boolean isRecovering() {
        return m_recovering;
    }

    /**
     * Returns URL parameters that authenticate this user.  Package-private.
     *
     * @return an unmodifiable set of bebop ParameterData.
     */
    Set getParams() {
        return Collections.unmodifiableSet(m_params);
    }

    /**
     * Returns the set of all possible URL params used by UserContext.
     * Package-private.
     *
     * @return an unmodifiable set of bebop ParameterModels.
     */
    static Set getModels() {
        try {
            // LoginModules add ParameterModels to Subject in initialize()
            LoginContext context =
                new LoginContext(REQUEST_LOGIN_CONTEXT);
            return Collections.unmodifiableSet
                (context.getSubject().getPublicCredentials
                 (ParameterModel.class));
        } catch (LoginException e) {
            throw new UncheckedWrapperException
                ("Could not load context", e);
        }
    }

    /**
     * Returns the current user's ID.
     *
     * @return the ID of the logged in user.
     *
     * @throws IllegalStateException if the user is not logged in.
     */
    public BigDecimal getUserID() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("user is not logged in");
        }
        return m_userID;
    }

    /**
     * Returns a User object for the current user.  Subsequent calls to this
     * method return references to the same User object until the
     * <code>logout</code> method is called.
     *
     * @return the User object for the logged in user or null if the
     * user is not found.
     *
     * @throws IllegalStateException if the user is not logged in.
     */
    public User getUser() {
        if (m_user == null) {
            try {
                m_user = User.retrieve(getUserID());
            } catch (DataObjectNotFoundException e) {
                //leave m_user as null since the user is not found
                m_user = null;
            }
            if (m_user != null) {
                m_user.disconnect();
            }
                
        } 

        return m_user;
    }

    /**
     * Logs in the user from data in the current request.  Checks the
     * session ID using <code>SessionContext</code>.
     *
     * @throws RedirectException if the user should be redirected to the
     * login page.
     */
    private void login()
        throws RedirectException {
        s_log.debug("START login()");
        CallbackHandler handler = new RequestCallbackHandler();
        boolean success = false;
        try {
            // log in user from request parameters
            LoginContext context = new LoginContext(REQUEST_LOGIN_CONTEXT, handler);
            clearValues();
            context.login();
            loadValues(context.getSubject());

            // Check that the user making this request is not banned.  If they
            // are we logout the context and throw an exception.
            if(Kernel.getSecurityConfig().isUserBanOn()
               && User.retrieve(m_userID).isBanned()) {
                context.logout();
                throw new LoginException("This user is banned");
            }
            s_log.debug("SUCCESS login()");
            success = true;
        } catch (CredentialNotFoundException e) {
            // user does not have a cookie (common case)
            s_log.debug("FAIL login(): user does not have credential", e);
        } catch (CredentialParsingException e) {
            // login credential was tampered with
            s_log.debug("FAIL login()", e);
            SecurityLogger.warn("login credential tampered with");
        } catch (LoginException e) {
            // could not log in user, but continue anyway
            s_log.debug("FAIL login()", e);
        } finally {
            if (!success) {
                // common code for all exception cases
                if (Util.getSecurityHelper().requiresLogin(m_req)) {
                    s_log.debug("This request requires logging in; " +
                                "requesting redirect to login UI");
                    redirectToLoginPage(m_req);
                } else {
                    s_log.debug("This request does not require logging in");
                }
            }
        }
        try {
            // load session ID from request parameters
            m_session.loadSessionID(handler);
        } catch (LoginException e) {
            s_log.debug("FAIL login()", e);
            redirectToLoginPage(m_req);
        }
    }

    /**
     * Implements callbacks for automatic (per-request) login.
     */
    private class RequestCallbackHandler
        implements CallbackHandler {
        String m_username = null;
        char[] m_password = null;

        public RequestCallbackHandler() {
            try {
                // HTTP Basic Authentication
                String auth = m_req.getHeader("Authorization");
                if ((auth == null)
                    || !auth.toUpperCase().startsWith("BASIC")) {
                    return;
                }
                String encoded = auth.substring(6).trim(); // remove "Basic "
                byte[] decoded = new Base64().decode(
                                 encoded.getBytes(Crypto.CHARACTER_ENCODING));
                String userpass = new String(decoded, Crypto.CHARACTER_ENCODING);
                int colon = userpass.indexOf(':');
                if (colon < 0) {
                    return;
                }
                m_username = userpass.substring(0, colon);
                m_password = userpass.substring
                    (colon+1, userpass.length()).toCharArray();
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
        }

        public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

            for (int i = 0; i < callbacks.length; i++) {
                Callback cb = callbacks[i];
                if (cb instanceof HTTPRequestCallback) {
                    ((HTTPRequestCallback)cb).setRequest
                        (UserContext.this.m_req);

                } else if (cb instanceof HTTPResponseCallback) {
                    ((HTTPResponseCallback)cb).setResponse
                        (UserContext.this.m_res);

                } else if (cb instanceof LifetimeCallback) {
                    ((LifetimeCallback)cb).setForever(false);

                } else if (cb instanceof NameCallback) {
                    ((NameCallback)cb).setName(m_username);

                } else if (cb instanceof PasswordCallback) {
                    ((PasswordCallback)cb).setPassword(m_password);

                } else {
                    UserContext.reportUnsupportedCallback(cb);
                }
            }
        }
    }

    /**
     * Creates a URL to send the user to the login page and then return to
     * the current page.
     *
     * @throws com.arsdigita.web.LoginSignal
     */
    public static void redirectToLoginPage(HttpServletRequest req) {
        throw new LoginSignal(req);
    }

    /**
     * Name of the request parameter that stores the URL to return to after
     * redirecting to the login page.
     *
     * @deprecated Use com.arsdigita.ui.login.LoginHelper.RETURN_URL_PARAM_NAME
     *             instead
     */
    public final static String RETURN_URL_PARAM_NAME = "return_url";

    /**
     * Encodes the given request into a return URL parameter.  Returns
     * <code>URLencode(returnURL)</code> where returnURL is
     * <code>returnURI?key=URLencode(val)&...</code>.  The original
     * parameter values are doubly-encoded so that they are decoded
     * appropriately.
     *
     *
     * @param req the request to encode
     *
     * @return the URL-encoded parameter
     * @deprecated This should be moved to a more appropriate class.
     */
    public static String encodeReturnURL(HttpServletRequest req) {
        StringBuilder returnURL = new StringBuilder(100);
        returnURL.append(Web.getContext().getRequestURL().getRequestURI());
        returnURL.append('?');

        // convert posted parameters to URL parameters
        Enumeration params = req.getParameterNames();
        boolean first = true;
        while (params.hasMoreElements()) {
            String key = (String) params.nextElement();
            String[] vals = req.getParameterValues(key);
            for (int i = 0; i < vals.length; i++) {
                if (first) {
                    first = false;
                } else {
                    returnURL.append('&');
                }
                returnURL.append(key);
                returnURL.append('=');
                returnURL.append(URLEncoder.encode(vals[i]));
            }
        }

        return URLEncoder.encode(returnURL.toString());
    }

    /**
     * Logs in the user.  Checks the session ID using
     * <code>SessionContext</code>.
     *
     * @param username the user's username
     * @param password the user's password
     * @param forever true if the user requests permanent login
     *
     * @throws LoginException if login fails.
     */
    public void login(String username,
                      char[] password,
                      boolean forever)
                throws LoginException {
        s_log.debug("START login(username, password, forever)");
        try {
            CallbackHandler handler = new LoginCallbackHandler
                (username, password, forever);
            LoginContext context = new LoginContext
                (REGISTER_LOGIN_CONTEXT, handler);
            clearValues();
            context.login();

            // We now check if the user is banned and, if so, we don't allow
            // the user to login.
            if(Kernel.getSecurityConfig().isUserBanOn() 
               && UserAuthentication.retrieveForLoginName(username).getUser()
                                                                   .isBanned()) {
                throw new LoginException("This user is currently banned");
            }

            loadValues(context.getSubject());
            m_session.loadSessionID(handler);
            s_log.debug("SUCCESS login(username, password, forever)");
        } catch (LoginException e) {
            SecurityLogger.info("register login failed: ", e);
            throw e;
        }
    }

    /**
     * Logs in the user using alternative "RegisterSSO" login context.
     * It is expected that SSO token is present in the request
     * @see SimpleSSOLoginModule 
     * @throws LoginException
     */
    public void loginSSO() throws LoginException {
        s_log.debug("START loginSSO()");
        try {
            // Request cb is enough, since with SSO we should have all needed
            // info in the request and cookie must be temporary

            CallbackHandler handler = new RequestCallbackHandler();
            LoginContext context = new LoginContext(REGISTER_SSO_LOGIN_CONTEXT,
                    handler);
            clearValues();
            context.login();

            loadValues(context.getSubject());
            m_session.loadSessionID(handler);
            s_log.debug("SUCCESS loginSSO()");
        } catch (LoginException e) {
            SecurityLogger.info("register SSO login failed: ", e);
            throw e;
        }
    }

    /**
     * Implements callbacks for interactive (register-based) login.
     */
    private class LoginCallbackHandler
        implements CallbackHandler {
        private String m_username;
        private char[] m_password;
        private boolean m_forever;

        public LoginCallbackHandler(String username,
                                    char[] password,
                                    boolean forever) {
            m_username = username;
            m_password = password;
            m_forever  = forever;
        }

        public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

            for (int i = 0; i < callbacks.length; i++) {
                Callback cb = callbacks[i];
                if (cb instanceof HTTPRequestCallback) {
                    ((HTTPRequestCallback)cb).setRequest
                        (UserContext.this.m_req);

                } else if (cb instanceof HTTPResponseCallback) {
                    ((HTTPResponseCallback)cb).setResponse
                        (UserContext.this.m_res);

                } else if (cb instanceof LifetimeCallback) {
                    ((LifetimeCallback)cb).setForever(m_forever);

                } else if (cb instanceof NameCallback) {
                    ((NameCallback)cb).setName(m_username);

                } else if (cb instanceof PasswordCallback) {
                    ((PasswordCallback)cb).setPassword(m_password);

                } else {
                    UserContext.reportUnsupportedCallback(cb);
                }
            }
        }
    }

    /**
     * Reads the user ID from a Subject.
     *
     * @return the ID of the first PartyPrincipal in the subject.
     *
     * @throws LoginException if the user ID is not available.
     */
    private BigDecimal getUserID(Subject subject) throws LoginException {
        Iterator principals = subject.getPrincipals
            (PartyPrincipal.class).iterator();

        if (!principals.hasNext()) {
            throw new FailedLoginException
                ("no principal available after login");
        }

        return ((PartyPrincipal) principals.next()).getID();
    }

    /**
     * Logs out the user.  Clears the cached User object.  Loads a new
     * session ID using <code>SessionContext</code>.
     *
     * @throws LoginException if logout fails.
     */
    public void logout()
        throws LoginException {
        s_log.debug("START logout");
        CallbackHandler handler = new RequestCallbackHandler();
        LoginContext context = new LoginContext
            (REQUEST_LOGIN_CONTEXT, handler);
        context.logout();
        clearValues();
        m_session.loadSessionID(handler);
        s_log.debug("SUCCESS logout");
    }

    /**
     * Reports an unsupported callback to the debug log and throws an
     * exception.  Package-private.
     *
     * @throws UnsupportedCallbackException with appropriate error message
     */
    static void reportUnsupportedCallback(Callback cb)
        throws UnsupportedCallbackException {
        s_log.error
            ("Unsupported callback: "
             +(cb == null ? null : cb.getClass().getName()));
        throw new UnsupportedCallbackException(cb);
    }
}

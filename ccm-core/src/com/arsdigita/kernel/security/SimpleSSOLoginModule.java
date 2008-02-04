package com.arsdigita.kernel.security;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.UserAuthentication;

/**
 * SimpleSSOLoginModule is fairly trivial LoginModule which obtains the
 * currently logged-in user via HttpServletRequest.getRemoteUser() method.
 * 
 * NOTE: This Simple implementation assumes that webapp can <b>only</b> be
 * accessed through the frontend webserver which performs the authentication.
 *
 * @author Sebastian Skracic
 * @author Alan Pevec
 */
public class SimpleSSOLoginModule implements LoginModule {
    private static org.apache.log4j.Category s_log =
        org.apache.log4j.Category.getInstance ( SimpleSSOLoginModule.class );

    private HttpServletRequest m_request = null;
    private HttpServletResponse m_response = null;
    private CallbackHandler m_handler = null;
    private Subject m_subject = null;

    private BigDecimal m_userID = null;


    /**
     * Called by the LoginContext to initialise the module. Stores handles
     * to the CallbackHandler (used to get the HttpServletRequest), and the
     * shared Map (used to pass the distinguished name to the authentication
     * process).
     */
    public void initialize ( Subject subject,
                             CallbackHandler handler,
                             Map shared,
                             Map options ) {
        m_handler = handler;
        m_subject = subject;
    }


    /**
     * Deduct the logged-in user name from the request.
     */
    protected String getUserName(HttpServletRequest req) throws LoginException {
        String uid = req.getRemoteUser();
        if (uid == null) {
            throw new FailedLoginException("User credentials not present in HTTP request");
        }
        return uid;
    }
    
    /**
     * Retrieve userID for the given SSO login name.
     * @param ssoLogin SSO login name
     * @return userID
     * @throws LoginException when SSO login cannot be found
     */
    protected BigDecimal getUserID(String ssoLogin) throws LoginException {
        try {
            UserAuthentication auth = UserAuthentication.retrieveForSSOlogin(ssoLogin);
            return auth.getUser().getID();
        } catch (DataObjectNotFoundException donfe) {
            throw new FailedLoginException("SSO login "+ssoLogin+" not found");
        }
    }

    /**
     * Try to login in using SSO token present in HTTP requrest.
     * 
     * @return Success of login.
     */
    public boolean login() throws LoginException {
        HttpServletRequest req = getRequest();
        HttpSession session = req.getSession();
        m_userID = getUserID(getUserName(req));
        if (s_log.isDebugEnabled()) {
            s_log.debug("SSO: login successful");
        }
        return true;
    }

    /**
     * Adds the user ID to the Subject in a PartyPrincipal.
     *
     * @return <code>true</code>.
     **/
    public boolean commit() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("START commit");
        }
        if (m_userID != null) {
            m_subject.getPrincipals().add(new PartyPrincipal(m_userID));
            if (s_log.isDebugEnabled()) {
                s_log.debug("SUCCESS added new principal");
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("END commit");
        }
        return true;
    }

    /**
     * Called by the LoginContext if login was unsuccessful.
     * The implementation here simply generates a log message and does nothing else.
     */
    public boolean abort() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("SSO: aborting");
        }
        return true;
    }

    /**
     * Trivial implementation which just invalidates current HTTP session.
     */
    public boolean logout() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("SSO: logout");
        }
        getRequest().getSession().invalidate();
        return true;
    }


    /**
     * Get a handle to the current HttpServletRequest.
     *
     * @see com.arsdigita.kernel.security.CredentialLoginModule
     */
    protected HttpServletRequest getRequest() throws LoginException {
        try {
            if (m_request == null) {
                HTTPRequestCallback cb = new HTTPRequestCallback();
                m_handler.handle( new Callback[] { cb } );
                m_request = cb.getRequest();
            }
            return m_request;
        } catch (IOException e) {
            throw new LoginException("Could not get HTTP request: " + e);
        } catch (UnsupportedCallbackException e) {
            throw new LoginException("Could not get HTTP request: " + e);
        }
    }

    /**
     * Get a handle to the current HttpServletResponse.
     *
     * @see com.arsdigita.kernel.security.CredentialLoginModule
     */
    protected HttpServletResponse getResponse() throws LoginException {
        try {
            if (m_response == null) {
                HTTPResponseCallback cb = new HTTPResponseCallback();
                m_handler.handle( new Callback[] { cb } );
                m_response = cb.getResponse();
            }
            return m_response;
        } catch (IOException e) {
            throw new LoginException("Could not get HTTP response: " + e);
        } catch (UnsupportedCallbackException e) {
            throw new LoginException("Could not get HTTP response: " + e);
        }
    }

}

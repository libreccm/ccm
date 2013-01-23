/* -*- mode: java; c-basic-offset: 4; indent-tabs-mode: nil -*-
 *
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.auth.http;

import com.arsdigita.db.Sequences;
import com.arsdigita.kernel.security.*;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.crypto.Cipher;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

/**
 * <p>
 * Attempt to authenticate a user against an IIS server for seamless login
 * from an IE session.
 * </p>
 *
 * <p>
 * This login module is placed after the normal <code>CookieLoginModule</code>
 * like so:
 * </p>
 *
 * <pre>
 * init com.arsdigita.kernel.security.Initializer {
 *   ...
 *   loginConfig = {
 *     "Request", {
 *          {"com.arsdigita.kernel.security.AdminLoginModule",
 *           "sufficient"},
 *          {"com.arsdigita.kernel.security.RecoveryLoginModule",
 *           "sufficient"},
 *          {"com.arsdigita.kernel.security.CookieLoginModule",
 *           "sufficient"},
 *          <b>{"com.arsdigita.auth.ntlm.HTTPLoginModule",
 *           "requisite"},</b>
 *          {"com.arsdigita.kernel.security.CookieLoginModule",
 *           "optional"}
 *      },
 *   ...
 * </pre>
 *
 * <p>
 * You also need an IIS server, running <code>HTTPAuthServlet</code>
 * (q.v.).
 * </p>
 *
 * <p>
 * When a request is received which is not authenticated, the
 * {@link #login login} method in this class will be called as part
 * of the JAAS chain of handlers. The login method works out if the
 * user is using IE by sniffing their <code>User-Agent</code>. If
 * so, then it generates a one-off random string called a <q>nonce</q>
 * and saves this in the database. The nonce is used to prevent
 * replay attacks, but is otherwise not needed. It then redirects
 * the user's browser to the IIS server, with the following arguments:
 * </p>
 *
 * <pre>
 * http://iis-server/foo ? nonce=<i>nonce</i> &amp; returnURL=<i>original URL</i>
 * </pre>
 *
 * <p>
 * The IIS server runs <code>HTTPAuthServlet</code>. This
 * basically calls <code>request.getRemoteUser()</code> which does
 * some proprietary M$ voodoo to fetch the username from the
 * browser. It takes the username and nonce and signs them with
 * its private key (see below) and redirects back to us at the
 * following URL:
 * </p>
 *
 * <pre>
 * original URL & auth=<i>magic number, username, nonce and signature</i>
 * </pre>
 *
 * <p>
 * Because the request <em>still</em> doesn't contain a cookie, we
 * will get this request during the normal course of processing, but
 * this time the <code>auth</code> parameter will be set. So we
 * process the parameter, check the signature, and map the IIS/HTTP
 * username to a CCM <code>User</code>.
 * </p>
 *
 * <p>
 * To see how key managament works and how to see it up, go to
 * the javadoc for <code>HTTPAuthServlet</code>.
 * </p>
 *
 * @author Matt Booth, documentation by Richard W.M. Jones
 *
 * @see com.arsdigita.auth.ntlm.HTTPAuthServlet
 */
public class HTTPLoginModule extends MappingLoginModule {
    private static Logger s_log = Logger.getLogger
        ( HTTPLoginModule.class );

    private static PublicKey s_publicKey = null;
    static {
        s_log.debug("Static initalizer starting...");
        if (HTTPAuth.getConfig().isActive()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Loading public key");
            }
            s_publicKey = HTTPAuth.getConfig().getPublicKey();
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("HTTP auth is not active");
            }
        }
        s_log.debug("Static initalizer finished.");
    }

    private static BASE64Decoder s_base64Decoder = new BASE64Decoder();
    
    // The time in seconds until a nonce expires
    private static final String TTL = "60";

    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;
    private BigDecimal m_userID = null;
    private Boolean m_secure = null;

    /* This is the decryption cipher. */
    private Cipher m_decrypt;

    public HTTPLoginModule() {
        m_decrypt = getDecryptionCipher ();
    }

    public void initialize( Subject subject,
                            CallbackHandler handler,
                            Map shared,
                            Map options ) {
        m_subject = subject;
        m_handler = handler;
        m_shared = shared;
        m_options = options;

        super.initialize( subject, handler, shared, options );
    }

    public boolean login() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug( "HTTP Login Start" );
        }

        if (m_decrypt == null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("No public key available, falling back to default");
            }
            return false;
        }

        // Get the request and response objects
        HttpServletRequest req;
        HttpServletResponse res;
        try {
            HTTPRequestCallback reqCB = new HTTPRequestCallback();
            HTTPResponseCallback resCB = new HTTPResponseCallback();


            m_handler.handle( new Callback[] { reqCB, resCB } );
            req = reqCB.getRequest();
            res = resCB.getResponse();
        } catch( Exception e ) {
            throw new UncheckedWrapperException( e );
        }

        // Check address is in range requested.
        Inet4AddressRange range = HTTPAuth.getConfig().getClientIPRange ();
        if (range != null) {
            InetAddress address;

            String ipaddress = req.getHeader("X-Forwarded-For");
            if (ipaddress == null) {
                ipaddress = req.getRemoteAddr();
                if (s_log.isDebugEnabled()) {
                    s_log.debug ("Remote Address = " + ipaddress);
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug ("Proxy forwarded chain is " + ipaddress);
                }
                
                int index = ipaddress.indexOf(',');
                if (index != -1) {
                    ipaddress = ipaddress.substring(0, index);
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug ("Proxy forwarded client is " + ipaddress);
                }
            }

            try {
                address = InetAddress.getByName (ipaddress);
            }
            catch (UnknownHostException ex) {
                s_log.warn("Unknown host " + ipaddress, ex);
                // Abort NTLM auth if can't lookup host
                // since it could be temporary DNS failure
                // so falling back on normal auth is nicer
                // to the user.
                return false;
            }
            catch (SecurityException ex) {
                throw new UncheckedWrapperException (ex);
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug ("Address = " + address);
                s_log.debug ("Range = " + range);
            }

            if (!range.inRange (address)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug ("Address not in range");
                }
                return false;
            }
        }

        // This authentication method only works with IE. If we haven't got IE
        // just fail immediately
        String userAgent = req.getHeader( "user-agent" );
        boolean isIE = userAgent != null && userAgent.toLowerCase().indexOf( "msie" ) != -1;

        if( !isIE ) {
            if (s_log.isDebugEnabled()) {
                s_log.debug( "User not using IE, falling back to default login" );
            }
            return false;
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug( "Attempting HTTP authentication" );
            }
        }

        // Check if we've got an Auth String
        String auth = req.getParameter (HTTPAuthServlet.AUTH);
        if( auth == null ) {
            // Fetch a new nonce and add it to the db
            String nonce;
            try {
                nonce = Sequences.getNextValue().toString();
                DataOperation op =
                    SessionManager.getSession().retrieveDataOperation
                    ( "com.arsdigita.auth.ntlm.AddNonce" );
                op.setParameter( "nonce", nonce );
                op.setParameter( "expires", new Date(new Date().getTime() + 
                                                     (HTTPAuth.getConfig().getNonceTTL() * 1000l)));
                op.setParameter( "status", Boolean.FALSE);
                op.execute();
                op.close();

                if (s_log.isDebugEnabled()) {
                    s_log.debug( "Added nonce: " + nonce );
                }

            } catch( Exception e ) {
                throw new UncheckedWrapperException( e );
            }

            String returnURL = "http://" + Web.getConfig().getHost().getName()
                                         + req.getRequestURI();
            if( req.getQueryString () != null ) {
                returnURL = returnURL + "?" + req.getQueryString ();
            }
            returnURL = URLEncoder.encode( returnURL );
            
            if (s_log.isDebugEnabled()) {
                s_log.debug( "Return URL: " + returnURL );
            }

            String redirectURL = "http://" + HTTPAuth.getConfig().getServerName() + 
                ":" + HTTPAuth.getConfig().getServerPort() + "/auth/" +
                "?nonce=" + nonce + "&returnURL=" + returnURL;
            clearCookie(req, res);

            throw new RedirectSignal (redirectURL, true);
        }

        // Decrypt the authorisation string
        try {
            byte[] decAuthBytes = s_base64Decoder.decodeBuffer( auth );
            auth = new String( m_decrypt.doFinal( decAuthBytes ) );
        } catch( Exception e ) {
            s_log.warn( "Error checking nonce value: " + e );
            e.printStackTrace();
            throw new LoginException
                ( "Invalid signature from authentication server" );
        }

        if( !auth.startsWith( HTTPAuthServlet.MAGIC ) ) {
            s_log.warn ("Invalid signature from authentication server " +
                        "- no MAGIC (auth was: " + auth + ")");
            throw new LoginException
                ( "Invalid signature from authentication server" );
        }
        auth = auth.substring( HTTPAuthServlet.MAGIC.length() );
        if (s_log.isDebugEnabled()) {
            s_log.debug( "Decrypted Auth: " + auth );
        }
        
        // Get the nonce and username from the auth string
        int sep = auth.indexOf( "|" );
        String nonce = auth.substring( 0, sep );
        String user = auth.substring( sep + 1 );

        if (s_log.isDebugEnabled()) {
            s_log.debug( "Nonce: " + nonce );
            s_log.debug( "User: " + user );
        }

        // Check that the nonce we got is valid
        DataQuery check =
            SessionManager.getSession().retrieveQuery
            ( "com.arsdigita.auth.ntlm.CheckNonce" );
        check.setParameter( "now", new Date());
        check.setParameter( "nonce", nonce );
        check.setParameter( "status", Boolean.FALSE);
        boolean nonceInvalid = check.isEmpty();
        check.close();

        if( nonceInvalid ) {
            s_log.warn( "Received invalid nonce: " + nonce );
            throw new LoginException
                ( "Invalid challenge string from authentication server" );
        }

        // Expire the used nonce
        DataOperation expire =
            SessionManager.getSession().retrieveDataOperation
            ( "com.arsdigita.auth.ntlm.ExpireNonce" );
        expire.setParameter( "nonce", nonce );
        expire.setParameter( "status", Boolean.TRUE);
        expire.execute();
        expire.close();

        m_shared.put( PasswordLoginModule.NAME_KEY, user );

        // Pull query vars into a HashMap called vars
        Enumeration vars = req.getParameterNames ();

        // Redirect again to this URL without the camden nonce. This leaves a
        // prettier URL and allows the displayed page to be returned from a
        // cache if appropriate.

        StringBuffer requestURL = new StringBuffer( req.getRequestURI() );
        // got more than just the camden nonce in the query string
        if (vars.hasMoreElements()) {
            requestURL.append( '?' );
            boolean first = true;
            while (vars.hasMoreElements()) {
                String key = (String)vars.nextElement();
                String[] vals = req.getParameterValues(key);
                
                for (int i = 0 ; i < vals.length ; i++) {
                    // Don't include the nonce in the output
                    if( !HTTPAuthServlet.AUTH.equals(key) ) {
                        if (first)
                            first = false;
                        else
                            requestURL.append( '&' );
                        
                        requestURL.append(key);
                        requestURL.append( '=' );
                        requestURL.append(vals[i]);
                    }
                }
            }
        }
        m_shared.put( RedirectLoginModule.REDIRECT_URL,
                      requestURL.toString() );

        if (s_log.isDebugEnabled()) {
            s_log.debug( "HTTP Login End" );
        }
        return super.login();
    }


    /**
     * Sets the named cookie to the given value.
     **/
    private void clearCookie(HttpServletRequest req,
                             HttpServletResponse res) 
        throws LoginException {
            Cookie cookie = new Cookie(isSecure(req) ? 
                                       UserLoginModule.SECURE_CREDENTIAL_NAME : 
                                       UserLoginModule.NORMAL_CREDENTIAL_NAME, "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setSecure(isSecure(req));
            res.addCookie(cookie);
    }

    protected final boolean isSecure(HttpServletRequest req)
        throws LoginException {
        if (m_secure == null) {
            m_secure = new Boolean
                (Util.getSecurityHelper().isSecure(req));
        }
        return m_secure.booleanValue();
    }

    protected BigDecimal getUserID( String ident ) throws LoginException {
        UserLogin login = UserLogin.findByLogin(ident);
        if (login == null) {
            s_log.warn( "No entry for user " + ident );
            throw new LoginException( "No entry for user " + ident );
        }

        return login.getUser().getID();
    }

    private Cipher getDecryptionCipher() {
        // Create the decryption cipher.
        if (s_publicKey == null) {
            return null;
        }

        Cipher decrypt;

        try {
            decrypt = Cipher.getInstance (HTTPAuth.getConfig().getKeyCypher());
            decrypt.init( Cipher.DECRYPT_MODE, s_publicKey );
        }
        catch (GeneralSecurityException ex) {
            throw new UncheckedWrapperException (ex);
        }

        return decrypt;
    }

    public boolean commit() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug( "Commit" );
        }
        return super.commit();
    }

    public boolean abort() throws LoginException {
        if (s_log.isDebugEnabled()) {
            s_log.debug( "Abort" );
        }
        return super.commit();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.kernel.security;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.UserAuthentication;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SamlLoginModule implements LoginModule {

    private static final Logger LOGGER = Logger.getLogger(SamlLoginModule.class);

    private CallbackHandler callbackHandler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Subject subject;
    private BigDecimal userId;

    @Override
    public void initialize(final Subject subject,
                           final CallbackHandler callbackHandler,
                           final Map<String, ?> sharedState,
                           final Map<String, ?> options) {

        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }

    @Override
    public boolean login() throws LoginException {

        final HttpServletRequest httpRequest = getRequest();
        final HttpServletResponse httpResponse = getResponse();

        final Auth auth;
        try {
            auth = new Auth(OneLoginUtil.buildSettings(httpRequest),
                            httpRequest,
                            httpResponse);
        } catch (SettingsException ex) {
            LOGGER.error("SAML Login failed.", ex);
            throw new LoginException("SAML Login failed. Configuration error?");
        }

        final List<String> errors = auth.getErrors();
        if (!errors.isEmpty()) {
            LOGGER.error(String.format("SAML Login errors: %s",
                                       String.join(";\n", errors)));
            throw new LoginException(String.format("SAML Login errors: %s",
                                                   String.join(";\n", errors)));
        }

        if (!auth.isAuthenticated()) {
            throw new LoginException("Not authenticated.");
        }

        userId = getUserId(auth.getNameId());

        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
    protected HttpServletRequest getRequest() throws LoginException {

        try {
            if (request == null) {
                final HTTPRequestCallback callback = new HTTPRequestCallback();
                callbackHandler.handle(new Callback[]{callback});
                request = callback.getRequest();
            }
            return request;
        } catch (IOException | UnsupportedCallbackException ex) {
            throw new LoginException("Could not get HTTP request" + ex);
        }
    }

    protected HttpServletResponse getResponse() throws LoginException {

        try {
            if (response == null) {
                final HTTPResponseCallback callback = new HTTPResponseCallback();
                callbackHandler.handle(new Callback[]{callback});
                response = callback.getResponse();
            }
            return response;
        } catch (IOException | UnsupportedCallbackException ex) {
            throw new LoginException("Could not get HTTP response" + ex);
        }
    }

    protected BigDecimal getUserId(final String ssoLogin)
        throws LoginException {

        try {
            final UserAuthentication userAuth = UserAuthentication
                .retrieveForSSOlogin(ssoLogin);

            return userAuth.getUser().getID();
        } catch (DataObjectNotFoundException ex) {
            throw new FailedLoginException(
                String.format("SSO login %s not found", ssoLogin)
            );
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.kernel.security;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SamlLoginModule implements LoginModule {

    private CallbackHandler callbackHandler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Subject subject;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean commit() throws LoginException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean abort() throws LoginException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean logout() throws LoginException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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

}

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
 */

package com.arsdigita.auth.http;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.security.HTTPRequestCallback;
import com.arsdigita.kernel.security.HTTPResponseCallback;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class RedirectLoginModule implements LoginModule {
    public static final String REDIRECT_URL =
        "com.arsdigita.auth.http.RedirectURL";

    private final static Logger s_log =
        Logger.getLogger( RedirectLoginModule.class );

    private CallbackHandler m_handler;
    private Map m_shared;

    public void initialize( Subject subject, CallbackHandler handler,
                            Map shared, Map options ) {
        m_handler = handler;
        m_shared = shared;
    }

    public boolean commit() {
        String redirectURL = (String) m_shared.get( REDIRECT_URL );

        if ( null == redirectURL ) {
            s_log.debug( "Not redirecting" );
            return true;
        }

        s_log.debug( "Redirecting to " + redirectURL );
        redirectTo( redirectURL );

        return false;
    }

    public boolean abort() {
        return true;
    }

    public boolean login() {
        return true;
    }

    public boolean logout() {
        return true;
    }

    private void redirectTo( String redirectURL ) {
        try {
            HTTPRequestCallback reqCB = new HTTPRequestCallback();
            HTTPResponseCallback resCB = new HTTPResponseCallback();

            m_handler.handle( new Callback[] { reqCB, resCB } );
            HttpServletRequest req = reqCB.getRequest();
            HttpServletResponse res = resCB.getResponse();

            DispatcherHelper.sendRedirect( req, res, redirectURL );
        } catch( Exception ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }
}

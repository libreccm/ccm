/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.kernel.KernelRequestContext;
import com.arsdigita.kernel.security.SessionContext;
import com.arsdigita.kernel.security.UserContext;

/**
 * Class RequestEnvironment
 * 
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/08/16 $
 */
public class RequestEnvironment {
    private HttpServletDummyRequest m_req;
    private HttpServletDummyResponse m_res;

    public RequestEnvironment() {
        this(new HttpServletDummyRequest(),
                new HttpServletDummyResponse());
    }
    public RequestEnvironment(HttpServletDummyRequest req,
                              HttpServletDummyResponse res) {
        setupServletContext(req, res);
        setKernelContext();

    }

    public HttpServletDummyRequest getRequest() {
        return m_req;
    }

    public HttpServletDummyResponse getResponse() {
        return m_res;
    }

    private void setupServletContext(HttpServletDummyRequest req,
                              HttpServletDummyResponse res) {
        m_req = req;
        m_res = res;

        DispatcherHelper.setRequest(m_req);

    }

    /**
     * Sets the KernelContext in the request.
     */
    public void setKernelContext() {
        InitialRequestContext irc = new InitialRequestContext
            (m_req, new DummyServletContext());

        UserContext uc = null;
        try {
            uc = new UserContext(m_req, m_res);
        } catch (RedirectException re) {
            System.out.println(re.getMessage());
            re.printStackTrace();
        }
        SessionContext sc = uc.getSessionContext();

        KernelRequestContext krc =
            new KernelRequestContext(irc, sc, uc);
        DispatcherHelper.setRequestContext(m_req, krc);
    }

}

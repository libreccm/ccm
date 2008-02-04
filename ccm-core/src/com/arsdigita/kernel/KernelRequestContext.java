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
package com.arsdigita.kernel;

import com.arsdigita.kernel.security.SessionContext;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.RequestContext;
import org.apache.log4j.Logger;

public class KernelRequestContext extends InitialRequestContext {

    public static final String versionId = "$Id: KernelRequestContext.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_cat =
        Logger.getLogger(KernelRequestContext.class.getName());

    private SessionContext m_session;
    private UserContext m_user;

    public KernelRequestContext(RequestContext parent,
                                SessionContext session,
                                UserContext user) {
        super(parent);
        m_session = session;
        m_user = user;
    }

    /**
     * Copy constructor.
     **/
    protected KernelRequestContext(KernelRequestContext that) {
        this(that, that.m_session, that.m_user);
    }

    public SessionContext getSessionContext() {
        return m_session;
    }

    public UserContext getUserContext() {
        return m_user;
    }
}

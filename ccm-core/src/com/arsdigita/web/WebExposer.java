/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.kernel.security.UserContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: WebExposer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class WebExposer {
    public static final String versionId =
        "$Id: WebExposer.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (WebExposer.class);

    public static final void clearRequestLocals() {
        InternalRequestLocal.clearAll();
    }

    public static final void initializeRequestLocals
            (final HttpServletRequest sreq) {
        InternalRequestLocal.prepareAll(sreq);
    }

    public static final void init(HttpServletRequest sreq,
                                  ServletContext sc,
                                  UserContext uc) {
        Web.init(sreq, sc, uc);
    }

    public static WebContext getInitialContext() {
        return Web.s_initialContext;
    }
}

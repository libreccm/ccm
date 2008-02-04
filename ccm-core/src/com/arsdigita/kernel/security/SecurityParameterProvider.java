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

import com.arsdigita.util.ParameterProvider;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.KernelRequestContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Determines the URL parameters needed for user tracking.
 *
 * @author Sameer Ajmani
 **/
public class SecurityParameterProvider implements ParameterProvider {

    public static final String versionId = "$Id: SecurityParameterProvider.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(SecurityParameterProvider.class.getName());

    /**
     * Returns a copy of <code>UserContext.getModels()</code>.
     *
     * @return a copy of <code>UserContext.getModels()</code>
     **/
    public Set getModels() {
        // get parameter models
        Set models = new HashSet();
        models.addAll(UserContext.getModels());
        return models;
    }

    /**
     * Returns a copy of the given request's <code>UserContext</code>'s
     * <code>getParams()</code>.
     *
     * @return a copy of the given request's <code>UserContext</code>'s
     * <code>getParams()</code>.
     **/
    public Set getParams(HttpServletRequest req) {
        // get user and session info
        KernelRequestContext rctx =
            KernelHelper.getKernelRequestContext(req);

        // Request context can be null e.g. if called in a request listener
        if ( rctx == null ) {
            return Collections.EMPTY_SET;
        }

        UserContext uctx = rctx.getUserContext();
        // get set of parameters
        Set params = new HashSet();
        params.addAll(uctx.getParams());
        return params;
    }
}

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

import javax.security.auth.callback.Callback;
import javax.servlet.http.HttpServletResponse;

/**
 * Callback to retrieve the current <code>HttpServletResponse</code>.
 *
 * @author Sameer Ajmani
 * @version $Id: HTTPResponseCallback.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class HTTPResponseCallback implements Callback {

    private HttpServletResponse m_response;

    /**
     * Gets the <code>HttpServletResponse</code>.
     **/
    public HttpServletResponse getResponse() {
        return m_response;
    }

    /**
     * Sets the <code>HttpServletResponse</code>.
     **/
    public void setResponse(HttpServletResponse response) {
        m_response = response;
    }
}

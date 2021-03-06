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

package com.arsdigita.rssfeed.dispatcher;

import com.arsdigita.rssfeed.RSSChannel;
import com.arsdigita.dispatcher.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


public class SimpleRSSDispatcher extends RSSDispatcher {

    private RSSChannel m_channel;

    /**
     * Constructor.
     * @param channel
     */
    public SimpleRSSDispatcher(RSSChannel channel) {
        m_channel = channel;
    }

    /**
     * Implementation auf abstract method in parent class
     * @param request
     * @param response
     * @param actx
     * @return
     * @throws ServletException
     */
    public RSSChannel getChannel(HttpServletRequest request,
                                 HttpServletResponse response,
                                 RequestContext actx)
        throws ServletException {
        return m_channel;
    }
}

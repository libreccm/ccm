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

package com.arsdigita.london.subsite;


import javax.servlet.FilterChain;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.arsdigita.web.BaseFilter;
import com.arsdigita.web.URL;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.navigation.NavigationContext;

import org.apache.log4j.Logger;

public class SubsiteFilter extends BaseFilter {
    
    private static final Logger s_log = Logger.getLogger(SubsiteFilter.class);

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp,
                             FilterChain chain)
        throws IOException,
               ServletException {

        URL url = new URL(sreq);
        String hostname = url.getServerName();
        int port = url.getServerPort();
        if (port != 80) {
            hostname = hostname + ":" + port;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Looking for site matching " + hostname);
        }
        Site site = null;
        try {
            site = Site.findByHostname(hostname);
        } catch (DataObjectNotFoundException ex) {
            // Must be on the main site
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Got site " + site);
        }
        sreq.setAttribute(SubsiteContext.SITE_REQUEST_ATTRIBUTE, 
                          site);
        sreq.setAttribute(NavigationContext.TEMPLATE_CONTEXT,
                          site == null ? null : 
                          site.getTemplateContext());

        chain.doFilter(sreq, sresp);
    }
}

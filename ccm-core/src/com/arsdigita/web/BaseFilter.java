/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * A base class for filters that require a persistence
 * session to be present. This implements the doFilter
 * method to open up a default persistence session, and
 * then hand off to the doService method. Subclasses 
 * should override this doService method to provide the 
 * processing they require.
 *
 * Assumes a initialized database connection and domain
 * coupling machinery (provided by CCMApplicationListener
 * during container startup).
 *
 * Actually, the class does nothing at all!
 */
public class BaseFilter implements Filter {

    private static Logger s_log = Logger.getLogger(BaseFilter.class);

    /**
     * 
     * @param sconfig
     * @throws javax.servlet.ServletException
     */
    public final void init(final FilterConfig sconfig) throws ServletException {
        if (s_log.isInfoEnabled()) {
            s_log.info("Initializing filter " + sconfig.getFilterName() +
                       " (class: " + getClass().getName() + ")");
        }
        
        doInit();
    }

    /**
     * 
     * @throws javax.servlet.ServletException
     */
    protected void doInit() throws ServletException {
        // Empty
    }

    /**
     * 
     */
    public final void destroy() {
        if (s_log.isInfoEnabled()) {
            s_log.info
                ("Destroying filter " + getClass().getName());
        }

        doDestroy();
    }

    /**
     * 
     */
    protected void doDestroy() {
        // Empty
    }

    /**
     * 
     * @param sreq
     * @param sresp
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void doFilter(ServletRequest sreq,
                         ServletResponse sresp,
                         FilterChain chain)
        throws java.io.IOException,
               ServletException {

        if (s_log.isInfoEnabled()) {
            s_log.info("Filtering request " + sreq);
        }

        doService((HttpServletRequest)sreq,
                  (HttpServletResponse)sresp,
                  chain);
    }

    /**
     * This is the extension point for users of this class.
     */
    protected void doService(final HttpServletRequest sreq,
                             final HttpServletResponse sresp,
                             final FilterChain chain)
            throws ServletException, IOException {
        // Empty
    }
}

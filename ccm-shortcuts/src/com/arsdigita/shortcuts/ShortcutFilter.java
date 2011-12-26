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

package com.arsdigita.shortcuts;

import com.arsdigita.web.BaseFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ShortcutFilter extends BaseFilter {
    
    private static final Logger s_log = Logger.getLogger(ShortcutFilter.class);

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp,
                             FilterChain chain)
        throws IOException,
               ServletException {

        //String path = sreq.getPathInfo();
        String path = sreq.getRequestURI();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sreq path info: " + sreq.getPathInfo() + 
                        ", path translated: " + sreq.getPathTranslated() +
                        ", URI: " + sreq.getRequestURI());
        }
        
        if (path == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No path, passing off to next filter ");
            }

            chain.doFilter(sreq, sresp);
            return;
        }
        
        String key = ShortcutUtil.cleanURLKey(path);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Check key " + key);
        }
        String target = ShortcutUtil.getTarget(key);
        if (target != null) {
            String urlVars = sreq.getQueryString();
            if ( urlVars != null ) {
                target = target + "?" + urlVars;
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending to " + target);
            }

            sresp.sendRedirect(target);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No shortcut, passing off to next filter ");
            }

            chain.doFilter(sreq, sresp);
        }

    }
}

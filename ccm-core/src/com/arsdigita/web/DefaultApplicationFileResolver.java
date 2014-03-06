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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import org.apache.log4j.Logger;


public class DefaultApplicationFileResolver implements ApplicationFileResolver {

    private static final String[] WELCOME_FILES = new String[] {
        "index.jsp", "index.html"
    };

    private static Logger s_log = 
        Logger.getLogger(DefaultApplicationFileResolver.class);

    /**
     * 
     * @param templatePath
     * @param sreq
     * @param sresp
     * @param app
     * @return
     */
    @Override
    public RequestDispatcher resolve(String templatePath,
                                     HttpServletRequest sreq,
                                     HttpServletResponse sresp,
                                     Application app) {
        // XXX proper list of dependent & customization webapps to search
        String[] webapps = new String[] {
                                         app.getContextPath(), "ROOT"
                                        };
        String pathInfo = sreq.getPathInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolving resource for " + pathInfo);
        }

        String node = app.getPath();
        do {
            String path = templatePath + node + pathInfo;

            
            if (path.endsWith("/")) {
                for (int i = 0 ; i < WELCOME_FILES.length ; i++) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Trying welcome resource " + 
                                    path + WELCOME_FILES[i]);
                    }

                    RequestDispatcher rd = Web.findResourceDispatcher(
                                                   webapps,
                                                   path + WELCOME_FILES[i]);
                    if (rd != null) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Got dispatcher " + rd);
                        }
                        return rd;
                    }
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Trying resource " + path);
                }
                
                RequestDispatcher rd = Web.findResourceDispatcher(
                    webapps,
                    path);
                if (rd != null) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got dispatcher " + rd);
                    }
                    return rd;
                }
            }
            if ("".equals(node)) {
                node = null;
            } else {
                int index = node.lastIndexOf("/", node.length() - 2);
                node = node.substring(0, index);
            }
        } while (node != null);

        if (s_log.isDebugEnabled()) {
            s_log.debug("No dispatcher found");
        }
        
        return null;
    }
    
}

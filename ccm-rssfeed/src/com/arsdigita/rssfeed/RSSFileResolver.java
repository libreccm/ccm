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

package com.arsdigita.rssfeed;


import com.arsdigita.web.DefaultApplicationFileResolver;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;


import org.apache.log4j.Logger;

public class RSSFileResolver extends DefaultApplicationFileResolver {

    private static final Logger s_log = 
        Logger.getLogger(RSSFileResolver.class);

    public RequestDispatcher resolve(String templatePath,
                                     HttpServletRequest sreq,
                                     HttpServletResponse sresp,
                                     Application app) {
        String[] webapps = new String[] {
            app.getContextPath(), "ROOT"
        };

        String pathInfo = sreq.getPathInfo();
        if (pathInfo.endsWith(".rss") ||
            pathInfo.endsWith(".xml")) {
            // Translate .rss or .xml into .jsp
            pathInfo = pathInfo.substring(0, pathInfo.length() - 3) + "jsp";
            String node = app.getPath();
            do {
                String path = templatePath + node + pathInfo;
                
                
                if (path.endsWith("/")) {
                    path = path + "index.jsp";
                }

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
        } else {
            return super.resolve(templatePath,
                                 sreq,
                                 sresp,
                                 app);
        }
    }
}

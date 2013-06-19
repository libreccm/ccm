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

import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinderNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet to redirect an OID based request to a complete url in an application's
 * url tree and is one of the basic servlets required by any ccm-core application
 * instance to work correctly.
 *
 * It has to be declared in an application web.xml, typically:
 *   <servlet>
 *     <servlet-name>oid-redirect</servlet-name>
 *     <servlet-class>com.arsdigita.web.OIDRedirectServlet</servlet-class>
 *   </servlet>
 *   ....
 *   ....
 *   <servlet-mapping>
 *     <servlet-name>oid-redirect</servlet-name>
 *     <url-pattern>/redirect/*</url-pattern>
 *   </servlet-mapping>
 * 
 */
public class OIDRedirectServlet extends BaseServlet {

    private static final Logger s_log =
                                Logger.getLogger(OIDRedirectServlet.class);
    public static final String OID_PARAM = "oid";
    private static final OIDParameter param = new OIDParameter(OID_PARAM);

    @Override
    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
            throws ServletException, IOException {

        OID oid = null;
        try {
            // extract parameter named OID_PARAM (="oid") from sreq object
            // searches for something like oid=Article-id-167013
            oid = (OID) param.transformValue(sreq);
        } catch (Exception e) {
            // invalid OID value, return 400 Bad Request
            sresp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (oid == null) {
            // fix for non-encoded OIDs,
            // put in content body text using the link functionality
            try {
                oid = URLService.getNonencodedOID(sreq);
            } catch (Exception e) {
                // invalid OID value, return 400 Bad Request
                sresp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Tried to read non encoded OID, result: " + oid);
            }

            if (oid == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No OID parameter supplied");
                }
                sresp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        try {
            String context = sreq.getParameter("context");
            String url = URLService.locate(oid, context);

            /* Addition by Jensp: */
            Map<?, ?> parameters = sreq.getParameterMap();
            StringBuilder urlParams = new StringBuilder();
            for (Map.Entry<?, ?> entry : parameters.entrySet()) {
                if (!entry.getKey().equals("oid")) {
                    if (urlParams.length() == 0) {
                        urlParams.append('?');
                    } else {
                        urlParams.append('&');
                    }
                    String key = (String) entry.getKey();
                    String[] value = (String[]) entry.getValue();
                    if (value.length > 0) {
                        urlParams.append(String.format("%s=%s", key, value[0]));
                    }
                }
            }
            url = url.concat(urlParams.toString());
            /* JensP END */

            if (s_log.isDebugEnabled()) {
                s_log.debug("Redirecting oid " + oid + " to " + url);
            }
            throw new RedirectSignal(url, false);

        } catch (URLFinderNotFoundException ex) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No URL finder for oid " + oid);
            }
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
            
        } catch (NoValidURLException ex) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No URL for oid " + oid);
            }
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }
}

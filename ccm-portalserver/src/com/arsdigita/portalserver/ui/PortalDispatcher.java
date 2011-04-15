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
package com.arsdigita.portalserver.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.PageDispatcher;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.portalserver.ui.admin.PortalAdminPage;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/PortalDispatcher.java#5 $
 */
public class PortalDispatcher extends BebopMapDispatcher {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/PortalDispatcher.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log = Logger.getLogger
        (PortalDispatcher.class);

    // XXX Move these to constant pool.

    protected static final String PORTAL_HOME_PAGE = "index.xhtml";
    protected static final String PORTAL_ADMIN_PAGE = "admin";
    protected static final String PORTAL_PARTICIPANT_PAGE = "participants";

    private Dispatcher m_portalsiteAdminDispatcher;

    public PortalDispatcher() {
        super();

        Page portalHomePage = new PortalHomePage();

        Map m = new HashMap();
        m.put("", portalHomePage);
        m.put(PORTAL_HOME_PAGE, portalHomePage);
        m.put(PORTAL_PARTICIPANT_PAGE, PortalParticipants.createPage());
        setMap(m);
    }

    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext context)
        throws IOException, ServletException {
        String remainingURLPart = context.getRemainingURLPart();


        if (s_log.isDebugEnabled()) {
            s_log.debug("Remaining URL part is '" + remainingURLPart + "'");
        }

        if (remainingURLPart.startsWith(PORTAL_ADMIN_PAGE)) {
            if (m_portalsiteAdminDispatcher == null) {
                m_portalsiteAdminDispatcher =
                    new PageDispatcher(new PortalAdminPage());
            }

            m_portalsiteAdminDispatcher.dispatch(request, response, context);
        } else {
            super.dispatch(request, response, context);
        }
    }
}

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
package com.arsdigita.portalserver;

import com.arsdigita.bebop.Page;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portalserver.ui.PortalHomePage;
import com.arsdigita.portalserver.ui.PortalParticipants;
import com.arsdigita.portalserver.ui.admin.PortalAdminPage;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


// ////////////////////////////////////////////////////////////////////////////
//
// UNKNOWN whether this is unfinished work in progress or really used in some
// way.
//
// Might be a first shot to replace ui.PortalDispatcher by a new legacy free
// servlet because it uses the same pages (PortalHomePage, PortalParticipants
// but additionally a PortalAdminPage (from package c.ad.ps.ui.admin)
// On the other hand this is the same as m_portalsiteAdminDispatcher of
// PortalDispatcher
//
// ////////////////////////////////////////////////////////////////////////////


/**
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: PortalServlet.java  pboy $
 */
public class PortalServlet extends BaseApplicationServlet {

    private static final Logger s_log = Logger.getLogger
        (PortalServlet.class);

    private static final PresentationManager s_presManager =
        Templating.getPresentationManager();

    private static Page s_homePage = new PortalHomePage();
    private static Page s_particPage = PortalParticipants.createPage();
    private static Page s_adminPage = null;

    static {
    //  TransactionContext ctx = SessionManager.getSession().getTransactionContext();
    //  ctx.beginTxn();

        s_adminPage = new PortalAdminPage();

    //  ctx.commitTxn();
    }

    public void doService(HttpServletRequest sreq,
                          HttpServletResponse sresp,
                          Application app)
            throws ServletException, IOException {
        s_log.debug("PortalServlet.doService called for request '" +
                    sreq.getRequestURI() + "'");

        String path = sreq.getServletPath();
        Document doc = null;

        if (path.endsWith("participants")) {
            doc = s_particPage.buildDocument(sreq, sresp);
        } else if (path.endsWith("admin")) {
            doc = s_adminPage.buildDocument(sreq, sresp);
        } else {
            doc = s_homePage.buildDocument(sreq, sresp);
        }

        s_presManager.servePage(doc, sreq, sresp);
    }
}

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
package com.arsdigita.bebop.page;

import com.arsdigita.bebop.Page;
import com.arsdigita.templating.Templating;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.xml.Document;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.Application;
import com.arsdigita.util.Assert;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;

import java.io.IOException;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BebopApplicationServlet.java 1372 2006-11-13 09:22:54Z chrisgilbert23 $
 * 
 * chris gilbert - allow BebopApplicationServlet pages to disable client/middleware
 */
public class BebopApplicationServlet extends BaseApplicationServlet {
    public static final String versionId =
        "$Id: BebopApplicationServlet.java 1372 2006-11-13 09:22:54Z chrisgilbert23 $" +
        "$Author: chrisgilbert23 $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (BebopApplicationServlet.class);

    // String pathInfo => Page page
    private final Map m_pages = new HashMap();
    // Set of pathinfo
    private final Set m_clientCacheDisabledPages = new HashSet();

    public void init() throws ServletException {
        super.init();
    }

    protected final void put(final String pathInfo,
                             final Page page) {
        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        Assert.isTrue(pathInfo.startsWith("/"), "path starts with '/'");

        m_pages.put(pathInfo, page);
    }
    /**
     *
     * disable client/middleware caching of specified page. 
     * @param pathinfo - the same path used to add the page when put was called
     */
    protected final void disableClientCaching(String pathInfo) {
        Assert.exists(pathInfo, String.class);
	Assert.isTrue(m_pages.containsKey(pathInfo), "Page " + pathInfo + " has not been put in servlet");
   	m_clientCacheDisabledPages.add(pathInfo);
    }

    protected final void doService(final HttpServletRequest sreq,
                                   final HttpServletResponse sresp,
                                   final Application app)
            throws ServletException, IOException {
        final String pathInfo = sreq.getPathInfo();

        Assert.exists(pathInfo, "String pathInfo");

        final Page page = (Page) m_pages.get(pathInfo);

        if (page == null) {
            sresp.sendError(404, "Application not found");
            throw new IllegalStateException("No such page for path " + pathInfo);
        } else {
            DeveloperSupport.startStage("Dispatcher page.buildDocument");
            if (m_clientCacheDisabledPages.contains(pathInfo)) {
            	DispatcherHelper.cacheDisable(sresp);
            }
            final Document doc = page.buildDocument(sreq, sresp);
            DeveloperSupport.endStage("Dispatcher page.buildDocument");


            DeveloperSupport.startStage("Dispatcher presMgr.servePage");
            PresentationManager pm = Templating.getPresentationManager();
            pm.servePage(doc, sreq, sresp);
            DeveloperSupport.endStage("Dispatcher presMgr.servePage");
        }
    }
}

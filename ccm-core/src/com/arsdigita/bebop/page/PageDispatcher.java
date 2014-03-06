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
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Class for dispatching a single Bebop page object.  Generates the
 * XML output for a Bebop page and renders it with an XSL template.
 * 
 * @version $Id: PageDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PageDispatcher implements Dispatcher {

    private static final Logger s_log = Logger.getLogger(PageDispatcher.class);

    private final Page m_page;
    private final PresentationManager m_pres;

    /**
     * Creates a new page dispatcher for a page object and a
     * PresentationManager.
     * @param page
     * @param pres
     */
    public PageDispatcher(final Page page,
                          final PresentationManager pres) {
        m_page = page;
        m_pres = pres;
    }

    /**
     * Creates a new page dispatcher for a page object.  Uses the
     * default presentation manager.
     * @param page
     */
    public PageDispatcher(final Page page) {
        m_page = page;
        m_pres = Templating.getPresentationManager();
    }

    /**
     * Serves the Bebop page using the specified
     * <code>PresentationManager</code>.
     * @param req
     * @param resp
     * @param ctx
     */
    @Override
    public void dispatch(final HttpServletRequest req,
                         final HttpServletResponse resp,
                         final RequestContext ctx)
            throws IOException, ServletException {
        Assert.exists(m_pres, PresentationManager.class);

        DeveloperSupport.startStage("Dispatcher page.buildDocument");
        com.arsdigita.xml.Document doc = m_page.buildDocument(req, resp);
        DeveloperSupport.endStage("Dispatcher page.buildDocument");

        DeveloperSupport.startStage("Dispatcher presMgr.servePage");
        m_pres.servePage(doc, req, resp);
        DeveloperSupport.endStage("Dispatcher presMgr.servePage");
    }

    /**
     * Returns the wrapped page that this dispatcher serves.
     *
     * @return the wrapped <code>Page</code> object
     */
    public Page getPage() {
        return m_page;
    }
}

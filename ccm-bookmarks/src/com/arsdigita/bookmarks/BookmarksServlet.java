/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.bookmarks;

import com.arsdigita.bebop.Page;
import com.arsdigita.bookmarks.ui.BookmarkBasePage;
import com.arsdigita.bookmarks.ui.BookmarkEditPane;
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


/**
 * Bookmarks Application Servlet class, central entry point  to 
 * create and process the applications UI.
 * 
 * We should have subclassed BebopApplicationServlet but couldn't overwrite
 * doService() method to add permission checking. So we use our own page
 * mapping. The general logic is the same as for BebopApplicationServlet.
 * {@see com.arsdigita.bebop.page.BebopApplicationServlet}
 * 
 * @author pb
 */
public class BookmarksServlet extends BaseApplicationServlet {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(BookmarksServlet.class);

    private Page indexPage;

    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {

        indexPage = buildIndexPage();

    }


    /**
     * Central service method, checks for required permission, determines the
     * requested page and passes the page object to PresentationManager.
     */
    public final void doService(HttpServletRequest sreq,
                                HttpServletResponse sresp,
                                Application app)
                      throws ServletException, IOException {

        if (indexPage != null) {

            final Document doc = indexPage.buildDocument(sreq, sresp);

            PresentationManager pm = Templating.getPresentationManager();
            pm.servePage(doc, sreq, sresp);

        } else {

            sresp.sendError(404, "No such page.");

        }
                
    }

    /**
     * 
     * @return 
     */
    private Page buildIndexPage() {

        BookmarkBasePage p = new BookmarkBasePage();

        p.addRequestListener(new ApplicationAuthenticationListener("admin"));
        p.getBody().add(new BookmarkEditPane());

        p.lock();

        return p;
    }

    
}

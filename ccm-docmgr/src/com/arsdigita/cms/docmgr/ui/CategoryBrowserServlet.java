/*
 * Copyright (C) 2012 Peter boy (pboy@barkhof.uni-bremen.de
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

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.TabbedPane;

import org.apache.log4j.Logger;

/**
 * Application servlet for ccm-docmgr's DocumentCategoryBrowser and 
 * LegacyCategoryBrowser application which serves all request made for 
 * the application's UI. 
 * 
 * CagtegoryBrowserServlet is called by BaseApplicationServlet which has
 * determined that it is associated with a request URL.
 * 
 * The servlet has to be included in servlet container's deployment descriptor,
 * see teh domain classes' getServletPath() method for details
 * about web.xml record. It is NOT directly referenced by any other class.
 * 
 * It determines whether a <tt>Page</tt> has been registered to the URL and
 * if so passes the request to that page. Otherwise it hands  the request
 * to the TemplateResolver to find an appropriate JSP file.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: RepositoryServlet.java 2161 2012-02-26 00:16:13Z pboy $
 */
public class CategoryBrowserServlet extends RepositoryServlet 
                                    implements DMConstants         {

    /** Private logger instance to faciliate debugging procedures             */
    private static final Logger s_log = Logger.getLogger(
                                               CategoryBrowserServlet.class);


    /**
     * Use parent's class initialization extension point to perform additional
     * initialisation tasks. Here: build the UI pages.
     */
    @Override
    public void doInit()  {
        
        addPage( "/", buildCategoryBrowserIndexPage() );
        addPage( "/file", buildFileInfoPage());
        // search is a tab, for now. 
        //addPage("/search", buildSearchPage());
        //addPage("/search/file", buildFileInfoPage());

    }

    
    /**
     * Build index page to browse documents by category
     */
    private Page buildCategoryBrowserIndexPage() {

        Page p = new DocmgrBasePage();

        /* Create main administration tab.         */
        TabbedPane tb = new TabbedPane();
        tb.setIdAttr("page-body");

        p.add(new BrowseCatDocsPane());
        p.lock();

        return p;

    }
    
}

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

package org.undp.weblog;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopApplicationServlet;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.undp.weblog.ui.WebLogPage;


/**
 * WebLog Application Servlet class, central entry point to create and process
 * the applications UI.
 * 
 * We should have subclassed BebopApplicationServlet but couldn't overwrite
 * doService() method to add permission checking. So we use our own page
 * mapping. The general logic is the same as for BebopApplicationServlet.
 * {@see com.arsdigita.bebop.page.BebopApplicationServlet}
 * 
 * @author pb
 */
public class WebLogServlet extends BebopApplicationServlet {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(WebLogServlet.class);

    /**
     * User extension point used to create the pages to server and setup a 
     * URL - page mapping.
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {
        s_log.debug(" Initialize WebLogServlet ...");

        Page indexPage = new WebLogPage();
        put("/",indexPage);
        put("/index.jsp",indexPage);

    }

}

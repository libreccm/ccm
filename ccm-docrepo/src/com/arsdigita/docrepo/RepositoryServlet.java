/*
 * Copyright (C) 2011 Peter boy (pboy@barkhof.uni-bremen.de
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

package com.arsdigita.docrepo;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.page.BebopApplicationServlet;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.docrepo.File;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.Web;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Category;

import java.io.*;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 *
 * @author pb
 */
public class RepositoryServlet  extends BebopApplicationServlet {

    /** Private logger instance to faciliate debugging procedures         */
    private static final Logger s_log = Logger.getLogger(RepositoryServlet.class);


    /**
     * Servlet Initialisation, builds the UI elements (various panes)
     * @throws ServletException 
     */
    @Override
    public void init() throws ServletException {
        super.init();
        s_log.debug("creating DocRepo page");


//      Page index = buildIndexPage();
//      Page admin = buildAdminIndexPage();

//      put("/", index);
//      put("/index.jsp", index);
//      put("/one.jsp", index);

  //    put("admin", admin);
  //    put("admin/index.jsp", admin);

    }

    


}

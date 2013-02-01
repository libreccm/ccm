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
 *
 */
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * Page for redirecting results of Search to the item to be
 * displayed.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Id: SearchResultRedirector.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class CMSSearchResultRedirector extends CMSApplicationPage  {

    /**
     * Names of required parameters passed in the request
     */
    public static final String ITEM_ID = "item_id";
    public static final String CONTEXT = "context";
    private static final  Logger s_log = Logger.getLogger(CMSSearchResultRedirector.class);

    /**
     * Construct a new SearchResultRedirector
     */
    public CMSSearchResultRedirector() {
        super();
    }


    /***
     * override dispatcher to do redirect to page displaying item.
     ***/
    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp, RequestContext actx) {

        // Get item_id
        BigDecimal id;

        try {
            id = new BigDecimal(getParam(ITEM_ID, req));
        } catch (NumberFormatException e) {
            s_log.error("Invalid CMS Item id", e);
	    String itemID = (String) getParam(ITEM_ID,req);
            throw new RuntimeException( (String) GlobalizationUtil.globalize(
            "cms.ui.invalid_item_id", new Object[] { itemID }).localize() + e.getMessage());
        }

        // Get context
        String context = getParam(CONTEXT, req);

        // Get section and item
        ContentSection section;
        ContentItem item = new ContentItem(id);
        section = item.getContentSection();

        // Get url
        ItemResolver resolver = section.getItemResolver();
        PageState state;
        try {
            state = new PageState(this, req, resp);
        } catch (javax.servlet.ServletException ex) {
            throw new UncheckedWrapperException("Servlet Error: " + ex.getMessage(), ex);
        }
        String url = resolver.generateItemURL ( state, item, section, context );

        // redirect to url
        try {
            DispatcherHelper.sendRedirect(req, resp, url);
        } catch (IOException e) {
            UncheckedWrapperException.throwLoggedException(getClass(), "Could not redirect: " + e.getMessage(), e );
        }
    }


    /**
     * Get a parameter from the request
     */

    private String getParam ( String paramName, HttpServletRequest req) {
        String [] params = req.getParameterValues(paramName);
        if (params.length != 1) {
            s_log.error("Not one " + paramName);
            throw new RuntimeException("Not one " + paramName);
        }
        return params[0];
    }
}

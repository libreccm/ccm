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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherChain;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.sitenode.SiteNodeRequestContext;
import com.arsdigita.util.Assert;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b><font color="red">Unsupported</font></b> Refactored content
 * section dispatcher (under development).
 *
 * @author Karl Goldstein (karlg@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ContentSectionDispatcher.java 287 2005-02-22 00:29:02Z sskracic $ 
 **/
public class ContentSectionDispatcher implements Dispatcher {

    public static final String CONTENT_ITEM = "com.arsdigita.cms.dispatcher.item";

    static final String CONTENT_SECTION = "com.arsdigita.cms.dispatcher.section";

    private DispatcherChain dispatcherChain = new DispatcherChain();

    public ContentSectionDispatcher() {

        dispatcherChain.addChainedDispatcher(new CMSDispatcher(true));
        dispatcherChain.addChainedDispatcher(new FileDispatcher());
        dispatcherChain.addChainedDispatcher(new ItemDispatcher());
        dispatcherChain.addChainedDispatcher(new CMSDispatcher());
    }

    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext context)
        throws IOException, ServletException {

        setContentSection(request, (SiteNodeRequestContext) context);
        dispatcherChain.dispatch(request, response, context);
    }

    /**
     * Fetches the content section from the request attributes.
     *
     * @param request The HTTP request
     * @return The content section
     * @pre ( request != null )
     */
    public static ContentSection getContentSection(HttpServletRequest request) {
        return (ContentSection) request.getAttribute(CONTENT_SECTION);
    }

    /**
     * Fetches the content item from the request attributes.
     *
     * @param request The HTTP request
     * @return The content item
     * @pre ( request != null )
     */
    public static ContentItem getContentItem(HttpServletRequest request) {
        return (ContentItem) request.getAttribute(CONTENT_ITEM);
    }

    /**
     * Looks up the current content section using the remaining URL
     * stored in the request context object and the SiteNode class.
     *
     * @param url The section URL stub
     * @return The current Content Section
     */
    private void setContentSection(HttpServletRequest request,
                                   SiteNodeRequestContext actx)
        throws ServletException {

        try {

            // Fetch the current site node from the request context;
            SiteNode sn = actx.getSiteNode();
            ContentSection section = ContentSection.getSectionFromNode(sn);
            Assert.exists(section);

            request.setAttribute(CONTENT_SECTION, section);

        } catch (DataObjectNotFoundException e) {

            throw new ServletException(e);
        }
    }

    /**
     * Checks that the current user has permission to access the admin pages.
     **/
    public static boolean checkAdminAccess(HttpServletRequest request,
                                           ContentSection section) {

        User user = Utilities.getCurrentUser(request);

        SecurityManager sm = new SecurityManager(section);

        return sm.canAccess(user, SecurityManager.ADMIN_PAGES);
    }
}

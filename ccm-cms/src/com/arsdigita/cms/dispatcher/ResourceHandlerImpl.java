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
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.Assert;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An interface for resources that can be served.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #10 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public abstract class ResourceHandlerImpl implements ResourceHandler {

    public static final String versionId = "$Id: ResourceHandlerImpl.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    /**
     * This method is called by the {@link com.arsdigita.dispatcher.Dispatcher}
     * that initializes this page.
     */
    public void init() throws ServletException {
        // Do nothing.
    }

    /**
     * Fetch the request-local content section.
     *
     * @param request The HTTP request
     * @return The current content section
     */
    public ContentSection getContentSection(HttpServletRequest request) {
        // resets all content sections associations
        ContentSection section = CMSDispatcher.getContentSection(request);
        Assert.exists(section);
        return section;
    }

    /**
     * Fetch the request-local content item.
     *
     * @param request The HTTP request
     * @return The current content item
     */
    public ContentItem getContentItem(HttpServletRequest request) {
        // resets all content item associations
        return CMSDispatcher.getContentItem(request);
    }

    public void checkUserAccess(HttpServletRequest request,
                                HttpServletResponse response,
                                RequestContext actx,
                                ContentItem item) {
        User user = KernelHelper.getCurrentUser(request);

        PrivilegeDescriptor view = PrivilegeDescriptor.get
            (SecurityManager.CMS_READ_ITEM);
        PermissionDescriptor perm = new PermissionDescriptor(view, item, user);

        if (!PermissionService.checkPermission(perm)) {
            throw new AccessDeniedException( (String) GlobalizationUtil.globalize("cms.dispatcher.no_permission_to_access_resource").localize());
        }
    }


    /**
     * Services this resource.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     */
    public abstract void dispatch(HttpServletRequest request,
                                  HttpServletResponse response,
                                  RequestContext actx)
        throws IOException, ServletException;

}

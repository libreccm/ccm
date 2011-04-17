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
package com.arsdigita.ui.permissions;

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.ui.login.UserAuthenticationListener;

/**
 * Dispatcher for the
 * UI package.
 * Manages permissions admin pages.
 * The dispatcher is mounted at /permissions/
 * Below that, an index page is mounted at "" or "index",
 * a particualar object's permission page is mounted at "one".
 *
 * @author sdeusch@arsditgita.com
 * @version $Id: PermissionsDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class PermissionsDispatcher extends BebopMapDispatcher
    implements PermissionsConstants {

    public final static String APPLICATION_NAME = "permissions";

    /**
     * Initializes dispatcher by registering above listed URLs
     * with bebop pages.
     */

    public PermissionsDispatcher() {
        Page index = buildIndexPage();
        Page single = buildItemPage();
        
        addPage("", index);
        addPage("index", index);
        addPage("one", single);
        addPage("grant", single);
        addPage("denied", buildDeniedPage());
    }

    protected void preprocessRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext ctx,
                                     String url) {
        // No caching thankyou!
        DispatcherHelper.cacheDisable(resp);
    }

    private Page buildIndexPage() {
        Label title = new Label(PERMISSIONS_INDEX);
        title.setClassAttr("heading");

        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       title);
        p.addRequestListener(new UserAuthenticationListener());
        p.add(new IndexPanel());
        p.lock();
        return p;
    }

    private Page buildDeniedPage() {
        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       new Label(PAGE_DENIED_TITLE));
        Label label = new Label();
        label.setClassAttr("AccessDenied");
        p.add(label);
        p.lock();
        return p;
    }

    private Page buildItemPage() {
        PermissionsPane pane = new PermissionsPane();
        
        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       pane.getTitle());
        p.addRequestListener(new UserAuthenticationListener());
        p.add(pane);
        p.lock();
        return p;
    }
}

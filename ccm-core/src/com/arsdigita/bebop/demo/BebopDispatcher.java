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
package com.arsdigita.bebop.demo;


import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.templating.Templating;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Remnants of the once glorious Bebop dispatcher class.
 * Serves one page on all urls, notifying visitors about the merging
 * of demo-bebop into bebop-demo.
 *
 * @version $Id: BebopDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BebopDispatcher implements Dispatcher {

    private static Page s_moved = buildPageMoved();

    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
        throws javax.servlet.ServletException, java.io.IOException
    {
        Templating.getPresentationManager()
            .servePage(s_moved.buildDocument(req, resp), req, resp);
    }

    /** notification page, for legacy reasons
     */
    static Page buildPageMoved() {
        Page p = new Page("Relocated");

        p.add(new Label(GlobalizationUtil.globalize("bebop.demo.demobebop_has_been_merged_with_bebopdemo")));
        p.add(new Link( new Label(GlobalizationUtil.globalize("bebop.demo.go_there")),  "../bebop-demo"));
        p.lock();
        return p;
    }
}

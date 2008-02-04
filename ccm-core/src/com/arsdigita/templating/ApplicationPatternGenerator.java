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
package com.arsdigita.templating;


import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.sitenode.SiteNodeRequestContext;
import com.arsdigita.kernel.SiteNode;

import com.arsdigita.web.Web;
import com.arsdigita.web.Application;

import javax.servlet.http.HttpServletRequest;



/**
 * Generates a set of pattern values based on the application
 * key, eg content-center, content-section.
 */
public class ApplicationPatternGenerator implements PatternGenerator {
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        final Application app = Web.getContext().getApplication();
        
        if (app != null) {
            return new String[] {
                app.getPackageType().getKey()
            };
        }
        
        SiteNodeRequestContext ctx = (SiteNodeRequestContext)
            DispatcherHelper.getRequestContext(req);
        
        SiteNode node = ctx.getSiteNode();
        
        if (node != null) {
            return new String[] {
                node.getPackageInstance().getType().getKey()
            };
        }
        
        return new String[] {};
    }
}

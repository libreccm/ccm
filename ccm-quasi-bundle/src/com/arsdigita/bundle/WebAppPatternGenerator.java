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
 */

package com.arsdigita.bundle;


import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.templating.PatternGenerator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * Generates a set of patterns corresponding to the current 
 * web application prefix
 */
public class WebAppPatternGenerator implements PatternGenerator {

    private static final Logger s_log = 
         Logger.getLogger(WebAppPatternGenerator.class);

    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        Application app = Web.getContext().getApplication();
        String ctx = app == null ? null : app.getContextPath();

        if (app == null || 
            ctx == null ||
            "".equals(ctx)) {
            return new String[] { Web.ROOT_WEBAPP };
        }
        
        if (ctx.startsWith("/")) {
            ctx = ctx.substring(1);
        }
        
        return new String[] { ctx };
    }
}

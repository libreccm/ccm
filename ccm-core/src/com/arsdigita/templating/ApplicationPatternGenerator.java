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

import org.apache.log4j.Logger;



/**
 * Generates a set of pattern values based on the application
 * key, eg content-center, content-section.
 */
public class ApplicationPatternGenerator implements PatternGenerator {

    /** Private logger instance for debugging purpose  */
    private static final Logger s_log = Logger.getLogger(PatternGenerator.class);

    /** 
     * Implementation iof the Interface class.
     * 
     * @param key
     * @param req
     * @return
     */
    public String[] generateValues(String key,
                                   HttpServletRequest req) {

        s_log.debug("Processing Application with key: " + key );

        final Application app = Web.getContext().getApplication();
        if (app != null) {
            String[] returnValue = { app.getApplicationType().getName() };
            s_log.debug("Found application >>"+returnValue+"<< in Application.");
            return returnValue;
        }
        
        // SiteNodeRequestContext is deprecated and replaced by web.WebContext
        // used in the code above (Web.getContext(). 
        // This code should never be executed.
        // Findings: SideNode is requirred for modules which dont use
        // legacy-compatible applications but package-type apps. content-center
        // and cms-service are 2 examples. Code can be eliminated when all apps
        // will use web.Application for loading and instantiation.
        s_log.warn("ApplicationType for >>" +key +
                    "<< not found. Trying SiteNodes instead.");
        
        
        SiteNodeRequestContext ctx = (SiteNodeRequestContext)
            DispatcherHelper.getRequestContext(req);
        
        SiteNode node = ctx.getSiteNode();
        
        if (node != null) {
            String[] returnValue = {
                                    node.getPackageInstance().getType().getKey()
                                   };
            s_log.debug("Found node >>" + returnValue + "<< in SiteNodes.");
            return returnValue;
        }

        s_log.debug("ApplicationType for " +key +
                    " could not be found in SiteNodes either. Returning empty String[]");
        
        return new String[] {};
    }
}

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

package com.arsdigita.london.subsite;

import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import com.arsdigita.util.Assert;
import com.arsdigita.dispatcher.DispatcherHelper;

import com.arsdigita.london.subsite.Site;

/**
 * <p>The entry point into all the global state that CCM Subsite code expects to
 * have available to it when running, e.g. the current content section,
 * current item   </p>
 *
 * <p>This is a session object that provides an environment in which
 * code can execute. The SubsiteContext contains all session-specific
 * variables.  One session object is maintained per thread.</p>
 *
 * <p>Accessors of this class will assert that the item it returned is
 * not null. If the caller wants to handle the case where an item is
 * null explicitly, then use the hasContentItem and hasContentSection
 * methods first.
 *
 * @see com.arsdigita.kernel.KernelContext
 * @see com.arsdigita.cms.Subsite
 *
 * @author Daniel Berrange
 * @version $Id: SubsiteContext.java 755 2005-09-02 13:42:47Z sskracic $
 */
public final class SubsiteContext {

    private static final Logger s_log = Logger.getLogger(SubsiteContext.class);

    public static final String SITE_REQUEST_ATTRIBUTE  = Site.class.getName();
       

    /**
     * 
     */
    SubsiteContext() {}

    /**
     *
     * @return
     */
    public final String getDebugInfo() {
        String info = "Current state of " + this + ":\n" +
            "           getSite() -> " + getSite() + "\n";

        return info;
    }

    /**
     * Checks if a content site is available
     * @returns true if a content site is available
     */
    public final boolean hasSite() {
        HttpServletRequest request = DispatcherHelper.getRequest();
        return (request.getAttribute(SITE_REQUEST_ATTRIBUTE) != null);
    }

    /**
     * Gets the current content site
     * @pre hasSite() == true
     * @return the currently selected content site
     */
    public final Site getSite() {
        Site site = null;
        HttpServletRequest request = DispatcherHelper.getRequest();
        if(request.getAttribute(SITE_REQUEST_ATTRIBUTE) != null) {
            site = (Site)request.getAttribute(SITE_REQUEST_ATTRIBUTE);
            Assert.exists(site, Site.class);
        }
        return site;
    }    
}

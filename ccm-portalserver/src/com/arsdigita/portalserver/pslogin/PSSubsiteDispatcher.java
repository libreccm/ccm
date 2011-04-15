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
package com.arsdigita.portalserver.pslogin;

import com.arsdigita.ui.login.SubsiteDispatcher;
import com.arsdigita.kernel.security.Initializer;
import com.arsdigita.bebop.Page;
import java.util.Map;

// /////////////////////////////////////////////////////////////////////////////
//
//  Has to be refactored!
//  Uses methods in c.ad.kernel.security.Initializer which have been modified
//  and moved to ui
//
//  CURRENTLY NOT WORKING
//
// /////////////////////////////////////////////////////////////////////////////


/**
 * This class extends com.arsdigita.ui.login.SubsiteDispatcher
 * It changes the subsite dispatcher behavior by
 * directing logged-in users to a personal portal
 * rather than the default system portal
 * @author <a href="mailto:bryanche@arsdigita.com">Bryan Che</a>
 **/
public class PSSubsiteDispatcher extends SubsiteDispatcher {

    private static Page s_psPage = new PSPage();

    /**
     * Initializes dispatcher by registering URLs with bebop pages.
     * Replaces the portal mapping from an ACS workspace
     * to a portalserver portal
     **/
    public PSSubsiteDispatcher() {
        super();

        Map map = this.getMap();
        //override the default ACS workspace page
        //with a personal portal page

        // Funktioniert so nicht mehr, weil es die map in den alten
        // kernel.security.initializer nicht mehr gibt.
 // !!  put(map, Initializer.WORKSPACE_PAGE_KEY, s_psPage);


        setMap(map);
    }

    /**
     * Adds <url, page> to the given map, where URL is looked up from the
     * page map using the given key.  If the URL represents a directory
     * (ends with "/"), URL+"index" is also added to the map and URL-"/" is
     * redirected to URL.
     **/
    private void put(Map map, String key, Page page) {
 //!!   String url = Initializer.getURL(key);
 //     map.put(url, page);
 //     if (url.endsWith("/")) {
 //         map.put(url+"index", page);
 //         requireTrailingSlash(url.substring(0, url.length()-1));
 //     }
    }
}

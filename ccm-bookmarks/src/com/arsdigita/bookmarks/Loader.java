/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.bookmarks;


import com.arsdigita.bookmarks.ui.BookmarkPortlet;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * <p>Executes nonrecurring at install time and loads (installs and initializes)
 * the HTTP Auth application and type persistently into database.</p>
 *
 * @author Daniel Berrange
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */

/**
 *
 * @author pb
 */
public class Loader extends PackageLoader {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    /**
     * 
     * @param ctx 
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                ApplicationType bmrkAppType = loadBookmarksApp();
                loadBookmarksPortlet(bmrkAppType);
                setupDefaultBookmarkApplicationInstance();

            }
        }.run();
    }

    /**
     * Load the Bookmarks application into persistent storage.
     * 
     * @return Bookmarks application type, requirred to load the portlet type 
     */
    private ApplicationType loadBookmarksApp() {

        /* Create new type legacy free application type                 
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Bookmarks" will become "bookmarks".                              */
        ApplicationType type = new  ApplicationType("Bookmarks",
                                    Bookmarks.BASE_DATA_OBJECT_TYPE );

        type.setDescription("Bookmarks for a Portal");
        type.save();

        return type;
    }

    private void loadBookmarksPortlet(ApplicationType bmrkAppType ) {

		AppPortletType type = AppPortletType.createAppPortletType(
                                       "Portal Bookmarks",
                                       PortletType.NARROW_PROFILE,
                                       BookmarkPortlet.BASE_DATA_OBJECT_TYPE);
		type.setProviderApplicationType(bmrkAppType);
        type.setDescription("Displays bookmarks for this portal.");
        
    }


    /**
     * Instantiates the Bookmarks application admin instance.
     * 
     */
    public static void setupDefaultBookmarkApplicationInstance() {

        /* Determine a parent application. Bookmarks admin page will be 
         * installed beyond the admin's applications URL.                    */
        Application admin = Application.retrieveApplicationForPath("/admin/");

        // create application instance 
        // Whether a legacy compatible or a legacy free application is
        // created depends on the type of ApplicationType above. No need to
        // modify anything here in the migration process
        // old-style package key used as url fragment where to install the instance
        s_log.debug("Creating BookmarkApplication instance ...");

        Bookmarks app = Bookmarks.create("bookmarks", "Bookmarks", admin); 

        s_log.debug("Bookmarks instance " + " created.");
        s_log.debug("Done loading bookmarks.");
    }

}

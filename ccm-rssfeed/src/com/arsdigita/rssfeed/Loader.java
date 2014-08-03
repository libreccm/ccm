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

package com.arsdigita.rssfeed;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.rssfeed.portlet.WorkspaceDirectoryPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.URL;

import org.apache.log4j.Logger;

/**
 * Executes nonrecurring at install time and loads (installs and initializes)
 * the ccm-rssfeed package persistently into database.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     *
     * @param ctx
     */
    @Override
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {
            @Override
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                // CategoryPurpose is deprecated and replaced by terms in
                // some way. So this step has to be refactored in some way.
                // Category creation may be omitted and /channels/admin still
                // works, but /channels rsp. /channels/rss does not wether a
                // CategoryPurpose ist created or not.
                // RSS currently depends on an existing category domain in terms
                // an a domain mapping to this applicaion.
                /*
                String catKey = RSSFeed.getConfig().getCategoryKey();
                s_log.info("Setting RSS Category Key to " + catKey + ".");
                if (!CategoryPurpose.purposeExists(catKey)) {
                    (new CategoryPurpose(catKey, "RSS Feed")).save();
                }
                 */

                // load application type for admin application into database
                // (i.e. create application type)
                setupChannelControlCenter();

                // Load local feeds into database
            //  setupLocalFeeds();  // currently not working, incompatible with
                                    // changes in handling webapp's web context

                // load portlet type into database
                loadWorkspaceDirectoryPortlet();
            }
        }.run();
    }

    /**
     * Creates the application type for the admin application as an
     * (new style) legacy-free applicaiton and an instance of the admin
     * application.
     */
    public void setupChannelControlCenter() {

        /* Create legacy-free application type                               
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "RSSFeed" will become "rssfeed".                               */
        ApplicationType type = new ApplicationType( 
                                       "RSS Feed",
                                        RSSFeed.BASE_DATA_OBJECT_TYPE );
        type.setSingleton(true);
        type.setDescription("Provides RSS feed service");
      
        if (!Application.isInstalled(RSSFeed.BASE_DATA_OBJECT_TYPE,
                                     "/channels/")) {
            // create an (singelton) application instance
            Application app = Application
                              .createApplication(type,
                                                 "channels",
                                                 "RSS Service",
                                                 null);
            app.setDescription("RSS feed channels");
            app.save();
        }
    }

    /**
     * 
     */
    public void setupLocalFeeds() {

        URL external = URL.there("/channels/rss/external.rss", null);
        try {
            Feed feed = Feed.retrieve(external.getURL());
        } catch (DataObjectNotFoundException ex) {
            Feed feed = Feed.create(external.getURL(),
                                    "External feeds",
                                    "External rss content feeds",
                                    true);
            feed.save();
        }

        URL index = URL.there("/channels/rss/index.rss", null);
        try {
            Feed feed = Feed.retrieve(index.getURL());
        } catch (DataObjectNotFoundException ex) {
            Feed feed = Feed.create(index.getURL(),
                                    "Local content feeds",
                                    "Local CMS content feeds",
                                    true);
            feed.save();
        }
    }

	private void loadWorkspaceDirectoryPortlet() {
		PortletType type = PortletType.createPortletType("Workspace Directory",
				PortletType.WIDE_PROFILE,
				WorkspaceDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a list of workspaces");
	}

}

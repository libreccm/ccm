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

package com.arsdigita.london.rss;

import com.arsdigita.categorization.CategoryPurpose;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.london.rss.portlet.WorkspaceDirectoryPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.URL;

import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    public void run(final ScriptContext ctx) {

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                // CatgegoryPurpose is deprecated and replaced by terms in
                // some way. So this step may be ommitted.
                String catKey = RSS.getConfig().getCategoryKey();
                s_log.info("Setting RSS Category Key to " + catKey + ".");
                if (!CategoryPurpose.purposeExists(catKey)) {
                    (new CategoryPurpose(catKey, "RSS Feed")).save();
                }

                // load application type for admin application into database
                // (i.e. create application type)
                setupChannelControlCenter();

                // Load local fveeds into database
                setupLocalFeeds();

                // load portlet type into database
                loadWorkspaceDirectoryPortlet();
            }
        }.run();
    }

    /**
     * Creates the application type for the admin application as an
     * (old style) compatible applicaiton.
     */
    public void setupChannelControlCenter() {
        ApplicationSetup setup = new ApplicationSetup(s_log);

        setup.setApplicationObjectType(RSS.BASE_DATA_OBJECT_TYPE);
        setup.setKey("rss");
        setup.setTitle("RSS Channels");
        setup.setDescription("RSS Channels");
        setup.setSingleton(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new RSS(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();

        if (!Application.isInstalled(RSS.BASE_DATA_OBJECT_TYPE,
                                     "/channels/")) {
            Application app =
                Application.createApplication(type,
                                              "channels",
                                              "RSS",
                                              null);
            app.save();
        }
    }

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

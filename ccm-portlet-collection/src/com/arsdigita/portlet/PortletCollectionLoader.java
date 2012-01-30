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

package com.arsdigita.portlet;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.loader.PackageLoader;
// import com.arsdigita.portalworkspace.portlet.ApplicationDirectoryPortlet;
// import com.arsdigita.portalworkspace.portlet.ContentDirectoryPortlet;
// import com.arsdigita.portalworkspace.portlet.FreeformHTMLPortlet;
// import com.arsdigita.portalworkspace.portlet.LoginPortlet;
// import com.arsdigita.portalworkspace.portlet.MyWorkspacesPortlet ;
// import com.arsdigita.portalworkspace.portlet.RSSFeedPortlet;
// import com.arsdigita.portlet.TimeOfDayPortlet;
// import com.arsdigita.portalworkspace.portlet.WorkspaceNavigatorPortlet;
// import com.arsdigita.portalworkspace.portlet.WorkspaceSummaryPortlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Executes nonrecurring at install time and loads (and configures ) all the
 * portlets of the protlet collection in a default configuration.
 * 
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: Loader.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class PortletCollectionLoader extends PackageLoader {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(PortletCollectionLoader.class);


    /**
     * Standard constructor loads/registers the configuration parameter.
     */
    public PortletCollectionLoader() {
	/*
        register(m_isPublic);
		register(m_url);
		register(m_title);
     */
    }

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     *
     * @param ctx
     */
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

            //  loadApplicationDirectoryPortlet();
            //  loadContentDirectoryPortlet();
            //  loadFreeformHTMLPortlet();
            //  loadLoginPortlet();
            //  loadMyWorkspacesPortlet();
            //  loadRSSFeedPortlet();
                loadTimeOfDayPortlet();
            //  loadWorkspaceNavigatorPortlet();
            //  loadWorkspaceSummaryPortlet();
            }
        }.run();
    }



    /**
     *
     */
/*
    private void loadApplicationDirectoryPortlet() {
		PortletType type = PortletType.createPortletType(
				"Application Directory", PortletType.WIDE_PROFILE,
				ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a list of portal workspace applications");
	}

	private void loadContentDirectoryPortlet() {
		PortletType type = PortletType.createPortletType("Content Directory",
				PortletType.WIDE_PROFILE,
				ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the content directory categories");
	}

	private void loadFreeformHTMLPortlet() {
		PortletType type = PortletType.createPortletType("Freeform HTML",
				PortletType.WIDE_PROFILE,
				FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a freeform block of HTML");
	}

	private void loadLoginPortlet() {
		PortletType type = PortletType.createPortletType("Site Login",
				PortletType.WIDE_PROFILE, LoginPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Display a login form or user details");
	}

	private void loadMyWorkspacesPortlet() {
		PortletType type = PortletType.createPortletType("My Workspaces",
				PortletType.WIDE_PROFILE, MyWorkspacesPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("MyWorkspaces: Display ????");
	}

	private void loadRSSFeedPortlet() {
		PortletType type = PortletType.createPortletType("RSS Feed",
				PortletType.WIDE_PROFILE, RSSFeedPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays an RSS Feed");
	}
*/
    /**
     *
     */
    private void loadTimeOfDayPortlet() {
		PortletType type = PortletType.createPortletType("Time of Day",
				PortletType.WIDE_PROFILE,
				TimeOfDayPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the current date and time");
    }

/*
    private void loadWorkspaceNavigatorPortlet() {
		PortletType type = PortletType.createPortletType(
				"Workspace Navigator Portlet", PortletType.WIDE_PROFILE,
				WorkspaceNavigatorPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays ??");
	}

    private void loadWorkspaceSummaryPortlet() {
		PortletType type = PortletType.createPortletType(
				"Workspace Summary Portlet", PortletType.WIDE_PROFILE,
				WorkspaceSummaryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays ???");
	}
*/
    
}

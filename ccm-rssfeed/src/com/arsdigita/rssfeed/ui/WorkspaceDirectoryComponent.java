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

package com.arsdigita.rssfeed.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.rssfeed.portlet.WorkspaceDirectoryChannel;
// import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.rssfeed.RSSChannel;
// import com.arsdigita.london.rss.ui.RSSComponent;
import com.arsdigita.web.Application;

/**
 * 
 * 
 */
public class WorkspaceDirectoryComponent extends RSSComponent {

    public static final String PORTAL_XML_NS = "http://www.uk.arsdigita.com/portal/1.0";

	/**
     * 
     */
    public WorkspaceDirectoryComponent() {
		super("portal:workspaceDirectory", PORTAL_XML_NS);
	}

	/**
     * 
     * @param pageState
     * @return 
     */
    protected RSSChannel getChannel(PageState pageState) {
		Application current = (Application) Kernel.getContext().getResource();

		WorkspaceDirectoryChannel channel = new WorkspaceDirectoryChannel(
				Kernel.getContext().getParty(), current.getPath()
						+ "/workspaces.jsp", null);
		return channel;
	}
}

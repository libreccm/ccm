/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.london.portal.portlet.WorkspaceDirectoryChannel;
import com.arsdigita.london.rss.RSSChannel;
import com.arsdigita.london.rss.ui.RSSComponent;
import com.arsdigita.web.Application;

public class WorkspaceDirectoryComponent extends RSSComponent {

	public WorkspaceDirectoryComponent() {
		super("portal:workspaceDirectory", PortalConstants.PORTAL_XML_NS);
	}

	protected RSSChannel getChannel(PageState pageState) {
		Application current = (Application) Kernel.getContext().getResource();

		WorkspaceDirectoryChannel channel = new WorkspaceDirectoryChannel(
				Kernel.getContext().getParty(), current.getPath()
						+ "/workspaces.jsp", null);
		return channel;
	}
}

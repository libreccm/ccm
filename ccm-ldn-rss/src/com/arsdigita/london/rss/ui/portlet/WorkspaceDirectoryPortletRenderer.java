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

package com.arsdigita.london.rss.ui.portlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.london.rss.portlet.WorkspaceDirectoryChannel;
import com.arsdigita.london.rss.portlet.WorkspaceDirectoryPortlet;
//  import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.london.rss.RSSRenderer;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

public class WorkspaceDirectoryPortletRenderer extends AbstractPortletRenderer {
	private WorkspaceDirectoryPortlet m_portlet;

	// XXX Quick HAck! Keep in Sync with portal application!
    private static final String PORTLET_XML_NS = "http://www.uk.arsdigita.com/portlet/1.0";

	public WorkspaceDirectoryPortletRenderer(WorkspaceDirectoryPortlet portlet) {
		m_portlet = portlet;
	}

	protected void generateBodyXML(PageState pageState, Element parentElement) {
		Element content = new Element("portlet:workspaceDirectory",
				PORTLET_XML_NS);

		Application current = Web.getContext().getApplication();

		WorkspaceDirectoryChannel channel = new WorkspaceDirectoryChannel(
				Kernel.getContext().getParty(), URL.there(current, "/", null)
						.toString(), null);

		content.addContent(RSSRenderer.generateXML(channel));
		parentElement.addContent(content);
	}
}

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

package com.arsdigita.london.portal.ui.portlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.london.portal.portlet.RSSFeedPortlet;
import com.arsdigita.london.portal.portlet.RSSFeedPortletHelper;
import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.xml.Element;

public class RSSFeedPortletRenderer extends AbstractPortletRenderer {
	private RSSFeedPortlet m_portlet;

	public RSSFeedPortletRenderer(RSSFeedPortlet portlet) {
		m_portlet = portlet;
	}

	public void generateBodyXML(PageState state, Element parent) {
		Element content = parent.newChildElement("portlet:RSSFeed",
				PortalConstants.PORTLET_XML_NS);

		String feed = m_portlet.getURL();

		if (feed != null) {
			Element data = RSSFeedPortletHelper.getRSSElement(feed);
			if (data != null) {
				content.addContent(data);
			} else {
				Element error = content.newChildElement("portlet:RSSFeedError",
						PortalConstants.PORTLET_XML_NS);
				error.setText("There was an error fetching this content feed");
			}
		}
	}
}

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

package com.arsdigita.portalworkspace.ui.portlet;

import java.util.Date;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.portalworkspace.portlet.TimeOfDayPortlet;
import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.xml.Element;

public class TimeOfDayPortletRenderer extends AbstractPortletRenderer {
	private TimeOfDayPortlet m_portlet;

	public TimeOfDayPortletRenderer(TimeOfDayPortlet portlet) {
		m_portlet = portlet;
	}

	public void generateBodyXML(PageState state, Element parent) {
		Element date = parent.newChildElement("portlet:timeOfDay",
				PortalConstants.PORTLET_XML_NS);
		date.addAttribute("date", (new Date()).toString());
	}
}

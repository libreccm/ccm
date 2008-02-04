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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

public class SimplePortlet extends AbstractPortletRenderer {

	private Component m_contents;

	public SimplePortlet(String title, int column, int row, Component contents) {
		Assert.exists(contents, Component.class);

		m_contents = contents;
		setTitle(title);
		setCellNumber(column);
		setSortKey(row);
	}

	protected void generateBodyXML(PageState state, Element parent) {
		Element content = parent.newChildElement("portlet:simple",
				PortalConstants.PORTLET_XML_NS);
		m_contents.generateXML(state, content);
	}
}

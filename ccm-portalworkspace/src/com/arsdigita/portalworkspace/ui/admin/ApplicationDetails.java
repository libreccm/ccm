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

package com.arsdigita.portalworkspace.ui.admin;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

public class ApplicationDetails extends SimpleContainer {

	private static final Logger s_log = Logger.getLogger(ApplicationList.class);

	private ApplicationSelectionModel m_app;

	public ApplicationDetails(ApplicationSelectionModel app) {
		super("portal:applicationDetails", WorkspacePage.PORTAL_XML_NS);

		m_app = app;
	}

    /**
     * 
     * @param state
     * @param parent 
     */
    @Override
	public void generateXML(PageState state, Element parent) {
		Element content = generateParent(parent);

		Application app = (Application) m_app.getSelectedObject(state);

		DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(content);
		xr.setWrapRoot(false);
		xr.setWrapAttributes(true);
		xr.setWrapObjects(false);

		xr.walk(app, ApplicationDetails.class.getName());
	}

}

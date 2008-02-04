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

package com.arsdigita.london.portal.ui.admin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.xml.Element;

public class ApplicationList extends SimpleContainer {

	private static final Logger s_log = Logger.getLogger(ApplicationList.class);

	private ApplicationSelectionModel m_app;

	private static final String SELECT = "select";

	public ApplicationList(ApplicationSelectionModel app) {
		super("portal:applicationList", PortalConstants.PORTAL_XML_NS);

		m_app = app;
	}

	public void respond(PageState state) {
		String key = state.getControlEventName();
		String value = state.getControlEventValue();

		if (SELECT.equals(key)) {
			OID oid = OID.valueOf(value);
			s_log.debug("OID " + oid);
			Application app = (Application) DomainObjectFactory
					.newInstance(oid);
			m_app.setSelectedObject(state, app);
		} else {
			s_log.warn("Unknown control event " + key + ":" + value);
		}
	}

	public void generateXML(PageState state, Element parent) {
		Element content = generateParent(parent);

		ApplicationCollection apps = Application.retrieveAllApplications();

		while (apps.next()) {
			Application app = (Application) apps.getDomainObject();

			Element appEl = content.newChildElement("portal:application",
					PortalConstants.PORTAL_XML_NS);
			try {
				state.setControlEvent(this, SELECT, app.getOID().toString());				
				appEl.addAttribute("appClass", app.getClass().getName());
				appEl.addAttribute("appType", app.getApplicationType().getTitle());
				appEl.addAttribute("viewURL", state.stateAsURL());
				state.clearControlEvent();
			} catch (IOException ex) {
				throw new UncheckedWrapperException("damn", ex);
			}

			DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(appEl);
			xr.setWrapRoot(false);
			xr.setWrapAttributes(true);
			xr.setWrapObjects(false);

			xr.walk(app, ApplicationList.class.getName());
		}
	}
}

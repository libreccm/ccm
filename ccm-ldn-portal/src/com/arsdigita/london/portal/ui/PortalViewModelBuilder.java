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

package com.arsdigita.london.portal.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.util.LockableImpl;

public class PortalViewModelBuilder extends LockableImpl implements
		PortalModelBuilder {

	private static final Logger s_log = Logger
			.getLogger(PortalViewModelBuilder.class);

	private PortalSelectionModel m_portal;

	public PortalViewModelBuilder(PortalSelectionModel portal) {
		m_portal = portal;
	}

	public PortalModel buildModel(PageState state) {
		WorkspacePage portal = m_portal.getSelectedPortal(state);

		ArrayList list = new ArrayList();
		PortletCollection portlets = portal.getPortlets();
		while (portlets.next()) {
			com.arsdigita.portal.Portlet portlet = portlets.getPortlet();
			list.add(portlet.getPortletRenderer());
		}

		return new PortalViewModel(list.iterator(), portal.getTitle());
	}

	private class PortalViewModel implements PortalModel {

		private Iterator m_portlets;

		private String m_title;

		public PortalViewModel(Iterator portlets, String title) {
			m_portlets = portlets;
			m_title = title;
		}

		public Iterator getPortletRenderers() {
			return m_portlets;
		}

		public String getTitle() {
			return m_title;
		}
	}
}

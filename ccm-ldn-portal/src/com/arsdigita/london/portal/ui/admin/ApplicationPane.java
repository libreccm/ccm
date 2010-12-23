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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;

public class ApplicationPane extends SimpleContainer {

    private Map m_edit; // Map of application type -> edit config form

    private Map m_create;

    private NewApplicationForm m_newApp;

    private ApplicationDetails m_appDetails;

    private ActionLink m_editLink;

    private ApplicationSelectionModel m_app;

    /**
     *
     * @param app
     */
    public ApplicationPane(ApplicationSelectionModel app) {
		super("portal:applicationPane", PortalConstants.PORTAL_XML_NS);

		m_app = app;

		m_create = new HashMap();
		m_edit = new HashMap();

		m_appDetails = new ApplicationDetails(app);
		add(m_appDetails);

		m_editLink = new ActionLink("edit");
		m_editLink.addActionListener(new ApplicationEditListener());
		add(m_editLink);

		m_newApp = new NewApplicationForm();
		m_newApp.addCompletionListener(new ApplicationCreateListener());
		add(m_newApp);

		final RequestLocal appRL = new RequestLocal() {
			public Object initialValue(PageState state) {
				return m_app.getSelectedObject(state);
			}
		};

		ApplicationTypeCollection types = ApplicationType
				.retrieveAllApplicationTypes();
		while (types.next()) {
			ApplicationType type = types.getApplicationType();

			ResourceConfigComponent create = type.getCreateComponent(appRL);
			create.addCompletionListener(new ApplicationCompleteCreateListener(
					create));
			m_create.put(type.getOID(), create);
			add(create);

			ResourceConfigComponent modify = type.getModifyComponent(appRL);
			modify.addCompletionListener(new ApplicationCompleteEditListener(
					modify));
			m_edit.put(type.getOID(), modify);
			add(modify);
		}

	}

	public void register(Page p) {
		super.register(p);

		Iterator c = m_create.keySet().iterator();
		while (c.hasNext()) {
			OID type = (OID) c.next();
			p.setVisibleDefault((Component) m_create.get(type), false);
		}

		Iterator e = m_edit.keySet().iterator();
		while (e.hasNext()) {
			OID type = (OID) e.next();
			p.setVisibleDefault((Component) m_edit.get(type), false);
		}
	}

	private class ApplicationCreateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			PageState state = e.getPageState();

			ApplicationType type = m_newApp.getApplicationType(state);

			Component editor = (Component) m_create.get(type.getOID());
			editor.setVisible(state, true);

			m_newApp.setVisible(state, false);
			m_appDetails.setVisible(state, false);
			m_editLink.setVisible(state, false);
		}
	}

	private class ApplicationEditListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			PageState state = e.getPageState();

			Application app = (Application) m_app.getSelectedObject(state);

			Component editor = (Component) m_edit.get(app.getResourceType()
					.getOID());
			editor.setVisible(state, true);

			m_newApp.setVisible(state, false);
			m_appDetails.setVisible(state, false);
			m_editLink.setVisible(state, false);
		}
	}

	private class ApplicationCompleteCreateListener implements ActionListener {
		private Component m_src;

		public ApplicationCompleteCreateListener(Component src) {
			m_src = src;
		}

		public void actionPerformed(ActionEvent e) {
			PageState state = e.getPageState();

			ResourceConfigComponent c = (ResourceConfigComponent) m_src;
			Resource newResource = c.createResource(state);
			c.setVisible(state, false);

			// Copy categorization from nav app instance
			Resource parentResource = newResource.getParentResource();
			Category.setRootForObject(newResource, Category
					.getRootForObject(parentResource));

			m_newApp.setVisible(state, true);
			m_appDetails.setVisible(state, true);
			m_editLink.setVisible(state, true);

			m_app.clearSelection(state);
		}
	}

	private class ApplicationCompleteEditListener implements ActionListener {
		private Component m_src;

		public ApplicationCompleteEditListener(Component src) {
			m_src = src;
		}

		public void actionPerformed(ActionEvent e) {
			PageState state = e.getPageState();

			ResourceConfigComponent c = (ResourceConfigComponent) m_src;
			c.modifyResource(state);
			c.setVisible(state, false);

			m_newApp.setVisible(state, true);
			m_appDetails.setVisible(state, true);
			m_editLink.setVisible(state, true);

			m_app.clearSelection(state);
		}
	}

}

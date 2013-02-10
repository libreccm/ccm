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

package com.arsdigita.portalworkspace.ui;

import java.util.Iterator;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.util.Assert;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/**
 * Purpose?.
 * 
 * As of version 6.6 not really used. References from UserWorkspace, 
 * DefaultWorkspace, and PackageWorkspace, none of those referenced anymore
 * (all are meant as JSP entry points, but not used from  any JSP.)
 * 
 */
public class WorkspaceComponent extends SimpleContainer {

	private WorkspaceSelectionModel m_workspace;

	private PortalSelectionModel m_portal;

	private PortalList m_portals_edit;

	private PortalList m_portals_view;

	private PersistentPortal m_display;

	private PersistentPortal m_editor;

	private ActionLink m_add;

	private ActionLink m_custom;

	private ActionLink m_browse;

	private ActionLink m_admin;

	private boolean m_multiPortal;

	public WorkspaceComponent(WorkspaceSelectionModel workspace) {
		setTag("portal:workspace");
		setNamespace(WorkspacePage.PORTAL_XML_NS);

		m_multiPortal = true;

		m_workspace = workspace;

		m_portal = new DefaultPortalSelectionModel(workspace,
				new BigDecimalParameter("portal"));

		m_portals_edit = new PortalListEditor(workspace, m_portal);
		m_portals_view = new PortalListViewer(workspace, m_portal);

		m_display = new PersistentPortal(m_portal, WorkspacePage.MODE_DISPLAY);
		m_editor = new PersistentPortal(m_portal, WorkspacePage.MODE_EDITOR);

		m_add = new ActionLink("add pane");
		m_add.setClassAttr("actionLink");
		m_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();

				Workspace workspace = getSelectedWorkspace(state);
				Party party = Kernel.getContext().getParty();
				if (!PortalHelper.canCustomize(party, workspace)) {
					throw new AccessDeniedException(
							"no permissions to customize workspace");
				}

				WorkspacePage page = workspace.addPage("New page",
						"New portal page");
			}
		});

		m_custom = new ActionLink("customise");
		m_custom.setClassAttr("actionLink");
		m_custom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDisplayMode(e.getPageState(), false);
			}
		});

		m_browse = new ActionLink("browse");
		m_browse.setClassAttr("actionLink");
		m_browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDisplayMode(e.getPageState(), true);
			}
		});

		m_admin = new ActionLink("admin");
		m_admin.setClassAttr("actionLink");
		m_admin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				throw new RedirectSignal(URL.there(e.getPageState()
						.getRequest(), getSelectedWorkspace(e.getPageState()),
						"/admin/index.jsp"), false);
			}
		});

		BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);
		panel.add(m_portals_edit);
		panel.add(m_portals_view);

		addCustomizeLinks(panel, m_add);
		addCustomizeLinks(panel, m_custom);
		addCustomizeLinks(panel, m_browse);
		addCustomizeLinks(panel, m_admin);

		add(panel);
		add(m_editor);
		add(m_display);
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

	protected void addCustomizeLinks(Container parent, Component child) {
		parent.add(child);
	}

	public void addCustomizeListener(ActionListener l) {
		m_custom.addActionListener(l);
	}

	private void setDisplayMode(PageState state, boolean browse) {
		// Portal display
		m_editor.setVisible(state, !browse);
		m_display.setVisible(state, browse);

		// Buttons
		m_browse.setVisible(state, !browse);
		m_custom.setVisible(state, browse);

		// Portal lists
		if (m_multiPortal) {
			m_portals_edit.setVisible(state, !browse);
			m_portals_view.setVisible(state, browse);

			m_add.setVisible(state, !browse);
		}
	}

	public void setMultiPortal(boolean val) {
		Assert.isUnlocked(this);
		m_multiPortal = val;
	}

	public void register(Page page) {
		super.register(page);

		page.addComponentStateParam(this, m_portal.getStateParameter());

		page.setVisibleDefault(m_editor, false);
		page.setVisibleDefault(m_display, true);

		page.setVisibleDefault(m_custom, true);
		page.setVisibleDefault(m_browse, false);

		page.setVisibleDefault(m_portals_view, (m_multiPortal ? true : false));
		page.setVisibleDefault(m_portals_edit, false);

		page.setVisibleDefault(m_add, false);
	}

	public void generateXML(PageState state, Element parent) {
		if (isVisible(state)) {
			Workspace workspace = m_workspace.getSelectedWorkspace(state);
			PermissionDescriptor read = new PermissionDescriptor(
					PrivilegeDescriptor.READ, workspace, Kernel.getContext()
							.getParty());

			if (!PermissionService.checkPermission(read)) {
				throw new AccessDeniedException(
						"No permission to view workspace");
			}

			Object key = m_portal.getSelectedKey(state);
			// m_display.setVisible(state, key != null);

			Element content = generateParent(parent);
			content.addAttribute("title", workspace.getTitle());
			content.addAttribute("description", workspace.getDescription());

			for (Iterator i = children(); i.hasNext();) {
				Component c = (Component) i.next();
				if (c.isVisible(state)) {
					c.generateXML(state, content);
				}
			}
		}
	}

	public WorkspaceSelectionModel getWorkspaceModel() {
		return m_workspace;
	}

	public PortalSelectionModel getPortalModel() {
		return m_portal;
	}
}

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

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.portalworkspace.Workspace;

public abstract class AbstractWorkspaceComponent extends SimpleContainer {

	private WorkspaceSelectionModel m_workspace;

	private DefaultPortalSelectionModel m_portal;

	private WorkspaceDetails m_details;

	private PortalList m_portalList;

	private PersistentPortal m_portalDisplay;

	public AbstractWorkspaceComponent() {
		this(null);
	}

	public AbstractWorkspaceComponent(WorkspaceSelectionModel workspace) {
		super("portal:workspace", PortalConstants.PORTAL_XML_NS);

		m_workspace = workspace;

		m_details = new WorkspaceDetails(m_workspace);

		m_portal = new DefaultPortalSelectionModel(new BigDecimalParameter(
				"portal"));

		m_portalList = createPortalList(m_portal);

		m_portalDisplay = createPortalDisplay(m_portal);

		add(m_details);
		add(m_portalList);
		add(m_portalDisplay);
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
		m_details.setWorkspaceModel(workspace);
		m_portalList.setWorkspaceModel(workspace);
		m_portal.setWorkspaceModel(workspace);
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

	protected abstract PortalList createPortalList(PortalSelectionModel portal);

	protected abstract PersistentPortal createPortalDisplay(
			PortalSelectionModel portal);

	public void register(Page page) {
		super.register(page);

		page.addComponentStateParam(this, m_portal.getStateParameter());
	}

}

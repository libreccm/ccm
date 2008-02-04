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

import java.math.BigDecimal;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.london.portal.WorkspacePageCollection;

public class DefaultPortalSelectionModel extends ACSObjectSelectionModel
		implements PortalSelectionModel {

	private WorkspaceSelectionModel m_workspace;

	public DefaultPortalSelectionModel(BigDecimalParameter param) {
		this(null, param);
	}

	public DefaultPortalSelectionModel(WorkspaceSelectionModel workspace,
			BigDecimalParameter param) {
		super(WorkspacePage.class.getName(),
				WorkspacePage.BASE_DATA_OBJECT_TYPE, param);

		m_workspace = workspace;
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

	public Object getSelectedKey(PageState state) {
		BigDecimal key = (BigDecimal) super.getSelectedKey(state);
		if (key == null) {
			loadDefaultPortal(state);
		}

		return super.getSelectedKey(state);
	}

	public DomainObject getSelectedObject(PageState state) {
		BigDecimal key = (BigDecimal) super.getSelectedKey(state);
		if (key == null) {
			loadDefaultPortal(state);
		}

		return super.getSelectedObject(state);
	}

	public WorkspacePage getSelectedPortal(PageState state) {
		return (WorkspacePage) getSelectedObject(state);
	}

	private void loadDefaultPortal(PageState state) {
		Workspace workspace = getSelectedWorkspace(state);
		WorkspacePageCollection portals = workspace.getPages();
		portals.addOrder(WorkspacePage.SORT_KEY);
		if (portals.next()) {
			WorkspacePage portal = portals.getPage();
			setSelectedObject(state, portal);
			portals.close();
		} else {
			WorkspacePage portal = workspace.addPage("Main", "Main page");
			setSelectedObject(state, portal);
		}
	}
}

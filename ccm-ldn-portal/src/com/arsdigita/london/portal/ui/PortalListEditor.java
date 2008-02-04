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

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.persistence.OID;

public class PortalListEditor extends PortalListViewer {
	private PortalEditForm m_edit;

	private LayoutForm m_layout;

	private static final Logger s_log = Logger
			.getLogger(PortalListEditor.class);

	public PortalListEditor(PortalSelectionModel portal) {
		this(null, portal);
	}

	public PortalListEditor(WorkspaceSelectionModel workspace,
			PortalSelectionModel portal) {
		super(workspace, portal);

		m_edit = new PortalEditForm(workspace, portal);
		m_edit.setRedirecting(true);
		add(m_edit);

		m_layout = new LayoutForm(workspace, portal);
		m_layout.setRedirecting(true);
		add(m_layout);

		addPortalAction("moveLeft", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();
				String value = state.getControlEventValue();

				assertWorkspaceAdmin(state);

				if (s_log.isDebugEnabled()) {
					s_log.debug("Moving portal left " + value);
				}

				WorkspacePage portal = (WorkspacePage) DomainObjectFactory
						.newInstance(new OID(
								WorkspacePage.BASE_DATA_OBJECT_TYPE,
								new BigDecimal(value)));
				Workspace workspace = getSelectedWorkspace(state);
				workspace.movePageLeft(portal);
			}
		});
		addPortalAction("moveRight", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();
				String value = state.getControlEventValue();

				assertWorkspaceAdmin(state);

				if (s_log.isDebugEnabled()) {
					s_log.debug("Moving portal right " + value);
				}

				WorkspacePage portal = (WorkspacePage) DomainObjectFactory
						.newInstance(new OID(
								WorkspacePage.BASE_DATA_OBJECT_TYPE,
								new BigDecimal(value)));
				Workspace workspace = getSelectedWorkspace(state);
				workspace.movePageRight(portal);
			}
		});
		addPortalAction("delete", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();
				String value = state.getControlEventValue();

				assertWorkspaceAdmin(state);

				if (s_log.isDebugEnabled()) {
					s_log.debug("Deleting portal " + value);
				}

				WorkspacePage portal = (WorkspacePage) DomainObjectFactory
						.newInstance(new OID(
								WorkspacePage.BASE_DATA_OBJECT_TYPE,
								new BigDecimal(value)));

				if (portal.equals(getSelectedPortal(state))) {
					clearSelectedPortal(state);
				}

				Workspace workspace = getSelectedWorkspace(state);
				workspace.removePage(portal);
			}
		});
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		super.setWorkspaceModel(workspace);
		m_edit.setWorkspaceModel(workspace);
		m_layout.setWorkspaceModel(workspace);
	}

	private void assertWorkspaceAdmin(PageState state) {
		Workspace workspace = getSelectedWorkspace(state);
		Party party = Kernel.getContext().getParty();
		if (!PortalHelper.canCustomize(party, workspace)) {
			throw new AccessDeniedException(
					"no permissions to customize workspace");
		}
	}
}

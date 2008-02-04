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

package com.arsdigita.london.portal.ui.admin;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.ui.permissions.PermissionsPane;
import com.arsdigita.web.Application;

/**
 * Wrapper class to have com.arsdigita.ui.permissions.PermissionsPane play nice
 * with Workspaces.
 */

public class WorkspacePermissionsPane extends PermissionsPane {

	private static final Logger s_log = Logger
			.getLogger(WorkspacePermissionsPane.class);

	private ApplicationSelectionModel m_model;

	/**
	 * UI component to manipulate existing privileges and to grant new ones
	 */

	private static final PrivilegeDescriptor[] m_privs = {
			PrivilegeDescriptor.READ, PrivilegeDescriptor.ADMIN };

	/*
	 * XXX@param model must have PermissionsPane.getObjectIDParam() as the
	 * parameter
	 */

	public WorkspacePermissionsPane() {
		super(m_privs);
		setClassAttr(WorkspacePermissionsPane.class.getName());

		m_model = new ApplicationSelectionModel(OBJECT_ID);
	}

	public void actionPerformed(ActionEvent e) {
		Application app = (Application) Kernel.getContext().getResource();
		m_model.setSelectedApplication(e.getPageState(), app);

		e.getPageState().setValue(m_model.getStateParameter(), app.getID());

		super.actionPerformed(e);
	}

	public ACSObjectSelectionModel getSelectionModel() {
		return m_model;
	}

	public void register(Page page) {
		super.register(page);
		// we don't need the header so we hide it
		page.setVisibleDefault(getPermissionsHeader(), false);
	}
}

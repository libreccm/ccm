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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.util.Assert;

/**
 * PortalEditForm.
 * <p>
 * <strong>Experimental</strong>
 * </p>
 * 
 * @version $Id: PortalEditForm.java 1174 2006-06-14 14:14:15Z fabrice $
 */
public class PortalEditForm extends Form implements FormProcessListener,
		FormInitListener {

	private WorkspaceSelectionModel m_workspace;

	private PortalSelectionModel m_portal;

	private TextField m_title;

	private Submit m_save;

	public PortalEditForm(PortalSelectionModel portal) {
		this(null, portal);
	}

	public PortalEditForm(WorkspaceSelectionModel workspace,
			PortalSelectionModel portal) {
		super("editPortal", new SimpleContainer("portal:editPortal",
				PortalConstants.PORTAL_XML_NS));

		m_workspace = workspace;
		m_portal = portal;

		m_title = new TextField(new StringParameter("title"));
		m_title.addValidationListener(new NotNullValidationListener());
		m_title
				.addValidationListener(new StringInRangeValidationListener(1,
						40));

		m_save = new Submit("Save");

		add(m_title);
		add(m_save);

		addProcessListener(this);
		addInitListener(this);
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

	public void init(FormSectionEvent e) {
		PageState state = e.getPageState();

		WorkspacePage portal = (WorkspacePage) m_portal
				.getSelectedPortal(state);

		Assert.exists(portal, WorkspacePage.class);

		m_title.setValue(state, portal.getTitle());
	}

	public void process(FormSectionEvent e) {
		PageState state = e.getPageState();

		Workspace workspace = getSelectedWorkspace(state);
		Party party = Kernel.getContext().getParty();
		if (!PortalHelper.canCustomize(party, workspace)) {
			throw new AccessDeniedException(
					"no permissions to customize workspace");
		}

		WorkspacePage portal = (WorkspacePage) m_portal
				.getSelectedPortal(state);

		Assert.exists(portal, WorkspacePage.class);

		String title = (String) m_title.getValue(state);
		portal.setTitle(title);
	}
}

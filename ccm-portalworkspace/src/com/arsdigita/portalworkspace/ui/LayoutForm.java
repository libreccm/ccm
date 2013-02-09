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

import java.util.TooManyListenersException;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.PageLayout;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * <p>
 * <strong>Experimental</strong>
 * </p>
 *
 * @version $Id: LayoutForm.java 1174 2006-06-14 14:14:15Z fabrice $
 */
public class LayoutForm extends Form implements FormProcessListener,
		FormInitListener {

	private WorkspaceSelectionModel m_workspace;

	private PortalSelectionModel m_portal;

	private SingleSelect m_layout;

	private Submit m_save;

	public LayoutForm(PortalSelectionModel portal) {
		this(null, portal);
	}

	public LayoutForm(WorkspaceSelectionModel workspace,
			PortalSelectionModel portal) {
		super("editLayout", new SimpleContainer("portal:editLayout",
				WorkspacePage.PORTAL_XML_NS));

		m_workspace = workspace;
		m_portal = portal;

		m_layout = new SingleSelect(new OIDParameter("layout"));
		m_layout.addValidationListener(new NotNullValidationListener());
		try {
			m_layout.addPrintListener(new PrintListener() {
				public void prepare(PrintEvent ev) {
					SingleSelect target = (SingleSelect) ev.getTarget();
					DomainCollection layouts = PageLayout.retrieveAll();
					layouts.addOrder(PageLayout.TITLE);
					while (layouts.next()) {
						PageLayout layout = (PageLayout) layouts
								.getDomainObject();
						target.addOption(new Option(layout.getOID().toString(),
								layout.getTitle()));
					}
				}
			});
		} catch (TooManyListenersException ex) {
			throw new UncheckedWrapperException("this cannot happen", ex);
		}

		m_save = new Submit("Save");

		add(m_layout);
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

		m_layout.setValue(state, portal.getLayout().getOID());
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

		OID layoutOID = (OID) m_layout.getValue(state);
		PageLayout layout = (PageLayout) DomainObjectFactory
				.newInstance(layoutOID);
		portal.setLayout(layout);
	}
}

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.london.portal.WorkspacePageCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 * 
 * 
 */
public abstract class PortalList extends SimpleContainer {

	private WorkspaceSelectionModel m_workspace;

	private PortalSelectionModel m_portal;

	private Map m_actions = new HashMap();

	/**
     * Constructor.
     * 
     * @param portal
     */
    public PortalList(PortalSelectionModel portal) {
		this(null, portal);
	}

	/**
     * Constructor
     * @param workspace
     * @param portal
     */
    public PortalList(WorkspaceSelectionModel workspace,
                      PortalSelectionModel portal) {
		super("portal:portalList", PortalConstants.PORTAL_XML_NS);

		m_workspace = workspace;
		m_portal = portal;
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	protected void addPortalAction(String name, ActionListener listener) {
		m_actions.put(name, listener);
	}

	public void respond(PageState state) {
		String name = state.getControlEventName();

		ActionListener listener = (ActionListener) m_actions.get(name);
		if (listener != null) {
			listener.actionPerformed(new ActionEvent(this, state));
		}
	}

	public void setSelectedPortal(PageState state, WorkspacePage portal) {
		m_portal.setSelectedObject(state, portal);
	}

	public WorkspacePage getSelectedPortal(PageState state) {
		return (WorkspacePage) m_portal.getSelectedPortal(state);
	}

	public void clearSelectedPortal(PageState state) {
		m_portal.clearSelection(state);
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

    /**
     *
     * @param state
     * @param parent
     */
    @Override
	public void generateXML(PageState state, Element parent) {
		if (!isVisible(state)) {
			return;
		}

		Element content = generateParent(parent);

		generateChildrenXML(state, content);

		generatePortalListXML(state, content);
	}

	/**
     * 
     * @param state
     * @param parent
     */
    protected void generatePortalListXML(PageState state, Element parent) {
		WorkspacePage current = m_portal.getSelectedPortal(state);

		Workspace workspace = getSelectedWorkspace(state);
		WorkspacePageCollection pages = workspace.getPages();
		pages.addOrder(WorkspacePage.SORT_KEY);
		while (pages.next()) {
			Element pageEl = new Element("portal:portalDetails",
					PortalConstants.PORTAL_XML_NS);

			WorkspacePage page = pages.getPage();

			generatePageXML(state, pageEl, page);

			pageEl.addAttribute("isSelected",
					XML.format(page.equals(current) ? Boolean.TRUE
							: Boolean.FALSE));

			parent.addContent(pageEl);
		}
	}

	protected void generatePageXML(PageState state, Element parent,
			WorkspacePage page) {
		DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(parent);
		xr.setWrapRoot(false);
		xr.setWrapAttributes(true);
		xr.setWrapObjects(false);

		xr.walk(page, PortalList.class.getName());

		Iterator actions = m_actions.keySet().iterator();
		while (actions.hasNext()) {
			String name = (String) actions.next();
			state.setControlEvent(this, name, page.getID().toString());
			try {
				parent.addAttribute(name + "Action", state.stateAsURL());
			} catch (IOException ex) {
				throw new UncheckedWrapperException("cannot get state url", ex);
			}
			state.clearControlEvent();
		}
	}
}

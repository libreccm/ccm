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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.WorkspacePageCollection;
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
        super("portal:portalList", WorkspacePage.PORTAL_XML_NS);

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
                                         WorkspacePage.PORTAL_XML_NS);
            WorkspacePage page = pages.getPage();
            generatePageXML(state, pageEl, page);

            pageEl.addAttribute("isSelected",
                                XML.format(page.equals(current) ? Boolean.TRUE
                                                                : Boolean.FALSE));

            parent.addContent(pageEl);
        }

    }

    /**
     * 
     * @param state
     * @param parent
     * @param page
     */
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

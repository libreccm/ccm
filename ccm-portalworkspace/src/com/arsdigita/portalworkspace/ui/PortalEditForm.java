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


    /**
     *
     * @param portal
     */
    public PortalEditForm(PortalSelectionModel portal) {
        this(null, portal);
    }


    /** 
     * 
     * @param workspace
     * @param portal
     */
    public PortalEditForm(WorkspaceSelectionModel workspace,
                          PortalSelectionModel portal) {

        super("editPortal", new SimpleContainer("portal:editPortal",
                                                WorkspacePage.PORTAL_XML_NS));

        m_workspace = workspace;
        m_portal = portal;

        m_title = new TextField(new StringParameter("title"));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new StringInRangeValidationListener(1,40));

        m_save = new Submit("Save");

        add(m_title);
        add(m_save);

        addProcessListener(this);
        addInitListener(this);
    }

    /**
     *
     * @param workspace
     */
    public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
        m_workspace = workspace;
    }

    /**
     * 
     * @param state
     * @return
     */
    public Workspace getSelectedWorkspace(PageState state) {
        return m_workspace.getSelectedWorkspace(state);
    }

    /**
     * 
     * @param e
     */
    public void init(FormSectionEvent e) {

        PageState state = e.getPageState();
        WorkspacePage portal = (WorkspacePage) m_portal
                .getSelectedPortal(state);


        Assert.exists(portal, WorkspacePage.class);
        m_title.setValue(state, portal.getTitle());

    }

    /**
     * 
     * @param e
     */
    public void process(FormSectionEvent e) {
        PageState state = e.getPageState();

        Workspace workspace = getSelectedWorkspace(state);
        Party party = Kernel.getContext().getParty();
        if (!PortalHelper.canCustomize(party, workspace)) {
                              throw new AccessDeniedException(
                                  "no permissions to customize workspace");
		}

        WorkspacePage portal = (WorkspacePage) m_portal.getSelectedPortal(state);

        Assert.exists(portal, WorkspacePage.class);

        String title = (String) m_title.getValue(state);
        portal.setTitle(title);

    }

}

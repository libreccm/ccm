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

package com.arsdigita.aplaws.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.portalworkspace.ui.PersistentPortal;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;
import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;


public class HomepageWorkspace extends SimpleContainer {
    
    private HomepagePortalSelectionModel m_model;
    private ActionLink m_reset;
    private ActionLink m_browse;
    private ActionLink m_edit;
    private PersistentPortal m_browser;
    private PersistentPortal m_editor;
    private boolean m_customizable;
    private boolean m_readOnly;
    private String m_name;
    
    public HomepageWorkspace() {
        super("portal:homepageWorkspace", PortalConstants.PORTAL_XML_NS);
        m_customizable = false;
    }
    
    public void setModel(HomepagePortalSelectionModel model) {
        m_model = model;
    }
    
    public void setCustomizable(boolean customizable) {
        m_customizable = customizable;
    }
    
    public void setReadOnly(boolean readOnly) {
        m_readOnly = readOnly;
    }

    public void setName(String name) {
        m_name = name;
    }
    
    public void addWidgets() {
        m_edit = new ActionLink("customize");
        m_browse = new ActionLink("browse");
        m_reset = new ActionLink("reset");
        m_reset.setConfirmation("Are you sure you wish to reset this column? " +
                                "This will permanently remove all portlets.");

        m_browser = new PersistentPortal(m_model,
                                         m_name,
                                         PortalConstants.MODE_DISPLAY);
        m_editor = new PersistentPortal(m_model,
                                        m_name,
                                        PortalConstants.MODE_EDITOR);
        
        m_edit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    setDisplayMode(state, false);
                    
                    m_model.onCustomize(state);
                }
            });
        m_browse.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    setDisplayMode(state, true);
                }
            });
        m_reset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    setDisplayMode(state, true);
                    
                    m_model.onReset(state);
                }
            });
        
        add(m_edit);
        add(m_browse);
        add(m_reset);
        add(m_editor);
        add(m_browser);
    }

    public void setDisplayMode(PageState state,
                               boolean browse) {
        if (m_readOnly) {
            return;
        }
        m_browse.setVisible(state, !browse);
        m_reset.setVisible(state, !browse);
        m_edit.setVisible(state, browse);
        m_browser.setVisible(state, browse);
        m_editor.setVisible(state, !browse);
    }

    public void register(Page page) {
        super.register(page);
        
        page.setVisibleDefault(m_browse, false);
        page.setVisibleDefault(m_reset, false);
        page.setVisibleDefault(m_edit, !m_readOnly);
        page.setVisibleDefault(m_browser, true);
        page.setVisibleDefault(m_editor, false);
    }

    public void generateXML(PageState state,
                            Element parent) {
        Party party = Kernel.getContext().getParty();
        Workspace global = m_model.getWorkspaceModel().getGlobalWorkspace(state);
        PermissionDescriptor admin = 
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     global,
                                     party);
        boolean hasAdmin = PermissionService.checkPermission(admin);
        boolean userWorkspaces = Workspace.getConfig().getCreateUserWorkspaces();
        
        if (party == null || m_readOnly ||
            (!hasAdmin && !m_customizable) || (!hasAdmin && !userWorkspaces)) {
            m_reset.setVisible(state, false);
            m_browse.setVisible(state, false);
            m_edit.setVisible(state, false);
        }
        
        super.generateXML(state, parent);
    }

}

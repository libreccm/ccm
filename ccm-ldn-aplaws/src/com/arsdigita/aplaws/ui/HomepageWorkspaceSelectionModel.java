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


import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;


import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.london.portal.WorkspacePageCollection;

import com.arsdigita.london.subsite.Subsite;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.domain.DataObjectNotFoundException;

import org.apache.log4j.Logger;


/** 
 * 
 * 
 */
public class HomepageWorkspaceSelectionModel {
    private RequestLocal m_loaded = new RequestLocal();
    private RequestLocal m_global = new RequestLocal();
    private RequestLocal m_personal = new RequestLocal();
    private RequestLocal m_left = new RequestLocal();
    private RequestLocal m_middle = new RequestLocal();
    private RequestLocal m_right = new RequestLocal();
    private RequestLocal m_custom = new RequestLocal();

    private static Logger s_log = Logger.getLogger
                          (HomepageWorkspaceSelectionModel.class.getName());

    public WorkspacePage getPortal(PageState state,
                                   int column) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadWorkspacePages(state);
        }

        if (column == 0) { // Always global portal
            return (WorkspacePage)m_left.get(state);
        } else if (column == 1) { // Always global portal
            return (WorkspacePage)m_middle.get(state);
        } else if (column == 2) { // Personal portal, fallback on global
            Party party = (Party)Kernel.getContext().getParty();
            WorkspacePage right = (WorkspacePage)m_right.get(state);
            PermissionDescriptor admin = 
                new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                         right,
                                         party);
            // Always ensure admin gets global portal
	    // don't use custom portals if asked not to
            if (m_custom.get(state) == null ||
                PermissionService.checkPermission(admin) ||
		!Workspace.getConfig().getCreateUserWorkspaces()) {
                s_log.debug("Returning global portal");
                return (WorkspacePage)m_right.get(state);
            } else {
                s_log.debug("Returning personal portal");
                return (WorkspacePage)m_custom.get(state);
            }
        }

        throw new UncheckedWrapperException("column out of range (0..2)");
    }

    public void onCustomize(PageState state,
                            int column) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadWorkspacePages(state);
        }

        Party party = (Party)Kernel.getContext().getParty();
        
        Assert.exists(party, Party.class);

        // When customizing right column, may need to clone
        // for a personal portal
	// don't use custom portals if asked not to
        if (column == 2 &&
	    Workspace.getConfig().getCreateUserWorkspaces()) {
            Workspace global = getTopWorkspace();

            WorkspacePage right = (WorkspacePage)m_right.get(state);
            PermissionDescriptor admin = 
                new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                         right,
                                         party);
            if (PermissionService.checkPermission(admin)) {
                s_log.debug("Party has admin on global portal");
                return;
            }
            
            if (m_custom.get(state) != null) {
                s_log.debug("Party has custom portal already");
                return;
            }
            
            s_log.debug("Looking for custom column 2");

            Workspace custom = null;
            try {
                custom = global.retrieveSubworkspaceForParty(party);
                s_log.debug("Found exsting personal workspce");
            } catch (DataObjectNotFoundException ex) {
                s_log.debug("Created new personal workspace");
                custom = Workspace.createWorkspace(
                    "personal-" + party.getID(),
                    "Personal Workspace for " + party.getDisplayName(),
                    global,
                    (User)party
                );
            }
            s_log.debug("Looking for portal");
            m_personal.set(state, custom);
            WorkspacePageCollection portals = custom.getPages();
            portals.addOrder(WorkspacePage.SORT_KEY);
            WorkspacePage portal;
            if (portals.next()) {
                s_log.debug("Found portal");
                portal = portals.getPage();
                portals.close();
            } else {
                s_log.debug("Create portal");
                portal = custom.addPage("Custom", "Custom page");
            }
            m_custom.set(state, portal);
        }
    }


    public void onReset(PageState state,
                        int column) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadWorkspacePages(state);
        }

        Party party = Kernel.getContext().getParty();

        Assert.exists(party, Party.class);
        
        WorkspacePage clear = null;

	// don't use custom portals if asked not to
        if (column == 2 &&
	    Workspace.getConfig().getCreateUserWorkspaces()) {
            // If we find a custom workspace, then delete it, 
            // otherwise just clear the portlets.
            Workspace global = getTopWorkspace();
            Workspace custom = null;
            try {
                custom = global.retrieveSubworkspaceForParty(party);
                Assert.isTrue(custom.getParty() != null &&
                             custom.getParty().equals(party),
                             "party is not null and not admin");
                s_log.debug("Found exsting personal workspce");
                custom.delete();
                m_custom.set(state, null);
            } catch (DataObjectNotFoundException ex) {
                clear = (WorkspacePage)m_right.get(state);
            }
        } else if (column == 1) {
            clear = (WorkspacePage)m_middle.get(state);
        } else if (column == 0) {
            clear = (WorkspacePage)m_left.get(state);
        }
        
        if (clear != null) {
            clear.clearPortlets();
        }
    }
    

    public Workspace getGlobalWorkspace(PageState state) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadWorkspacePages(state);
        }

        return (Workspace)m_global.get(state);
    }
    
    public Workspace getPersonalWorkspace(PageState state) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadWorkspacePages(state);
        }

        return (Workspace)m_personal.get(state);
    }

    private void loadWorkspacePages(PageState state) {
        Workspace global = getTopWorkspace();
        m_global.set(state, global);

        WorkspacePage left = null;
        WorkspacePage middle = null;
        WorkspacePage right = null;

        WorkspacePageCollection portals = global.getPages();
        portals.addOrder(WorkspacePage.SORT_KEY);
        while (portals.next()) {
            WorkspacePage portal = portals.getPage();
            
            if (portal.getSortKey() == 0) {
                left = portal;
            } else if (portal.getSortKey() == 1) {
                middle = portal;
            } else if (portal.getSortKey() == 2) {
                right = portal;
            }
        }
        
        if (left == null) {
            left = global.addPage("Left", "Left hand page");
        }
        if (middle == null) {
            middle = global.addPage("Middle", "Middle page");
        }
        if (right == null) {
            right = global.addPage("Right", "Right hand page");
        }
        m_left.set(state, left);
        m_middle.set(state, middle);
        m_right.set(state, right);
        
        
        Party party = (Party)Kernel.getContext().getParty();
        m_custom.set(state, null);
        if (party != null) {
            Workspace custom = null;
            try {
                custom = global.retrieveSubworkspaceForParty(party);
            } catch (DataObjectNotFoundException ex) {
                // nada
            }
            if (custom != null) {
                portals = custom.getPages();
                portals.addOrder(WorkspacePage.SORT_KEY);
                if (portals.next()) {
                    WorkspacePage portal = portals.getPage();
                    m_custom.set(state, portal);
                }
                portals.close();
            }
            s_log.debug("Is there a custom portal ?" + custom + 
                        " - " + m_custom.get(state));
            m_personal.set(state, custom);
        }

        m_loaded.set(state, Boolean.TRUE);
    }

    protected Workspace getTopWorkspace() {
        if (Subsite.getContext().hasSite()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Return a subsite front page");
            }
            return (Workspace)Subsite.getContext().getSite().getFrontPage();
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Return the main front page");
            }
            return (Workspace)Kernel.getContext().getResource();
        }
    }
}

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

package com.arsdigita.portalworkspace.ui.jsp;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.ui.PortalHelper;
import com.arsdigita.portalworkspace.ui.WorkspaceComponent;
import com.arsdigita.toolbox.ui.SecurityContainer;
import com.arsdigita.web.LoginSignal;


/**
 * Another specific entry point into a portal workspace page, here based upon
 * WorkspaceComponent (as an alternative to AbstractWorkspaceComponent.
 */
// As of APLAWS 1.0.4 / CCM 6.6.x this class may never have been used and is
// propably unfinished work or undinished port from ccm-portalserver module.
// As with WorkspaceViewer it should be invoked  by a jsp. It is not directly
// used by any java code.
public class PackageWorkspace extends WorkspaceComponent {

    private RequestLocal m_isAdmin = new RequestLocal();
    
    /**
     * 
     */
    public PackageWorkspace() {
        super(new PackageWorkspaceSelectionModel());
        addCustomizeListener(new CustomizeListener());
    }

    /**
     * 
     * @param parent
     * @param child
     */
    protected void addCustomizeLinks(Container parent, Component child) {
        Container secure = new WorkspaceSecurityContainer(child);
        super.addCustomizeLinks(parent, secure);
    }


    /**
     *
     */
    private class CustomizeListener implements ActionListener {

        /**
         * 
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            Workspace workspace = getWorkspaceModel()
                                     .getSelectedWorkspace(state);

            Party party = Kernel.getContext().getParty();
            if (party == null) {
                throw new LoginSignal(state.getRequest());
            }

            if (!PortalHelper.canCustomize(party, workspace)) {
                throw new AccessDeniedException(
                        "no permission to customize workspace");
            }
        }

    }

    /**
     *
     */
    private class WorkspaceSecurityContainer extends SecurityContainer {

        /**
         *
         * @param c
         */
        public WorkspaceSecurityContainer(Component c) {
            super(c);
        }

        /**
         * 
         * @param party
         * @param state
         * @return
         */
        protected boolean canAccess(Party party, PageState state) {
            Boolean isAdmin = (Boolean) m_isAdmin.get(state);
            if (isAdmin == null) {
                Workspace workspace = getWorkspaceModel()
                                         .getSelectedWorkspace(state);
                boolean canAccess = PortalHelper.canCustomize(party, workspace);
                isAdmin = new Boolean(canAccess);
                m_isAdmin.set(state, isAdmin);
            }
            return isAdmin.booleanValue();
        }
    }

}

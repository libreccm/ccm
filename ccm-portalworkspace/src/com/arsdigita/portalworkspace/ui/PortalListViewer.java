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

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.persistence.OID;

/**
 * 
 * 
 */
public class PortalListViewer extends PortalList {

    /** Logger instance to support debugging                                  */
    private static final Logger s_log = Logger
                                        .getLogger(PortalListViewer.class);

    /**
     *
     * @param portal
     */
    public PortalListViewer(PortalSelectionModel portal) {
        this(null, portal);
    }

    public PortalListViewer(WorkspaceSelectionAbstractModel workspace,
                            PortalSelectionModel portal) {

        super(workspace, portal);

        addPortalAction("select",
                        new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                PageState state = e.getPageState();
                                String value = state.getControlEventValue();
                                if (s_log.isDebugEnabled()) {
                                    s_log.debug("Selecting portal " + value);
                                }
                                WorkspacePage portal = (WorkspacePage)
                                    DomainObjectFactory
                                    .newInstance(new OID(
                                         WorkspacePage.BASE_DATA_OBJECT_TYPE,
                                         new BigDecimal(value)));
                                setSelectedPortal(state, portal);
                            }
        });
    }

}

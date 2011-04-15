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
 *
 */
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.web.URL;

/**
 * 
 * @author dennis
 * @version "$Id: PortalSummaryPortlet.java  pboy $ 
 */
public class PortalSummaryPortlet extends AppPortlet {

    public static final String BASE_DATA_OBJECT_TYPE =
                             "com.arsdigita.workspace.WorkspaceSummaryPortlet";

    private static final int MAX_PARTICIPANTS_FOR_LISTING = 15;

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // Retrieve.
    public PortalSummaryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new PortalSummaryPortletRenderer(this);
    }

    @Override
    public String getZoomURL() {
        return URL.getDispatcherPath() + getParentApplication()
                                         .getPrimaryURL() + "participants/";
    }

    /**
     * 
     */
    private static class PortalSummaryPortletRenderer
        extends AbstractPortletRenderer {
        private PortalSummaryPortlet m_portlet;

        public PortalSummaryPortletRenderer( PortalSummaryPortlet portlet) {
            m_portlet = portlet;
        }

        protected void generateBodyXML(PageState ps, Element parent) {
            PortalSite currentPortal =
                (PortalSite)m_portlet.getParentApplication();

            if (currentPortal == null) {
                return;
            }

            SegmentedPanel main = new SegmentedPanel();

            String mission = currentPortal.getMission();
            if (mission != null) {
                main.addSegment(new Label(GlobalizationUtil.globalize(
                                          "cw.workspace.mission")),
                                          new Label(mission,false));
            }

            PartyCollection pc = currentPortal.getParticipants();
            ListPanel participants = new ListPanel(ListPanel.UNORDERED);

            long participantCount = pc.size();
            if (participantCount > MAX_PARTICIPANTS_FOR_LISTING) {
                DataQuery dq = SessionManager.getSession().retrieveQuery(
                                    "com.arsdigita.workspace.RolesWithCounts");
                dq.setParameter("workspaceID", currentPortal.getID());

                while (dq.next()) {
                    participants.add(new Label(
                             dq.get("roleCount") + " " + dq.get("roleName")));
                }

                participants.add( new Label(participantCount + " " 
                                                 + " Total participants"));
            } else {
                while (pc.next()) {
                    participants.add(new Label(pc.getDisplayName()));
                }
            }

            Link searchLink = new Link( new Label(GlobalizationUtil
                                            .globalize(
                                              "cw.workspace.search_participants")),
                                              "participants/");
       //   deprecated, replaced by setVar in class Link
       //   searchLink.addURLVars("action", "search");
            searchLink.setVar("action", "search");
            participants.add(searchLink);

            main.addSegment(new Label(GlobalizationUtil
                                      .globalize("cw.workspace.participants")),
                                                 participants);

            main.generateXML(ps, parent);
        }
    }
}

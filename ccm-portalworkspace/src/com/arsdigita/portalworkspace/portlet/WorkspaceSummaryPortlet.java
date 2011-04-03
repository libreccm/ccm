/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.portlet;

import com.arsdigita.bebop.Label;
// import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.portalworkspace.Workspace;
// import com.arsdigita.london.portal.WorkspaceCollection;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.persistence.DataObject; 
// import com.arsdigita.persistence.DataQuery;
// import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.apportlet.AppPortlet;
// import com.arsdigita.web.Application;
// import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/**
 * WorkspaceSummaryPortlet
 *
 *
 * @author dennis (2003/08/15)
 * @version $Id: WorkspaceSummaryPortlet.java  pboy  $
 */
public class WorkspaceSummaryPortlet extends AppPortlet {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.portalworkspace.portlet.WorkspaceSummaryPortlet";

    private static final int MAX_PARTICIPANTS_FOR_LISTING = 15;

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public WorkspaceSummaryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new WorkspaceSummaryPortletRenderer(this);
    }

    //public String getZoomURL() {
    //    return URL.getDispatcherPath() + getParentApplication().getPrimaryURL()
    //                                   + "participants/";
    //}

    private static class WorkspaceSummaryPortletRenderer
        extends AbstractPortletRenderer {

        private WorkspaceSummaryPortlet m_portlet;

        public WorkspaceSummaryPortletRenderer(WorkspaceSummaryPortlet portlet) {
            m_portlet = portlet;
        }

        protected void generateBodyXML(PageState ps, Element parent) {

            //Workspace workspace = (Workspace) m_portlet.getParentApplication();
            Workspace workspace = Workspace.getCurrentlySelectedWorkspace();

            if (workspace == null) {
                return;
            }

            SegmentedPanel main = new SegmentedPanel();

            String description = workspace.getDescription();
            if (description != null) {
                main.addSegment(new Label(GlobalizationUtil.globalize(
                        "cw.workspace.description")), new Label(description,false));
            }

            // should be a group
            PartyCollection pc = ((Group) workspace.getParty()).getAllMembers();
            ListPanel participants = new ListPanel(ListPanel.UNORDERED);

//             long participantCount = pc.size();
//             if (participantCount > MAX_PARTICIPANTS_FOR_LISTING) {
//                 DataQuery dq = SessionManager.getSession().retrieveQuery(
//                                     "com.arsdigita.workspace.RolesWithCounts");
//                 dq.setParameter("workspaceID", workspace.getID());

//                 while (dq.next()) {
//                     participants.add(new Label(
//                              dq.get("roleCount") + " " + dq.get("roleName")));
//                 }

//                 participants.add( new Label(participantCount + " " 
//                                                  + " Total participants"));
//             } else {
                while (pc.next()) {
                    participants.add(new Label(pc.getDisplayName()));
                }
//             }

            //Link searchLink = new Link(
            //    new Label(GlobalizationUtil
            //              .globalize("cw.workspace.search_participants")),
            //                         "participants/");
            //searchLink.addURLVars("action", "search");
            //participants.add(searchLink);

            main.addSegment(new Label(GlobalizationUtil.globalize(
                                   "cw.workspace.participants")), participants);

            main.generateXML(ps, parent);
        }
    }
}

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

package com.arsdigita.london.portal.portlet;

import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspaceCollection;
import com.arsdigita.london.portal.util.GlobalizationUtil; 
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

public class WorkspaceNavigatorPortlet extends AppPortlet {

    public static final String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/WorkspaceNavigatorPortlet.java#7 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.portal.portlet.WorkspaceNavigatorPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public WorkspaceNavigatorPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new WorkspaceNavigatorPortletRenderer(this);
    }
}

class WorkspaceNavigatorPortletRenderer extends AbstractPortletRenderer {
    private WorkspaceNavigatorPortlet m_portlet;

    public WorkspaceNavigatorPortletRenderer
        (WorkspaceNavigatorPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parentElement) {
        //Workspace workspace = (Workspace) m_portlet.getParentApplication();
        Workspace workspace = Workspace.getCurrentlySelectedWorkspace();

        if (workspace == null) {
            return;
        }

        GridPanel panel = new GridPanel(2);

        // Variables used cursorwise.
        int counter;
        String title = null;
        String url = null;

        // Parent Portal
        Application parent = workspace.getParentApplication();
        if (parent != null && parent instanceof Workspace) {
            Workspace pws = (Workspace) parent;
            title = pws.getTitle();
            url = pws.getPrimaryURL();
            panel.add(new Link(title, url));
            Label l = new Label(GlobalizationUtil.globalize("cw.workspace.parent"));
            l.setFontWeight(Label.ITALIC);
            panel.add(l, BlockStylable.RIGHT);
        }

        // Child Portals
        WorkspaceCollection childWorkspaces = workspace.getChildWorkspaces();
        while (childWorkspaces.next()) {
            Workspace child = childWorkspaces.getWorkspace();
            title = child.getTitle();
            url = child.getPrimaryURL();
            panel.add(new Link(title, url));
            Label l = new Label(GlobalizationUtil.globalize("cw.workspace.child"));
            l.setFontWeight(Label.ITALIC);
            panel.add(l, BlockStylable.RIGHT);
        }

        // Related workspaces
        WorkspaceCollection relatedWorkspaces = workspace.getRelatedWorkspaces();
        while (relatedWorkspaces.next()) {
            Workspace related = relatedWorkspaces.getWorkspace();
            title = related.getTitle();
            url = related.getPrimaryURL();
            panel.add(new Link(title, url));
            panel.add(new Label(""));
        }

        panel.generateXML(pageState, parentElement);
    }
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.portal.PortletType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

/**
 * Builds the logic to modify (edit) a portal pane (remember: 0..n portal 
 * panes make up a workspace, see com.arsdigita.portalworkspace.Workspace).
 *
 * It collects the portlets belonging to a portal, determines whether a portlet
 * is currently (request wise) to be edited or not and constructs the
 * corresponding modification or display logic and enables the addition of new
 * portlets.
 * 
 */
public class PortalEditModelBuilder extends LockableImpl 
                                    implements PortalModelBuilder {

    private static final Logger s_log = Logger
                                        .getLogger(PortalEditModelBuilder.class);
    private Component m_adders[];
    private PortalSelectionModel m_portal;
    private PortletTypeSelectionModel m_creating;
    private SingleSelectionModel m_column;
    private PortletSelectionModel m_editing;
    private HashMap m_create;
    private HashMap m_modify;
    private HashMap m_createApp;
    private RequestLocal m_parentResource;

    /**
     * Constructor, checks existence of parameters and initialies internal
     * variables.
     *
     * @param portal
     * @param adders
     * @param creating
     * @param column
     * @param editing
     * @param create
     * @param modify
     * @param createApp
     * @param parentResource
     */
    public PortalEditModelBuilder(PortalSelectionModel portal,
                                  Component adders[],
                                  PortletTypeSelectionModel creating,
                                  SingleSelectionModel column,
                                  PortletSelectionModel editing,
                                  HashMap create,
                                  HashMap modify,
                                  HashMap createApp,
                                  RequestLocal parentResource) {

        Assert.exists(portal, PortalSelectionModel.class);
        Assert.exists(adders, Component.class);
        Assert.exists(creating, PortletTypeSelectionModel.class);
        Assert.exists(column, SingleSelectionModel.class);
        Assert.exists(editing, PortletSelectionModel.class);
        Assert.exists(create, HashMap.class);
        Assert.exists(modify, HashMap.class);
        Assert.exists(createApp, HashMap.class);
        Assert.exists(parentResource, RequestLocal.class);

        m_portal = portal;
        m_adders = adders;
        m_create = create;
        m_modify = modify;
        m_creating = creating;
        m_column = column;
        m_editing = editing;
        m_parentResource = parentResource;
        m_createApp = createApp;

    }


    /**
     * Actually builds the logic for the pane under consideration.
     * 
     * @param state
     * @return
     */
    public PortalModel buildModel(PageState state) {

        WorkspacePage portal = m_portal.getSelectedPortal(state);

        ArrayList list = new ArrayList();
        PortletCollection portlets = portal.getPortlets();
        while (portlets.next()) {
            com.arsdigita.portal.Portlet portlet = portlets.getPortlet();
            PortletRenderer renderer = null;

            if (portlet.getID().equals(m_editing.getSelectedKey(state))) {
                // If this portlet is being edited, show the edit form
                renderer = new SimplePortlet("Configure portlet",
                                             portlet.getCellNumber(),
                                             portlet.getSortKey(),
                                             (Component) m_modify
                                                 .get(portlet.getPortletType()
                                                     .getResourceObjectType()));
            } else {
                // Otherwise show the normal portlet XML
                renderer = (PortletRenderer) portlet.getPortletRenderer();
            }
            list.add(new Object[] { renderer, portlet.getID() });
        }

        // If we've selected a portlet type then show the form
        // for creating that portlet
        if (m_creating.isSelected(state)) {
            PortletType type = m_creating.getSelectedPortletType(state);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Selected type " + m_creating.getSelectedKey(state)
                                             + (type == null ? null : type.getID()));
            }

            Component component = (Component) m_create.get(type
                                                      .getResourceObjectType());
            Integer column = (Integer) m_column.getSelectedKey(state);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Component is " + component + " "
                           + type.getResourceObjectType());
            }

            if (type.getProviderApplicationType() == null) {
                list.add(new SimplePortlet("Create portlet",
                                           column.intValue(),
                                           99998,
                                           component));
            } else {
                Component appCreate = (Component) m_createApp
                                                     .get(type
                                                     .getResourceObjectType());
                list.add(new ApplicationPortlet("Create portlet",
                                                column.intValue(),
                                                99998,
                                                m_parentResource,
                                                component,
                                                appCreate));
            }
        }

        // Show the forms for picking a portlet type to create
        for (int i = 0; i < portal.getLayout().getColumns(); i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding adder number " + i + m_adders[i]);
            }

            list.add(new SimplePortlet("Add portlet",
                                       i + 1,
                                       999999,
                                       m_adders[i]));
        }

        return new PortalEditModel(list.iterator(), portal.getTitle());

    }


    /**
     *
     */
    private class PortalEditModel implements PortalModel {

        private Iterator m_portlets;
        private String m_title;

        /**
         * 
         * @param portlets
         * @param title
         */
        public PortalEditModel(Iterator portlets, String title) {
            m_portlets = portlets;
            m_title = title;
        }

        /**
         * 
         * @return
         */
        public Iterator getPortletRenderers() {
            return m_portlets;
        }

        /**
         * 
         * @return
         */
        public String getTitle() {
            return m_title;
        }

    }

}

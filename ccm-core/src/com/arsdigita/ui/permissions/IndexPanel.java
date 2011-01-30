/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.permissions;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * A page that displays all <code>ACSObjects</code> that the user has
 * the "admin" privilege on. It also includes a search box for finding
 * <code>ACSObjects</code> by ID.
 *
 * @version $Id: IndexPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */
class IndexPanel extends SimpleContainer implements PermissionsConstants  {
    private static final String OBJECT_QUERY =
        "com.arsdigita.ui.permissions.PermissionObjects";
    private static final String ACSOBJECT_ID =
        "objectID";

    private SegmentedPanel m_segmentedPanel;
    private List m_adminObjs;
    private Label m_listHeader;
    private DimensionalNavbar m_navbar;

    /**
     * Constructor
     */

    public IndexPanel() {
        setClassAttr("PERMISSIONS");
        m_navbar = new DimensionalNavbar();
        m_navbar.add(new Link(PERSONAL_SITE.localize()+"", 
                              "/" + LegacyInitializer.getURL(
                                  LegacyInitializer.WORKSPACE_PAGE_KEY)));
        m_navbar.add(new Link(MAIN_SITE.localize()+"", "/"));
        m_navbar.add(new Label(PERMISSIONS_INDEX_NAVBAR.localize()+""));
        m_navbar.setClassAttr("permNavBar");
        add(m_navbar);

        m_listHeader = new Label(PAGE_OBJECT_INDEX);

        m_adminObjs = new List(new AdminObjectsPermListBuilder());
        m_adminObjs.setCellRenderer(new ListObjectLinkRenderer());
        m_adminObjs.setClassAttr("bulletList");

        BoxPanel boxpanel = new BoxPanel();
        boxpanel.add(m_listHeader);
        boxpanel.add(m_adminObjs);

        m_segmentedPanel = new SegmentedPanel();
        m_segmentedPanel.addSegment(new Label(PAGE_OBJECT_PANEL_TITLE.localize()+""), boxpanel);
        add(m_segmentedPanel);
    }

    /**
     * Internal class for a ListBuilder that with all
     * ACSObjects for which the user has ADMIN privilege
     */
    private class AdminObjectsPermListBuilder extends LockableImpl
            implements ListModelBuilder {

        public ListModel makeModel(List l, PageState s) {
            Party party = Kernel.getContext().getParty();

            DataQuery query = SessionManager.getSession().retrieveQuery
                (OBJECT_QUERY);
            PermissionService.filterQuery(query,
                                          ACSOBJECT_ID ,
                                          PrivilegeDescriptor.ADMIN,
                                          party.getOID());
            return new AdminObjectsPermListModel(query);
        }
    }


    private class AdminObjectsPermListModel implements ListModel {
        private DataQuery m_query;
        private BigDecimal m_id;
        private ACSObject m_object;

        AdminObjectsPermListModel(DataQuery query) {
            m_query = query;
        }

        public boolean next() {
            if(m_query.next()) {
                m_id = (BigDecimal)m_query.get(ACSOBJECT_ID );
                m_object = UserObjectStruct.loadObject(m_id);
                return true;
            }
            m_query.close();
            return false;
        }

        public String getKey() {
            return  m_object.getID().toString();
        }

        public Object getElement() {
            return m_object.getDisplayName();
        }
    }

    private final class ListObjectLinkRenderer implements ListCellRenderer {

        public Component getComponent(List list, PageState state, Object value,
                                      String key, int index, boolean isSelected) {

            Link link = new Link((String)value,
                                 "/permissions/one?"+OBJECT_ID+"="+key);
            return link;
        }
    }

}

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
package com.arsdigita.cms.ui.permissions;


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;


public class ObjectAdminListing extends SimpleContainer {

    private Table m_admin;
    private ActionLink m_addUser;
    private ACSObjectSelectionModel m_object;
    private ObjectAddSearchAdmin m_search;

    public ObjectAdminListing(ACSObjectSelectionModel model) {
        super("cms:roleAdmin", CMS.CMS_XML_NS);

        m_object = model;

        m_admin = new Table(getTableModelBuilder(model),
                            new String[] {"Member", "Action"});
        m_admin.setDefaultCellRenderer(new ObjectAdminTableRenderer());
        m_admin.setEmptyView(new Label("There are no administrators for this object"));
        m_admin.setClassAttr("dataTable");
        m_admin.addTableActionListener(new ObjectAdminActionListener());



        m_addUser = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.permissions.add_administrator").localize());
        m_addUser.setClassAttr("actionLink");

        m_search = getObjectAddSearchAdmin(model);

        m_addUser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_search.setVisible(e.getPageState(), true);
                    m_addUser.setVisible(e.getPageState(), false);
                }
            });

        m_search.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_search.setVisible(e.getPageState(), false);
                    m_addUser.setVisible(e.getPageState(), true);
                }
            });


        add(m_admin);
        add(m_addUser);
        add(m_search);
    }

    public void register(Page p) {
        super.register(p);

        p.setVisibleDefault(m_search, false);
    }

    // This returns the add search admin form to use for this object
    protected ObjectAddSearchAdmin getObjectAddSearchAdmin
        (ACSObjectSelectionModel model) {
        return new ObjectAddSearchAdmin(model);
    }

    private class ObjectAdminActionListener implements TableActionListener {
        public void cellSelected(TableActionEvent e) {
            if (e.getColumn().intValue() == 1) {
                PageState state = e.getPageState();

                SecurityManager sm = Utilities.getSecurityManager(state);
                boolean isAdmin =
                    sm.canAccess(state.getRequest(),
                                 SecurityManager.STAFF_ADMIN);

                if (!isAdmin) {
                    throw new AccessDeniedException( (String) GlobalizationUtil.globalize("cms.ui.permissions.not_an_object_adminstrator").localize());
                }

                Party party = null;
                try {
                    String id = (String)e.getRowKey();
                    party = (Party)DomainObjectFactory
                        .newInstance(new OID(Party.BASE_DATA_OBJECT_TYPE,
                                             new BigDecimal(id)));
                } catch (DataObjectNotFoundException ex) {
                    throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.ui.permissions.cannot_retrieve_party").localize(),  ex);
                }

                ACSObject object = (ACSObject)m_object.getSelectedObject(state);

                PermissionDescriptor perm =
                    new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                             object,
                                             party);

                PermissionService.revokePermission(perm);
            }
        }

        public void headSelected(TableActionEvent e) {
        }
    }


    protected TableModelBuilder
        getTableModelBuilder(ACSObjectSelectionModel model) {
        return new ObjectTableModelBuilder(model);
    }

    private class ObjectTableModelBuilder extends LockableImpl
        implements TableModelBuilder {
        private ACSObjectSelectionModel m_model;

        ObjectTableModelBuilder(ACSObjectSelectionModel model) {
            m_model = model;
        }

        public TableModel makeModel(Table l, PageState state) {
            ACSObject object = (ACSObject)m_model.getSelectedObject(state);
            DataQuery parties = SessionManager.getSession().retrieveQuery
                ("com.arsdigita.cms.objectAdminListing");
            parties.setParameter("objectID", object.getID());
            return new ObjectAdminTableModel(parties);
        }
    }


    public class ObjectAdminTableModel implements TableModel {

        private DataQuery m_parties;

        public ObjectAdminTableModel(DataQuery parties) {
            m_parties = parties;
        }

        public int getColumnCount() {
            return 2;
        }

        public boolean nextRow() {
            return m_parties.next();
        }
        public Object getElementAt(int column) {
            return DomainObjectFactory.newInstance
                ((DataObject)m_parties.get("party"));
        }

        public Object getKeyAt(int column) {
            return m_parties.get("party.id").toString();
        }
    }


    private class ObjectAdminTableRenderer implements TableCellRenderer {
        public Component getComponent(Table list,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row, int column) {
            Party party = (Party)value;

            if (column == 0) {
                Label l = new Label(party.getDisplayName());
                return l;
            } else if (column == 1) {
                Label l = new Label("remove");
                return new ControlLink(l);
            }
            throw new IllegalArgumentException("Column index " + column + " out of bounds 0..1");
        }
    }
}

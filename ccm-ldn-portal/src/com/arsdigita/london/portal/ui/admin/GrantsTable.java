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

package com.arsdigita.london.portal.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableHeader;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
//import com.arsdigita.kernel.permissions.ParameterizedPrivilege;
import com.arsdigita.london.portal.util.GlobalizationUtil; 
import com.arsdigita.london.portal.ui.Icons;
import com.arsdigita.persistence.OID;

import java.util.Iterator;

import org.apache.log4j.Category;

/**
 * GrantsTable.
 *
 * @author dennis (2003/08/15)
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/permissions/GrantsTable.java#2 $
 */ 
abstract class GrantsTable extends Table {

    private static Category s_log = Category.getInstance
        (GrantsTable.class.getName());

    private static final String REMOVE_EVENT = "_revoke_all_";

    private static final String COLUMN_WIDTH = "12%";

    // These RLs are used by all the control links to get their
    // control event values.  It's basically a communications
    // chanel between getComponent and setControlEvent.
    final static RequestLocal s_targetRL = new RequestLocal();
    final static RequestLocal s_adjustCodeRL = new RequestLocal();

    private static class EditPrivilegeCellRenderer
        implements TableCellRenderer {
        final PrivilegeDescriptor m_privilege;
        final ControlLink m_link;

        EditPrivilegeCellRenderer(PrivilegeDescriptor privilege) {
            m_privilege = privilege;

            final String eventName = privilege.getName();

            m_link = new ControlLink(Icons.RADIO_EMPTY_16) {
                    public void setControlEvent(PageState ps) {
                        ps.setControlEvent((Component) s_targetRL.get(ps),
                                           eventName,
                                           (String) s_adjustCodeRL.get(ps));
                    }
                };
        }

        public Component getComponent(Table table, PageState ps,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {
            Grant grant = (Grant) value;

            if (grant.basePrivilege.equals(m_privilege)) {
                // If the current privilege is equal to the privilege
                // this component stands for, return the (*).
                return Icons.RADIO_FULL_16;
            } else {
                // Otherwise set up the control event for the link.
                s_targetRL.set(ps, table);
                s_adjustCodeRL.set(ps, key);
                return m_link;
            }
        }
    }

    private static class ViewPrivilegeCellRenderer
        implements TableCellRenderer {
        final PrivilegeDescriptor m_privilege;

        ViewPrivilegeCellRenderer(PrivilegeDescriptor privilege) {
            m_privilege = privilege;
        }

        public Component getComponent(Table table, PageState ps,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {
            Grant grant = (Grant) value;

            if (grant.basePrivilege.equals(m_privilege)) {
                return Icons.RADIO_FULL_GRAYED_16;
            } else {
                return Icons.RADIO_EMPTY_GRAYED_16;
            }
        }
    }

    private static TableCellRenderer[] s_editCellRenderers;
    private static TableCellRenderer[] s_viewCellRenderers;

    static {
        s_log.debug("Static initalizer starting...");
        int n = Grant.s_interestingPrivileges.length;
        s_editCellRenderers = new TableCellRenderer[n];
        s_viewCellRenderers = new TableCellRenderer[n];
        for (int i = 0; i < n; i++) {
            s_editCellRenderers[i] =
                new EditPrivilegeCellRenderer(Grant.s_interestingPrivileges[i]);
            s_viewCellRenderers[i] =
                new ViewPrivilegeCellRenderer(Grant.s_interestingPrivileges[i]);
        }
        s_log.debug("Static initalizer finished.");
    }

    // Per-request label for renderer getComponent calls
    private final static RequestLocal s_dynamicLabel = new RequestLocal() {
            public Object initialValue(PageState ps) {
                return new Label();
            }
        };

    public GrantsTable(final RequestLocal grantsRL,
                       final RequestLocal typesRL,
                       final boolean isEditable) {
        super();

        setClassAttr("fancy");

        setModelBuilder(new AbstractTableModelBuilder() {
                public TableModel makeModel(Table t, PageState ps) {
                    final Iterator grants = (Iterator) grantsRL.get(ps);

                    return new TableModel() {
                            Grant m_currentGrant;
                            String m_currentKey;

                            public int getColumnCount() {
                                return 6;
                            }

                            public Object getElementAt(int index) {
                                return m_currentGrant;
                            }

                            public Object getKeyAt(int columnIndex) {
                                return m_currentKey;
                            }

                            public boolean nextRow() {
                                if (grants.hasNext()) {
                                    m_currentGrant = (Grant) grants.next();

                                    s_log.debug("Preparing to display Grant "
                                                + m_currentGrant);

                                    m_currentKey = m_currentGrant.toString();

                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        };
                }
            });

        TableColumnModel columnModel = new DefaultTableColumnModel();

        // We assume that whoever uses the GrantsTable will
        // be adding a column of their own

        // COLUMN 1: Type Name (may be hidden)
        TableColumn typeColumn = new TableColumn(1, "On") {
                public boolean isVisible(PageState ps) {
                    return (typesRL.get(ps) != null);
                }
            };

        typeColumn.setCellRenderer(new TableCellRenderer() {
                public Component getComponent(Table table,
                                              PageState ps,
                                              Object value,
                                              boolean isSelected,
                                              Object key,
                                              int row,
                                              int column) {
                    Grant grant = (Grant) value;
                    Label typeLabel = (Label) s_dynamicLabel.get(ps);
                    typeLabel.setLabel(grant.objectTypeDisplayName);
                    return typeLabel;
                }
            });
        columnModel.add(typeColumn);

        // COLUMNS 2 through N: Privilege entries
        TableCellRenderer[] cellRenderers;

        if (isEditable) {
            cellRenderers = s_editCellRenderers;
        } else {
            cellRenderers = s_viewCellRenderers;
        }

        for (int i = 0; i < Grant.s_interestingPrivileges.length; i++) {
            TableColumn privColumn = new TableColumn
                (2 + i, Grant.s_privilegePrettyNames[i]);

            privColumn.setCellRenderer(cellRenderers[i]);
            privColumn.setClassAttr("icon");
            privColumn.setHeadClassAttr("icon");
            privColumn.setWidth(COLUMN_WIDTH);

            columnModel.add(privColumn);
        }

        // COLUMN N + 1: Optional REMOVE column
        if (isEditable) {
            TableColumn removeColumn = new TableColumn(5, "Remove");

            removeColumn.setAlign("center");
            removeColumn.setCellRenderer(new TableCellRenderer() {
                    final ControlLink m_link = new ControlLink(Icons.TRASH_16) {
                            public void setControlEvent(PageState ps) {
                                ps.setControlEvent(GrantsTable.this,
                                                   REMOVE_EVENT,
                                                   (String) s_adjustCodeRL.get(ps));
                            }
                        };

                    public Component getComponent(Table table,
                                                  PageState ps,
                                                  Object value,
                                                  boolean isSelected,
                                                  Object key,
                                                  int row,
                                                  int column) {
                        Grant grant = (Grant) value;

                        s_adjustCodeRL.set(ps, key);

                        return m_link;
                    }
                });

            removeColumn.setClassAttr("icon");
            removeColumn.setHeadClassAttr("icon");
            removeColumn.setWidth(COLUMN_WIDTH);

            columnModel.add(removeColumn);
        }

        setColumnModel(columnModel);
        setHeader(new TableHeader(columnModel));
    }

    public void respond(PageState ps) {
        String controlEventName = ps.getControlEventName();
        String controlEventValue = ps.getControlEventValue();

        s_log.debug("Responding to control event \"" + controlEventName +
                    " => " + controlEventValue + "\"");

        PrivilegeDescriptor newPriv = null;

        found: if (!REMOVE_EVENT.equals(controlEventName)) {
            for (int i = 0; i < Grant.s_interestingPrivileges.length; i++) {
                PrivilegeDescriptor priv = Grant.s_interestingPrivileges[i];
                String privName = priv.getName();

                if (privName.equals(controlEventName)) {
                    newPriv = priv;
                    break found;
                }
            }

            throw new IllegalStateException("Unknown control event");
        }

        Grant grant = Grant.unmarshal(ps.getControlEventValue());

        OID objectOID = new OID
            (ACSObject.BASE_DATA_OBJECT_TYPE, grant.objectID);

        OID partyOID = new OID
            (Party.BASE_DATA_OBJECT_TYPE, grant.granteeID);

        s_log.debug("Privilege requested is " + newPriv + ". " +
                    "Existing privilege is " + grant.basePrivilege + ".");

        int oldLevel = Grant.getPrivilegeLevel(grant.basePrivilege);

        if (oldLevel > 0 && newPriv == null) {
            // REMOVE_EVENT case 1: There are existing privileges
            // other than read.

            s_log.debug("Revoking old privilege.  Old privilege was " +
                        oldLevel + ".");

            PermissionDescriptor createPD =
                getPermissionDescriptorHelper(grant, Grant.CREATE,
                                              objectOID, partyOID);
            PermissionDescriptor oldPD =
                getPermissionDescriptorHelper(grant, oldLevel,
                                              objectOID, partyOID);
            PermissionService.revokePermission(createPD);
            PermissionService.revokePermission(oldPD);
        } else if (oldLevel == 0 && newPriv == null) {
            // REMOVE_EVENT case 2: Only existing privilege is read.

            s_log.debug("Revoking old privilege.  Old privilege was " +
                        oldLevel + ".");

            PermissionDescriptor oldPD =
                getPermissionDescriptorHelper(grant, oldLevel,
                                              objectOID, partyOID);
            PermissionService.revokePermission(oldPD);
        } else {
            // Add new privileges (but first revoke old ones).

            s_log.debug("Adding new privilege, but first removing old ones.");

            int newLevel = Grant.getPrivilegeLevel(newPriv);

            // Revoke old privileges.
            PermissionDescriptor oldPD =
                getPermissionDescriptorHelper(grant, oldLevel,
                                              objectOID, partyOID);
            PermissionService.revokePermission(oldPD);

            // Grant new privileges.
            PermissionDescriptor newPD =
                getPermissionDescriptorHelper(grant, newLevel,
                                              objectOID, partyOID);
            PermissionService.grantPermission(newPD);

            // Handle create.
            PermissionDescriptor createPD =
                getPermissionDescriptorHelper(grant, Grant.CREATE,
                                              objectOID, partyOID);

            if (newLevel == Grant.EDIT || newLevel == Grant.ADMIN) {
                s_log.debug("Implicitly granting create.");

                PermissionService.grantPermission(createPD);
            } else if (oldLevel == Grant.EDIT || oldLevel == Grant.ADMIN) {
                // If the new permission grant is one that
                // does not require create AND the old permission
                // did have create, revoke it.

                s_log.debug("Implicitly revoking create.");

                PermissionService.revokePermission(createPD);
            }
        }
    }

    private PermissionDescriptor getPermissionDescriptorHelper
        (Grant grant, int privLevel, OID objectOID, OID partyOID) {
        PrivilegeDescriptor priv;

//         if (grant.objectType != null) {
//             priv = ParameterizedPrivilege.createPrivilege
//                 (Grant.s_privileges[privLevel],
//                  grant.objectType.getQualifiedName(),
//                  "");
//         } else {
            priv = Grant.s_privileges[privLevel];
//         }

        return new PermissionDescriptor(priv, objectOID, partyOID);
    }
}

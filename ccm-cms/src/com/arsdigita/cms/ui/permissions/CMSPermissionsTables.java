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
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Class to represent direct and inherited permissions of an
 * ACSObject. This class provides two SegmentPanels with the direct
 * and the inherited permissions tables, respectively.  The expected
 * pageState contains a variable "id=123" of which the permissions are
 * rendered. The viewing user must be authenticated.  The permissions
 * representations can be swapped , e.g.  with a List, if scalability
 * warrants.
 *
 * @author Stefan Deusch
 */

class CMSPermissionsTables implements CMSPermissionsConstants {

    private static Logger s_log =
        Logger.getLogger(CMSPermissionsTables.class);

    private CMSPermissionsPane m_parent;
    private PrivilegeDescriptor[] m_privileges;
    private GridPanel m_permPanel[] = new GridPanel[2];
    private int[] m_tableColumns = new int[2];
    private ActionLink m_contextLink;
    private Label m_contextLabel;

    /**
     * Default constructor uses the DEFAULT_PRIVILEGES
     * as defined in PermissionsConstants.
     */

    CMSPermissionsTables (CMSPermissionsPane parent) {
        this(DEFAULT_PRIVILEGES, parent);
    }

    /**
     * Constructor that takes an array of PrivilegeDescriptors and builds
     * the grantee - privilege matrix. <strong>The permissions tables contain
     * the set of privileges that are passed into this constructor.</strong>
     * @param privs the array of PrivilegeDesrciptors with which go into table
     * @param parent the Bebop parent container
     */

    CMSPermissionsTables(PrivilegeDescriptor[] privs,
                         CMSPermissionsPane parent) {
        m_parent = parent;

        // fixed table information
        m_privileges = privs;
        m_tableColumns[DIRECT] = m_privileges.length+2;
        m_tableColumns[INHERITED] = m_privileges.length+1;

        Table table;
        BoxPanel boxpanel;

        // Construct Direct Permissions Panel
        m_permPanel[DIRECT] = new GridPanel(1);
        table = new Table(new PermissionsTableModelBuilder(DIRECT),
                          getHeaders(DIRECT));
        table.setClassAttr("dataTable");
        setCellRenderers(table, DIRECT);
        table.addTableActionListener(new DirectPermissionsTableActionListener());
        m_permPanel[DIRECT].add
            (new Label(GlobalizationUtil.globalize
                       ("cms.ui.permissions.these_are_the_custom_permissions" +
                        "_that_have_been_granted_on_this_object")));
        m_permPanel[DIRECT].add(table);

        //m_permPanel[DIRECT].addSegment(new Label(PERM_TABLE_DIRECT_HEADING),
        //                               boxpanel);

        // Construct Inherited Permissions Panel
        m_permPanel[INHERITED] = new GridPanel(1);
        table = new Table(new PermissionsTableModelBuilder(INHERITED),
                          getHeaders(INHERITED));
        table.setClassAttr("dataTable");
        setCellRenderers(table, INHERITED);
        m_permPanel[INHERITED].add(new Label(GlobalizationUtil.globalize("cms.ui.permissions.these_are_the_current_permissions_for_this_folder")));
        m_permPanel[INHERITED].add(table);

        //m_permPanel[INHERITED].addSegment(new Label(PERM_TABLE_INDIRECT_HEADING),
        //                                 boxpanel);
    }

    /**
     * Returns the SegmentedPanel with either the direct or the indirect
     * permissions table.
     * @param use PermissionsContants.DIRECT or
     * PermissionsContants.INHERITED
     */

    GridPanel getPermissions(int type) {
        return m_permPanel[type];
    }

    /**
     * Returns the set of privileges of the permission
     * tables as a String array.
     */

    String[] getPrivileges() {
        String[] privs = new String[m_privileges.length];
        for(int i=0; i<m_privileges.length; i++) {
            privs[i] = m_privileges[i].getName();
        }
        return privs;
    }


    SimpleComponent makeContextPanel() {
        SimpleContainer contextPanel = new SimpleContainer();
        Label contextLabel = new Label();
        contextLabel.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    Label l = (Label) e.getTarget();
                    ACSObject context = getContext(s);
                    if (context != null ) {
                        l.setLabel(PERM_TABLE_INDIRECT_CONTEXT
                                   .localize(s.getRequest())+"");
                        m_contextLabel.setVisible(s, true);
                    } else {
                        l.setLabel(PERM_TABLE_NO_PARENT_CONTEXT
                                   .localize(s.getRequest())+"");
                        m_contextLabel.setVisible(s, false);
                    }
                }
            });

        m_contextLabel = new Label();
        m_contextLabel.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    Label l = (Label) e.getTarget();
                    ACSObject context = getContext(s);
                    if(context != null ) {
                        l.setLabel(context.getDisplayName());
                    }
                }
            });
        m_contextLabel.setFontWeight(Label.BOLD);

        // m_contextLink = new ActionLink(linkLabel);
        //
        // m_contextLink.addActionListener(new ActionListener() {
        //         public void actionPerformed(ActionEvent e) {
        //             PageState s = e.getPageState();
        //             ActionLink l = (ActionLink)e.getSource();
        //             ACSObject context = getContext(s);
        //             if (context != null &&
        //                 context.getObjectType().
        //                 isSubtypeOf(SessionManager.getMetadataRoot().
        //                             getObjectType(Folder.BASE_DATA_OBJECT_TYPE))) {
        //                 // only change the selected object if it is a folder (or subtype of folder)
        //                 m_parent.getSelectionModel().
        //                     setSelectedObject(s, context);
        //             }
        //         }
        //     });

        contextPanel.add(contextLabel);
        // don't want to provide a link, because the folder tree gets out of sync
        // contextPanel.add(m_contextLink);
        contextPanel.add(m_contextLabel);
        return contextPanel;
    }

    ACSObject getContext(PageState s){
        ACSObject obj = m_parent.getObject(s);
        DataObject dobj = PermissionService.getContext(obj);
        if (dobj!=null) {
            OID oid = dobj.getOID();
            ACSObject context = CMSUserObjectStruct.loadObject(oid);
            return context;
        }
        return null;
    }

    private String[] getHeaders(int type) {
        String[] headers = new String[m_tableColumns[type]];
        headers[0] =  PERM_TABLE_GRANTEE.localize()+"";
        for (int j=0; j<m_privileges.length; j++) {
            headers[j+1] = m_parent.getPrivilegeName(m_privileges[j].getDisplayName());
        }
        if (type==DIRECT) {
            headers[m_privileges.length+1] = PERM_TABLE_ACTIONS.localize()+"";
        }
        return headers;
    }

    private void setCellRenderers(Table t, int type) {
        int j;
        if (type == DIRECT) {
            for(j=1; j < t.getColumnModel().size()-1; j++) {
                t.getColumn(j).setCellRenderer(new PermissionToggleRenderer());
            }
            t.getColumn(j).setCellRenderer(new LinkRenderer());
        } else {
            for (j=1; j < t.getColumnModel().size(); j++) {
                t.getColumn(j).setCellRenderer(new PermissionStatusRenderer());
            }
        }
    }

    private class DirectPermissionsTableActionListener
        implements TableActionListener {

        public void cellSelected(TableActionEvent e) {
            PageState state = e.getPageState();
            int col = e.getColumn().intValue();
            String rowkey = (String)e.getRowKey();
            if (rowkey == null){
                return;
            }

            Table table = (Table)e.getSource();
            int no_cols = table.getColumnModel().size();
            int lastCol = no_cols-1;

            if (col > 0 && col < lastCol) {

                PermissionStatus pmds = UserPrivilegeKey.undescribe(rowkey);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Do perm toggle on " + pmds.getObject().getOID() + 
                                " for " + pmds.getParty().getOID() + 
                                " of " + pmds.getPrivilegeDescriptor());
                }
                if (pmds.isGranted()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Do revoke perm");
                    }
                    PermissionService.
                        revokePermission(pmds.getPermissionDescriptor());

                    if (PermissionService.
                        checkPermission(pmds.getPermissionDescriptor())) {

                        // This should probably flash a panel and be logged
                        s_log.warn ("Warning: Permission revoked, " +
                                    " but Party still has" +
                                    " Permission through inheritance");
                    }
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Do grant perm");

                        if (PermissionService.
                            checkPermission(pmds.getPermissionDescriptor())) {
                            s_log.debug("Permission is already implied by an existing perm");
                        }
                    }

                    PermissionService.
                        grantPermission(pmds.getPermissionDescriptor());
                }

            } else if (col == lastCol) {
                // Process Remove All Link
                String[] tokens = StringUtils.split(rowkey, '.');
                BigDecimal pID = new BigDecimal(tokens[0]);

                /*
                 * Remove all indicated privileges from user
                 * enumerated in tokens array
                 */

                ACSObject obj = (ACSObject) m_parent.getObject(state);
                Party party = CMSUserObjectStruct.loadParty(pID);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Revoke all on " + obj.getOID() + " for " + obj.getOID());
                }
                PermissionDescriptor pmd = null;

                for (int j=1; j<tokens.length; j++) {
                    pmd = new PermissionDescriptor(PrivilegeDescriptor.get(tokens[j]),
                                                   obj,
                                                   party);
                    PermissionService.revokePermission(pmd);
                }
            }
        }

        public void headSelected(TableActionEvent e) {
            throw new UnsupportedOperationException();
        }
    }

    private final class PermissionsTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

        int m_type;

        PermissionsTableModelBuilder(int type) {
            m_type = type;
        }

        /*
         * this can be optimized to run the query only
         * once for both tables
         */
        public TableModel makeModel(Table t, PageState s) {
            BigDecimal id = m_parent.getObject(s).getID();
            ObjectPermissionCollection m_perm = PermissionService
                .getGrantedPermissions
                (new OID(ACSObject.BASE_DATA_OBJECT_TYPE, id));
            if(m_type==DIRECT) {
                return new DirectPermissionsTableModel(m_perm, id);
            } else if(m_type==INHERITED) {
                return new InheritedPermissionsTableModel(m_perm, id);
            }
            return null;
        }
    }

    private class DirectPermissionsTableModel implements TableModel {
        ObjectPermissionCollection m_perm;
        String m_currentGrantee;
        BigDecimal m_currentGranteeID;
        BigDecimal m_objectID;
        boolean m_more;
        ArrayList m_userPrivs = new ArrayList();

        public DirectPermissionsTableModel() {}

        public DirectPermissionsTableModel
            (ObjectPermissionCollection perm, BigDecimal id) {
            m_objectID = id;
            m_perm = perm;
            m_more = m_perm.next();
        }

        public int getColumnCount() {
            return m_tableColumns[DIRECT];
        }

        public Object getElementAt(int columnIndex) {
            if (columnIndex == 0) {

                // the Grantee column
                return m_currentGrantee;

            } else if (columnIndex == getColumnCount() - 1) {

                // the Action column
                return "Remove All";

            } else  {
                if (userHasPermission(columnIndex-1)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        }

        public Object getKeyAt(int columnIndex) {
            if (columnIndex == 0) {

                // the key for the grantee
                return m_currentGrantee;

            } else if(columnIndex == getColumnCount() - 1) {

                // key for 'Remove All' link
                return makeRemoveAllKey();

            } else {

                // key for a user privilege
                return
                    (new UserPrivilegeKey(m_objectID,
                                          m_currentGranteeID,
                                          m_privileges[columnIndex-1].getName(),
                                          userHasPermission(columnIndex-1))
                     ).toString();
            }
        }

        public boolean nextRow() {

            if(getGranteeInfo()) {
                return true;
            }
            m_perm.close();
            return false;
        }

        boolean userHasPermission(int idx) {
            return m_userPrivs.contains(m_privileges[idx]);
        }

        private String makeRemoveAllKey() {
            StringBuffer sb = new StringBuffer();
            sb.append(m_currentGranteeID.toString());
            for(int i=0; i < m_privileges.length; i++) {
                if(userHasPermission(i)) {
                    sb.append("." + m_privileges[i].getName());
                }
            }
            return sb.toString();
        }


        /*
         * collect privileges per user relevant in this table
         */
        private boolean getGranteeInfo() {
            if(!m_more || m_perm.isInherited()) {
                return false;
            }
            // initialize new table row
            m_currentGrantee = m_perm.getGranteeName();
            m_currentGranteeID =  m_perm.getGranteeID();
            m_userPrivs.clear();

            // find what privileges one user has
            while(m_more &&
                  m_perm.getGranteeName().compareTo(m_currentGrantee)==0) {

                addPrivilege(m_perm.getPrivilege());
                m_more = m_perm.next();
            }

            // build row if user has at least one privileges
            if( m_userPrivs.size() > 0) {
                return true;
            } else {
                return getGranteeInfo();
            }

        }

        void addPrivilege(PrivilegeDescriptor priv) {
            for(int i=0; i<m_privileges.length; i++) {
                if(m_privileges[i].equals(priv)) {
                    m_userPrivs.add(priv);
                    break;
                }
            }
        }
    }

    /**
     * Extension of DirectPermissionsTableModel to accomodate
     * Inherited permissions table model.
     */
    private final class InheritedPermissionsTableModel
        extends DirectPermissionsTableModel {

        public InheritedPermissionsTableModel (ObjectPermissionCollection perm,
                                               BigDecimal id) {
            m_objectID = id;
            m_perm = perm;
            // spool to beginning of inherited permissions
            while( m_more = m_perm.next()) {
                if( m_perm.isInherited() )
                    break;
            }
        }

        public int getColumnCount() {
            return m_tableColumns[INHERITED];
        }

        public Object getElementAt(int columnIndex) {
            if (columnIndex == 0) {

                // the Grantee column
                return m_currentGrantee;

            } else  {
                if (userHasPermission(columnIndex-1)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        }

        public Object getKeyAt(int columnIndex) {
            if (columnIndex == 0) {

                // the key for the grantee
                return m_currentGrantee;

            }
            // no keys for inherited permissions
            return null;
        }

        public boolean nextRow() {

            if(getGranteeInfo()) {
                return true;
            }
            m_perm.close();
            return false;
        }

        /*
         * collect privileges per user relevant in this table
         */
        private boolean getGranteeInfo() {
            if(!m_more ) {
                return false;
            }

            // initialize new table row
            m_currentGrantee = m_perm.getGranteeName();
            m_currentGranteeID =  m_perm.getGranteeID();
            m_userPrivs.clear();

            // find what privileges one user has
            while(m_more &&
                  m_perm.getGranteeName().compareTo(m_currentGrantee)==0) {

                addPrivilege(m_perm.getPrivilege());
                m_more = m_perm.next();
            }

            // build row if user has at least one privileges
            if( m_userPrivs.size() > 0) {
                return true;
            } else {
                return getGranteeInfo();
            }

        }
    }

    private final class PermissionToggleRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column)
        {
            ControlLink link = new ControlLink("");

            if (((Boolean) value).booleanValue()) {
                link.setClassAttr("checkBoxChecked");
            } else {
                link.setClassAttr("checkBoxUnchecked");
            }

            return link;
        }
    }

    private final class PermissionStatusRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            Label link = new Label();

            if (((Boolean) value).booleanValue()) {
                link.setClassAttr("checkBoxGreyChecked");
            } else {
                link.setClassAttr("checkBoxGreyUnchecked");
            }

            return link;
        }
    }

    private final class LinkRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ControlLink cl = new ControlLink((String) value);
            cl.setConfirmation(REMOVE_ALL_CONFIRM.localize().toString());
            return cl;
        }
    }
}


/**
 * Utility class to encode a user privilege in the bebop table
 */

final class UserPrivilegeKey {

    String m_objectID;
    String m_granteeID;
    String m_privilege;
    byte  m_granted;

    public UserPrivilegeKey(BigDecimal objectID,
                            BigDecimal granteeID,
                            String privilege,
                            boolean granted) {
        m_objectID = objectID.toString();
        m_granteeID = granteeID.toString();
        m_privilege = privilege;
        m_granted = granted?(byte)1:(byte)0;
    }

    public String toString() {
        return
            m_privilege + "." +
            m_objectID  + "." +
            m_granteeID + "." +
            m_granted;
    }

    /**
     * Decodes the information in a key into the helper class
     * @see PermissionStatus
     */

    static PermissionStatus undescribe(String key) {

        int i = key.indexOf(".");
        int j = key.indexOf(".", i+1);
        int k = key.lastIndexOf(".");

        String privilege = key.substring(0,i);
        BigDecimal oID = new BigDecimal(key.substring(i+1, j));
        BigDecimal gID = new BigDecimal(key.substring(j+1, k));

        byte granted = (byte)0;
        CMSUserObjectStruct uos = null;
        try {
            granted = Byte.parseByte(key.substring(k+1,k+2));
            uos = new CMSUserObjectStruct(gID, oID);
        } catch (NumberFormatException nfe) {
            // cannot decode
            throw new IllegalArgumentException(nfe.getMessage());
        }

        return new PermissionStatus(PrivilegeDescriptor.get(privilege),
                                    uos.getObject(),
                                    uos.getParty(),
                                    granted);
    }
}

/**
 * Structure to hold a permission and its current grant state
 */

final class PermissionStatus {
    PermissionDescriptor m_pmd;
    byte m_granted;
    ACSObject m_obj;
    Party m_party;
    PrivilegeDescriptor m_priv;

    PermissionStatus(PrivilegeDescriptor pd,
                     ACSObject o,
                     Party p,
                     byte granted) {
        m_pmd = new PermissionDescriptor(pd, o, p);
        m_granted = granted;

        m_obj = o;
        m_party = p;
        m_priv = pd;
    }

    boolean isGranted() {
        return m_granted==1;
    }

    PermissionDescriptor getPermissionDescriptor() {
        return m_pmd;
    }

    ACSObject getObject() {
        return m_obj;
    }

    Party getParty() {
        return m_party;
    }

    PrivilegeDescriptor getPrivilegeDescriptor() {
        return m_priv;
    }
}

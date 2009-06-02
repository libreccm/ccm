package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.contenttypes.OrganizationRoleCollection;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class RoleTable extends Table {

    private final static Logger logger = Logger.getLogger(RoleTable.class);

    // columns headings
    public static final String COL_TITLE = "Role";
    public static final String COL_EDIT = "Edit";
    public static final String COL_MOVE = "Move";
    public static final String COL_DEL = "Delete";
    private ItemSelectionModel m_selectionOrganization;
    private ItemSelectionModel m_selectionRole;
    private ItemSelectionModel m_moveRole;

    public RoleTable(ItemSelectionModel selOrga, ItemSelectionModel moveRole) {
        super();
        m_selectionOrganization = selOrga;
        m_moveRole = moveRole;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(0, COL_TITLE));
        model.add(new TableColumn(1, COL_EDIT));
        model.add(new TableColumn(2, COL_MOVE));
        model.add(new TableColumn(3, COL_DEL));

        model.get(1).setCellRenderer(new RoleTableCellRenderer(true));
        model.get(2).setCellRenderer(new RoleTableCellRenderer(true));
        model.get(3).setCellRenderer(new RoleTableCellRenderer(true));

        setModelBuilder(new RoleTableModelBuilder(m_selectionOrganization, m_moveRole));

        addTableActionListener(new TableActionListener() {

            public void cellSelected(TableActionEvent e) {
                PageState state = e.getPageState();

                TableColumn col = getColumnModel().get(e.getColumn().intValue());
                String colName = (String) col.getHeaderValue();

                if (COL_MOVE.equals(colName)) {
                    if (m_moveRole.getSelectedKey(state) == null) {
                        m_moveRole.setSelectedKey(state, m_selectionRole.getSelectedKey(state));
                    } else {
                        GenericOrganization orga = (GenericOrganization) m_selectionOrganization.getSelectedObject(state);

                        BigDecimal id = (BigDecimal) m_moveRole.getSelectedKey(state);
                        OrganizationRole role = (OrganizationRole) DomainObjectFactory.newInstance(new OID(OrganizationRole.BASE_DATA_OBJECT_TYPE, id));

                        BigDecimal dest = new BigDecimal((String) e.getRowKey());
                        OrganizationRole destRole = (OrganizationRole) DomainObjectFactory.newInstance(new OID(OrganizationRole.BASE_DATA_OBJECT_TYPE, "dest"));

                        m_moveRole.setSelectedKey(state, null);
                    }
                }
            }

            public void headSelected(TableActionEvent e) {
                //Nothing 
            }
        });

    }

    public void setRoleModel(ItemSelectionModel itemModel) {
        if (itemModel == null) {
            logger.warn("null item model");
        }
        m_selectionRole = itemModel;
    }

    protected class RoleTableModelBuilder extends LockableImpl implements TableModelBuilder {

        protected ItemSelectionModel m_selectionOrganization;
        protected ItemSelectionModel m_moveRole;

        public RoleTableModelBuilder(ItemSelectionModel selectionOrganization, ItemSelectionModel moveRole) {
            m_selectionOrganization = selectionOrganization;
            m_moveRole = moveRole;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            GenericOrganization orga = (GenericOrganization) m_selectionOrganization.getSelectedObject(state);

            return new RoleTableModel(table, state, orga, m_moveRole);
        }
    }

    protected class RoleTableModel implements TableModel {

        private TableColumnModel m_colModel;
        private RoleTable m_table;
        private PageState m_state;
        private OrganizationRoleCollection m_roles;
        private ItemSelectionModel m_moveRole;
        private OrganizationRole m_role;

        public RoleTableModel(Table table, PageState state, GenericOrganization orga, ItemSelectionModel moveRole) {
            m_colModel = table.getColumnModel();
            m_table = (RoleTable) table;
            m_state = state;
            m_roles = orga.getOrganizationRoles();
            m_moveRole = moveRole;
        }

        public int getColumnCount() {
            return m_colModel.size();
        }

        public boolean nextRow() {
            if (m_roles.next()) {
                m_role = m_roles.getOrganizationRole();
                return true;
            }
            return false;
        }

        public Object getElementAt(int columnIndex) {
            if (m_colModel == null) {
                return null;
            }

            TableColumn col = m_colModel.get(columnIndex);
            String colName = (String) col.getHeaderValue();

            if (COL_TITLE.equals(colName)) {
                return m_role.getRolename();
            } else if(COL_EDIT.equals(colName)) {
                return "edit";
            } else if(COL_DEL.equals(colName)) {
                return "delete";
            } else if(COL_MOVE.equals(colName)) {
                if(m_moveRole.getSelectedKey(m_state) == null) {
                    return "move";
                } else {
                    return "move below here";
                }
            }

            return null;
        }

        public Object getKeyAt(int columnIndex) {
            return m_role.getID();
        }
    }

    public class RoleTableCellRenderer extends LockableImpl implements TableCellRenderer {

        private boolean m_active;

        public RoleTableCellRenderer() {
            this(false);
        }

        public RoleTableCellRenderer(boolean active) {
            m_active = active;
        }

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            Component ret = null;
            SecurityManager sm = Utilities.getSecurityManager(state);
            ContentItem item = (ContentItem) m_selectionOrganization.getSelectedObject(state);

            boolean active = m_active && sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM, item);

            if (value instanceof Component) {
                ret = (Component) value;
            } else {
                if (value == null) {
                    ret = new Label("", false);
                } else {
                    if (active) {
                        ret = new ControlLink(value.toString());
                    } else {
                        ret = new Label(value.toString());
                    }
                }
            }

            return ret;
        }
    }
}

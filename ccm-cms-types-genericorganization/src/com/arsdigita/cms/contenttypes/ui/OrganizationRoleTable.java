package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleTable extends Table {

    private final static Logger logger = Logger.getLogger(OrganizationRoleTable.class);
    private OrganizationRoleSelectionModel m_roleModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_roleNameCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";
    protected final static String UP_EVENT = "up";
    protected final static String DOWN_EVENT = "down";

    public OrganizationRoleTable(ItemSelectionModel itemModel, OrganizationRoleSelectionModel roleModel) {
        super();
        this.m_itemModel = itemModel;
        this.m_roleModel = roleModel;
        addColumns();

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {

            @Override
            public Object initialValue(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = m_itemModel.getSelectedItem(state);
                Boolean val = new Boolean(sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM, item));
                return val;
            }
        };

        Label empty = new Label("There are no roles for this organization.");
        setEmptyView(empty);
        addTableActionListener(new OrganizationRoleTableActionListener());
        setRowSelectionModel(m_roleModel);
        setDefaultCellRenderer(new OrganizationRoleTableRenderer());
        setModelBuilder(new OrganizationRoleTableModelBuilder(itemModel));
    }

    protected void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        m_roleNameCol = new TableColumn(i, "Role");
        m_editCol = new TableColumn(++i, "Edit");
        m_delCol = new TableColumn(++i, "Delete");
        m_moveUpCol = new TableColumn(++i, "");
        m_moveDownCol = new TableColumn(++i, "");

        model.add(m_roleNameCol);
        model.add(m_editCol);
        model.add(m_delCol);
        model.add(m_moveUpCol);
        model.add(m_moveDownCol);
        setColumnModel(model);
    }

    private class OrganizationRoleTableRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            OrganizationRole role = (OrganizationRole) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((OrganizationRoleTableModelBuilder.OrganizationRoleTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            String url = role.getURI(state);
            if (column == m_roleNameCol.getModelIndex()) {
                ExternalLink extLink = new ExternalLink(role.getRolename(), url);
                return extLink;
            } else if (column == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    if (isSelected) {
                        return new Label(EDIT_EVENT, Label.BOLD);
                    } else {
                        return new ControlLink(EDIT_EVENT);
                    }
                } else {
                    return new Label(EDIT_EVENT);
                }
            } else if (column == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    return new ControlLink(DELETE_EVENT);
                } else {
                    return new Label(DELETE_EVENT);
                }
            } else if (column == m_moveUpCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isLast) {
                    Label downLabel = new Label(DOWN_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }
            } else if (column == m_moveDownCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isLast) {
                    Label downLabel = new Label(DOWN_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        }
    }

    private class OrganizationRoleTableActionListener implements TableActionListener {

        private OrganizationRole getOrganizationRole(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if(o instanceof String) {
                logger.debug("row key is a string: " + o);
                id = new BigDecimal((String)o);
            } else {
                id = (BigDecimal)e.getRowKey();
            }

            Assert.exists(id);
            OrganizationRole role;
            try {
                role = (OrganizationRole)DomainObjectFactory.newInstance(new OID(OrganizationRole.BASE_DATA_OBJECT_TYPE, id));
            } catch(DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(e);
            }
            return role;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            OrganizationRole role = getOrganizationRole(e);
            Assert.exists(role);

            if (col == m_roleNameCol.getModelIndex()) {
                //Nothing to do...
            } else if (col == m_editCol.getModelIndex()) {
                if(Boolean.TRUE.equals(m_editor.get(state))) {
                    logger.debug("setting organizationRoleModel to: " + role.getRolename());
                    m_roleModel.setSelectedObject(state, role);
                }
            } else if(col == m_delCol.getModelIndex()) {
                if(Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        logger.debug("About to delete");
                        m_roleModel.clearSelection(state);
                        role.delete();;
                    } catch(Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            } else if(col == m_moveUpCol.getModelIndex()) {
                m_roleModel.clearSelection(state);
                role.swapWithPrevious();
            } else if(col == m_moveDownCol.getModelIndex()) {
                m_roleModel.clearSelection(state);
                role.swapWithNext();
            }
        }

        public void headSelected(TableActionEvent e) {
            //Nothing
        }

    }
}
